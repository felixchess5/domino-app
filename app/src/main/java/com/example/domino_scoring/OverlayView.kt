package com.example.domino_scoring

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import org.opencv.core.MatOfPoint
import org.opencv.core.Point

class OverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var contours = listOf<MatOfPoint>()
    private var results = listOf<Pair<Int, Int>>()
    private var imageWidth = 0
    private var imageHeight = 0

    private val contourPaint = Paint().apply {
        color = Color.GREEN
        style = Paint.Style.STROKE
        strokeWidth = 4f
    }

    private val textPaint = Paint().apply {
        color = Color.RED
        textSize = 40f
    }

    fun update(
        contours: List<MatOfPoint>,
        results: List<Pair<Int, Int>>,
        imageWidth: Int,
        imageHeight: Int
    ) {
        this.contours = contours
        this.results = results
        this.imageWidth = imageWidth
        this.imageHeight = imageHeight
        invalidate() // Redraw the view
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (contours.isEmpty() || imageWidth == 0 || imageHeight == 0) return

        val scaleX = width.toFloat() / imageWidth
        val scaleY = height.toFloat() / imageHeight

        for ((index, contour) in contours.withIndex()) {
            val path = Path()
            val points = contour.toArray()

            if (points.isNotEmpty()) {
                path.moveTo(points[0].x.toFloat() * scaleX, points[0].y.toFloat() * scaleY)
                for (i in 1 until points.size) {
                    path.lineTo(points[i].x.toFloat() * scaleX, points[i].y.toFloat() * scaleY)
                }
                path.close()
                canvas.drawPath(path, contourPaint)

                if (index < results.size) {
                    val result = results[index]
                    val center = getContourCenter(points)
                    canvas.drawText(
                        "${result.first}|${result.second}",
                        center.x.toFloat() * scaleX,
                        center.y.toFloat() * scaleY,
                        textPaint
                    )
                }
            }
        }
    }

    private fun getContourCenter(points: Array<Point>): Point {
        var sumX = 0.0
        var sumY = 0.0
        for (point in points) {
            sumX += point.x
            sumY += point.y
        }
        return Point(sumX / points.size, sumY / points.size)
    }
}
