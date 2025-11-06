package com.example.domino_scoring

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.MatOfKeyPoint
import org.opencv.core.MatOfPoint
import org.opencv.core.MatOfPoint2f
import org.opencv.core.Point
import org.opencv.core.Rect
import org.opencv.core.Size
import org.opencv.features2d.SimpleBlobDetector
import org.opencv.features2d.SimpleBlobDetector_Params
import org.opencv.imgproc.Imgproc
import java.nio.ByteBuffer
import kotlin.math.max
import kotlin.math.min

class DominoAnalyzer(
    private val onTiles: (List<Pair<Int, Int>>, Int) -> Unit
) : ImageAnalysis.Analyzer {

    @Volatile
    var isEnabled = true

    // Helper to convert ImageProxy to Mat
    private fun imageProxyToMat(image: ImageProxy): Mat {
        val planes = image.planes
        val yBuffer: ByteBuffer = planes[0].buffer
        val uBuffer: ByteBuffer = planes[1].buffer
        val vBuffer: ByteBuffer = planes[2].buffer
        val ySize: Int = yBuffer.remaining()
        val uSize: Int = uBuffer.remaining()
        val vSize: Int = vBuffer.remaining()
        val nv21 = ByteArray(ySize + uSize + vSize)
        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)
        val yuv = Mat(image.height + image.height / 2, image.width, CvType.CV_8UC1)
        yuv.put(0, 0, nv21)
        val mat = Mat()
        Imgproc.cvtColor(yuv, mat, Imgproc.COLOR_YUV2BGR_NV21, 3)
        yuv.release()
        Core.rotate(mat, mat, Core.ROTATE_90_CLOCKWISE)
        return mat
    }

    private fun warpToSize(mat: Mat, approx: MatOfPoint2f, size: Size): Mat {
        val quad = approx.toArray().map { it -> Point(it.x, it.y) }.toMutableList()
        val s = quad.map { it.x + it.y }
        val diff = quad.map { it.y - it.x }

        val tl = quad[s.indexOf(s.minOrNull())]
        val br = quad[s.indexOf(s.maxOrNull())]
        val tr = quad[diff.indexOf(diff.minOrNull())]
        val bl = quad[diff.indexOf(diff.maxOrNull())]

        val src = MatOfPoint2f(tl, tr, br, bl)
        val dst = MatOfPoint2f(
            Point(0.0, 0.0),
            Point(size.width - 1, 0.0),
            Point(size.width - 1, size.height - 1),
            Point(0.0, size.height - 1)
        )
        val M = Imgproc.getPerspectiveTransform(src, dst)
        val warped = Mat()
        Imgproc.warpPerspective(mat, warped, M, size)
        src.release()
        dst.release()
        M.release()
        return warped
    }

    private fun countBlobs(roiBin: Mat): Int {
        val kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, Size(3.0, 3.0))
        val clean = Mat()
        Imgproc.morphologyEx(roiBin, clean, Imgproc.MORPH_OPEN, kernel, Point(-1.0, -1.0), 1)

        val params = SimpleBlobDetector_Params()
        params.filterByArea = true
        params.minArea = 12f
        params.maxArea = 1500f
        params.filterByCircularity = true
        params.minCircularity = 0.6f
        params.filterByConvexity = true
        params.minConvexity = 0.7f
        params.filterByInertia = true
        params.minInertiaRatio = 0.3f

        val detector = SimpleBlobDetector.create(params)
        val keypoints = MatOfKeyPoint()
        detector.detect(clean, keypoints)
        clean.release()
        kernel.release()
        val count = keypoints.toArray().size
        keypoints.release()
        return count
    }


    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        if (!isEnabled) {
            imageProxy.close()
            return
        }

        val mat = imageProxyToMat(imageProxy)
        imageProxy.close() // Close early to avoid holding up the camera

        val gray = Mat()
        Imgproc.cvtColor(mat, gray, Imgproc.COLOR_BGR2GRAY)
        Imgproc.GaussianBlur(gray, gray, Size(5.0, 5.0), 0.0)
        val bin = Mat()
        Imgproc.threshold(
            gray, bin, 0.0, 255.0,
            Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_OTSU
        )

        val contours = ArrayList<MatOfPoint>()
        val hierarchy = Mat()
        Imgproc.findContours(
            bin.clone(), contours, hierarchy,
            Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE
        )

        val results = mutableListOf<Pair<Int, Int>>()

        for (c in contours) {
            val area = Imgproc.contourArea(c)
            if (area < 2000.0) {
                c.release()
                continue
            }

            val matOfPoint2f = MatOfPoint2f(*c.toArray())
            val peri = Imgproc.arcLength(matOfPoint2f, true)
            val approx = MatOfPoint2f()
            Imgproc.approxPolyDP(matOfPoint2f, approx, 0.02 * peri, true)

            if (approx.total().toInt() == 4) {
                val rect = Imgproc.minAreaRect(approx)
                val w = rect.size.width
                val h = rect.size.height
                val ratio = max(w, h) / max(1.0, min(w, h))

                if (ratio >= 1.6 && ratio <= 2.6) {
                    val warped = warpToSize(mat, approx, Size(200.0, 100.0))
                    val wGray = Mat()
                    Imgproc.cvtColor(warped, wGray, Imgproc.COLOR_BGR2GRAY)
                    val wBin = Mat()
                    Imgproc.threshold(
                        wGray, wBin, 0.0, 255.0,
                        Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_OTSU
                    )

                    val left = wBin.submat(Rect(0, 0, 100, 100))
                    val right = wBin.submat(Rect(100, 0, 100, 100))
                    val a = countBlobs(left)
                    val b = countBlobs(right)

                    val aa = min(a, b)
                    val bb = max(a, b)
                    results.add(aa to bb)

                    warped.release()
                    wGray.release()
                    wBin.release()
                    left.release()
                    right.release()
                }
            }
            c.release()
            matOfPoint2f.release()
            approx.release()
        }

        val total = results.sumOf { it.first + it.second }
        onTiles(results, total)

        // Release all Mats
        mat.release()
        gray.release()
        bin.release()
        hierarchy.release()
    }
}
