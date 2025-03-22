package dev.mathewsmobile.trichromarancompose.ui.component

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import android.Manifest
import android.content.Context
import androidx.camera.core.ImageCapture
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner

@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    imageCapture: ImageCapture,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    // State for camera provider and preview
    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    val preview = Preview.Builder().build()
    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    val previewView = remember { PreviewView(context) }

    // ... (permission handling and LaunchedEffect as before) ...
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted, start camera
            cameraProvider?.let {
                startCamera(it, lifecycleOwner, cameraSelector, preview, previewView, imageCapture)
            }
        } else {
            // Handle permission denied
            // You might show a message to the user or disable camera features.
        }
    }

    //check for camera permission
    LaunchedEffect(key1 = Unit){
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            cameraProvider = getCameraProvider(context)
            startCamera(cameraProvider!!, lifecycleOwner, cameraSelector, preview, previewView, imageCapture)
        } else {
            //request camera permission
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    if (LocalInspectionMode.current) {
        Box(modifier.fillMaxSize().background(Color.Green))
    } else {
        AndroidView(
            factory = { previewView },
            modifier = modifier.fillMaxSize() // Fill available space
        )
    }
}
private fun startCamera(
    cameraProvider: ProcessCameraProvider,
    lifecycleOwner: LifecycleOwner,
    cameraSelector: CameraSelector,
    preview: Preview,
    previewView: PreviewView,
    imageCapture: ImageCapture
) {
    cameraProvider.unbindAll() // Unbind previous use cases

    try {
        preview.setSurfaceProvider(previewView.surfaceProvider)

        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageCapture
        )

    } catch (exc: Exception) {
        // Handle camera binding errors (e.g., camera in use by another app)
    }
}

private suspend fun getCameraProvider(context: Context): ProcessCameraProvider =
    suspendCoroutine { continuation ->
        ProcessCameraProvider.getInstance(context).also { cameraProvider ->
            cameraProvider.addListener({
                continuation.resume(cameraProvider.get())
            }, ContextCompat.getMainExecutor(context))
        }
    }