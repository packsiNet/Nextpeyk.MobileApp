package ir.nextpeyk.android.ui.screens.scanner

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

private val ScanGreen = Color(0xFF00E676)
private val ScanGreenDim = Color(0x4400E676)

@Composable
fun BarcodeScannerScreen(
    onBarcodeScanned: (String) -> Unit,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasCameraPermission = granted }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    val scanned = remember { AtomicBoolean(false) }

    val scanLineTransition = rememberInfiniteTransition(label = "scan")
    val scanLineProgress by scanLineTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "scanLine",
    )

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {

        if (hasCameraPermission) {
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx).apply {
                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    }
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }
                        val executor = Executors.newSingleThreadExecutor()
                        val options = BarcodeScannerOptions.Builder()
                            .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                            .build()
                        val barcodeScanner = BarcodeScanning.getClient(options)
                        val imageAnalysis = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()
                        imageAnalysis.setAnalyzer(executor, BarcodeAnalyzer(barcodeScanner, scanned, onBarcodeScanned))
                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                CameraSelector.DEFAULT_BACK_CAMERA,
                                preview,
                                imageAnalysis,
                            )
                        } catch (_: Exception) {}
                    }, ContextCompat.getMainExecutor(ctx))
                    previewView
                },
                modifier = Modifier.fillMaxSize(),
            )
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen },
        ) {
            val frameWidth = size.width * 0.74f
            val frameHeight = frameWidth * 0.62f
            val frameLeft = (size.width - frameWidth) / 2f
            val frameTop = (size.height - frameHeight) / 2f - size.height * 0.06f
            val cornerRadius = 18.dp.toPx()

            drawRect(Color.Black.copy(alpha = 0.65f))
            drawRoundRect(
                color = Color.Transparent,
                topLeft = Offset(frameLeft, frameTop),
                size = Size(frameWidth, frameHeight),
                cornerRadius = CornerRadius(cornerRadius),
                blendMode = BlendMode.Clear,
            )

            drawScanCorners(frameLeft, frameTop, frameWidth, frameHeight, cornerRadius)

            val lineY = frameTop + frameHeight * scanLineProgress
            val gradient = androidx.compose.ui.graphics.Brush.horizontalGradient(
                colors = listOf(Color.Transparent, ScanGreen, ScanGreen, Color.Transparent),
                startX = frameLeft + frameWidth * 0.1f,
                endX = frameLeft + frameWidth * 0.9f,
            )
            drawRect(
                brush = gradient,
                topLeft = Offset(frameLeft + frameWidth * 0.05f, lineY - 1.dp.toPx()),
                size = Size(frameWidth * 0.9f, 2.dp.toPx()),
            )
            drawRect(
                brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                    colors = listOf(Color.Transparent, ScanGreenDim, ScanGreenDim, Color.Transparent),
                    startX = frameLeft + frameWidth * 0.1f,
                    endX = frameLeft + frameWidth * 0.9f,
                ),
                topLeft = Offset(frameLeft + frameWidth * 0.05f, lineY - 6.dp.toPx()),
                size = Size(frameWidth * 0.9f, 12.dp.toPx()),
            )
        }

        IconButton(
            onClick = onBack,
            modifier = Modifier
                .padding(top = 52.dp, start = 12.dp)
                .align(Alignment.TopStart)
                .size(44.dp)
                .background(Color.White.copy(alpha = 0.12f), CircleShape),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "بازگشت",
                tint = Color.White,
            )
        }

        Text(
            text = "بارکد را در کادر قرار دهید",
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = (320).dp),
        )

        if (!hasCameraPermission) {
            Text(
                text = "دسترسی به دوربین لازم است.\nلطفاً از تنظیمات اجازه دهید.",
                color = Color.White,
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 32.dp),
            )
        }
    }
}

private fun DrawScope.drawScanCorners(
    left: Float,
    top: Float,
    width: Float,
    height: Float,
    cornerRadius: Float,
) {
    val cornerLen = 32.dp.toPx()
    val strokeWidth = 3.5.dp.toPx()
    val color = ScanGreen
    val cap = StrokeCap.Round

    // Top-left
    drawLine(color, Offset(left, top + cornerLen), Offset(left, top + cornerRadius), strokeWidth = strokeWidth, cap = cap)
    drawLine(color, Offset(left + cornerRadius, top), Offset(left + cornerLen, top), strokeWidth = strokeWidth, cap = cap)
    // Top-right
    drawLine(color, Offset(left + width - cornerLen, top), Offset(left + width - cornerRadius, top), strokeWidth = strokeWidth, cap = cap)
    drawLine(color, Offset(left + width, top + cornerRadius), Offset(left + width, top + cornerLen), strokeWidth = strokeWidth, cap = cap)
    // Bottom-left
    drawLine(color, Offset(left, top + height - cornerLen), Offset(left, top + height - cornerRadius), strokeWidth = strokeWidth, cap = cap)
    drawLine(color, Offset(left + cornerRadius, top + height), Offset(left + cornerLen, top + height), strokeWidth = strokeWidth, cap = cap)
    // Bottom-right
    drawLine(color, Offset(left + width - cornerLen, top + height), Offset(left + width - cornerRadius, top + height), strokeWidth = strokeWidth, cap = cap)
    drawLine(color, Offset(left + width, top + height - cornerRadius), Offset(left + width, top + height - cornerLen), strokeWidth = strokeWidth, cap = cap)
}

private class BarcodeAnalyzer(
    private val scanner: com.google.mlkit.vision.barcode.BarcodeScanner,
    private val scanned: AtomicBoolean,
    private val onScanned: (String) -> Unit,
) : ImageAnalysis.Analyzer {

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: androidx.camera.core.ImageProxy) {
        if (scanned.get()) {
            imageProxy.close()
            return
        }
        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            imageProxy.close()
            return
        }
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                barcodes.firstOrNull()?.rawValue?.let { value ->
                    if (scanned.compareAndSet(false, true)) {
                        onScanned(value)
                    }
                }
            }
            .addOnCompleteListener { imageProxy.close() }
    }
}
