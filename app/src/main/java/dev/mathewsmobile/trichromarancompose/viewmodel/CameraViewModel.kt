package dev.mathewsmobile.trichromarancompose.viewmodel

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.mathewsmobile.trichromarancompose.ext.Channel
import dev.mathewsmobile.trichromarancompose.ext.combineRGBChannels
import dev.mathewsmobile.trichromarancompose.data.usecase.AddImageUseCase
import dev.mathewsmobile.trichromarancompose.ext.saveColorChannelToImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.concurrent.Executors
import javax.inject.Inject
import kotlin.coroutines.resumeWithException

sealed interface CameraUiState {

    data object Ready : CameraUiState

    data object Capturing : CameraUiState

    data object Done : CameraUiState

    data class Error(val message: String) : CameraUiState
}

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val addImageUseCase: AddImageUseCase
) : ViewModel() {

    private val _uiState: MutableStateFlow<CameraUiState> = MutableStateFlow(CameraUiState.Ready)
    val uiState: StateFlow<CameraUiState> = _uiState

    // Create a dedicated Executor for CameraX operations.  A single thread is often sufficient.
    private val cameraExecutor = Executors.newSingleThreadExecutor()

    fun captureImages(imageCapture: ImageCapture, context: Context, delayFactor: Float) {
        takePicturesAndCombine(
            imageCapture,
            context,
            delayFactor,
            viewModelScope
        )
    }

    fun takePicturesAndCombine(
        imageCapture: ImageCapture,
        context: Context,
        delayFactor: Float,
        viewModelScope: CoroutineScope
    ) {
        val delay: Long = (1000L * delayFactor).toLong()

        viewModelScope.launch {
            _uiState.emit(CameraUiState.Capturing)

            // Await all Deferred results. This ensures all images are captured before combining.
            val redBitmap = takePictureAsync(imageCapture, Channel.Red)
            delay(delay)
            val greenBitmap = takePictureAsync(imageCapture, Channel.Green)
            delay(delay)
            val blueBitmap = takePictureAsync(imageCapture, Channel.Blue)

            // Check if any of the bitmaps are null (capture failed)
            if (redBitmap == null || greenBitmap == null || blueBitmap == null) {
                // Handle the error.  Perhaps log, show a message to the user, etc.
                Log.e("CameraViewModel", "One or more image captures failed.")
                return@launch // Exit the coroutine
            }
            // Combine the bitmaps on the IO dispatcher.
            val combinedBitmap = withContext(Dispatchers.IO) {
                combineRGBChannels(redBitmap, greenBitmap, blueBitmap)
            }

            // Save the combined bitmap on the IO dispatcher.
            saveImageToExternalStorage(context, combinedBitmap)
        }
    }

    // Helper function to capture a single picture and return a Deferred<Bitmap?>.
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun takePictureAsync(imageCapture: ImageCapture, channel: Channel): Bitmap? {
        return suspendCancellableCoroutine { continuation ->
            imageCapture.takePicture(
                cameraExecutor, // Use our dedicated background executor
                object : ImageCapture.OnImageCapturedCallback() {
                    override fun onCaptureSuccess(image: ImageProxy) {
                        viewModelScope.launch {
                            val bitmap = saveColorChannelToImage(image, channel)
                            continuation.resume(bitmap) { continuation.resumeWithException(it) } // Resume the coroutine with the Bitmap
                        }
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Log.e(
                            "CameraViewModel",
                            "Image capture failed: ${exception.message}",
                            exception
                        )
                        continuation.resumeWithException(exception) // Or, use resumeWithException(exception) to propagate the error
                    }
                }
            )
        }
    }

    suspend fun emitFinishedThenReady(success: Boolean) {
        val state = if (success) CameraUiState.Done else CameraUiState.Error("Something went wrong")
        _uiState.emit(state)
        delay(500)
        _uiState.emit(CameraUiState.Ready)
    }

    suspend fun saveImageToExternalStorage(context: Context, bitmap: Bitmap): Uri? {
        Log.d("CameraViewModel", "Saving image to external storage")
        return withContext(Dispatchers.IO) {
            // Get date as milliseconds
            val date = System.currentTimeMillis()
            val filename = "trichromaran-picture-$date.jpg"
            val mimeType = "image/jpeg"

            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                // set the relative path of the image to a trichromaran directory within the pictures directory
                put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    Environment.DIRECTORY_PICTURES + "/trichromaran"
                )
            }

            val resolver = context.contentResolver
            var uri =
                resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

            return@withContext try {
                uri?.let {
                    resolver.openOutputStream(it)?.use { outputStream ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                    }
                    addImageUseCase(uri.toString())
                    Log.d("CameraViewModel", "Image saved to ${uri.path}")
                    emitFinishedThenReady(true)
                }
                uri
            } catch (e: IOException) {
                // Handle error
                Log.e("CameraViewModel", "Error saving image: ${e.message}", e)
                uri?.let { resolver.delete(it, null, null) }
                emitFinishedThenReady(false)
                null
            }
        }
    }
}