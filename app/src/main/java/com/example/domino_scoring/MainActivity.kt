package com.example.domino_scoring

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.domino_scoring.ui.theme.Domino_scoringTheme
import org.opencv.android.OpenCVLoader
import org.opencv.core.MatOfPoint
import java.util.concurrent.Executors

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            Log.w("Permissions", "Camera permission was denied.")
        }
    }

    private val gameViewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        if (OpenCVLoader.initLocal()) {
            Log.i("OpenCV", "OpenCV loaded successfully")
        } else {
            Log.e("OpenCV", "OpenCV initialization failed")
        }

        setContent {
            Domino_scoringTheme {
                var showSettings by remember { mutableStateOf(false) }
                val players by gameViewModel.players

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (players.isEmpty()) {
                        PlayerSetupScreen(gameViewModel, modifier = Modifier.padding(innerPadding))
                    } else {
                        if (showSettings) {
                            SettingsScreen(gameViewModel, modifier = Modifier.padding(innerPadding)) {
                                showSettings = false
                            }
                        } else {
                            GameScreen(gameViewModel, modifier = Modifier.padding(innerPadding)) {
                                showSettings = true
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlayerSetupScreen(viewModel: GameViewModel, modifier: Modifier = Modifier) {
    var text by remember { mutableStateOf("") }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(value = text, onValueChange = { text = it }, label = { Text("Player Name") })
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            if (text.isNotBlank()) {
                viewModel.addPlayer(text)
                text = ""
            }
        }) {
            Text("Add Player")
        }
    }
}

@Composable
fun SettingsScreen(viewModel: GameViewModel, modifier: Modifier = Modifier, onDone: () -> Unit) {
    val currentRule by viewModel.scoringRule

    Column(modifier = modifier.padding(16.dp)) {
        Text("Scoring Rule", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        ScoringRule.entries.forEach { rule ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = currentRule == rule,
                    onClick = { viewModel.setScoringRule(rule) }
                )
                Text(rule.name.replace('_', ' ').lowercase())
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onDone) {
            Text("Done")
        }
    }
}

@Composable
fun GameScreen(viewModel: GameViewModel, modifier: Modifier = Modifier, onSettingsClicked: () -> Unit) {
    var detectedTiles by remember { mutableStateOf<List<Pair<Int, Int>>>(emptyList()) }
    var detectedContours by remember { mutableStateOf<List<MatOfPoint>>(emptyList()) }
    var imageWidth by remember { mutableIntStateOf(0) }
    var imageHeight by remember { mutableIntStateOf(0) }

    Box(modifier = modifier.fillMaxSize()) {
        CameraWithAnalysis { tiles, contours, width, height ->
            detectedTiles = tiles
            detectedContours = contours
            imageWidth = width
            imageHeight = height
        }

        AndroidView(
            factory = { OverlayView(it) },
            modifier = Modifier.fillMaxSize(),
            update = { view ->
                view.update(detectedContours, detectedTiles, imageWidth, imageHeight)
            }
        )

        Column(modifier = Modifier.padding(16.dp)) {
            Scoreboard(viewModel)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Detected Tiles: ${detectedTiles.joinToString()}")
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                Button(onClick = { viewModel.confirmTurn(detectedTiles) }) {
                    Text("Confirm Turn")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { viewModel.undo() }) {
                    Text("Undo")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onSettingsClicked) {
                    Text("Settings")
                }
            }
        }
    }
}

@Composable
fun CameraWithAnalysis(onAnalyzed: (List<Pair<Int, Int>>, List<MatOfPoint>, Int, Int) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val dominoAnalyzer = remember {
        DominoAnalyzer { tiles, contours, width, height ->
            onAnalyzed(tiles, contours, width, height)
        }
    }

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, dominoAnalyzer)
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalysis
                )
            } catch (exc: Exception) {
                Log.e("CameraWithAnalysis", "Use case binding failed", exc)
            }
            previewView
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun Scoreboard(viewModel: GameViewModel, modifier: Modifier = Modifier) {
    val players by viewModel.players
    val currentPlayerIndex by viewModel.currentPlayerIndex

    LazyColumn(modifier = modifier) {
        itemsIndexed(players) { index, player ->
            val isCurrentPlayer = index == currentPlayerIndex
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "${player.name}${if (isCurrentPlayer) " (Current)" else ""}")
                Text(text = "Score: ${player.score}")
            }
        }
    }
}
