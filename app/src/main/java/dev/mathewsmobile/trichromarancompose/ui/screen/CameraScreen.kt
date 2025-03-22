package dev.mathewsmobile.trichromarancompose.ui.screen

import androidx.camera.core.ImageCapture
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import dev.mathewsmobile.trichromarancompose.ui.component.ButtonRow
import dev.mathewsmobile.trichromarancompose.ui.component.CameraPreview
import dev.mathewsmobile.trichromarancompose.ui.component.Done
import dev.mathewsmobile.trichromarancompose.ui.component.Loading
import dev.mathewsmobile.trichromarancompose.ui.component.RoundedBorderBox
import dev.mathewsmobile.trichromarancompose.viewmodel.CameraUiState
import dev.mathewsmobile.trichromarancompose.viewmodel.CameraViewModel
import kotlinx.serialization.Serializable
import kotlin.math.roundToInt

@Serializable
object CameraScreenRoute

@Composable
fun CameraScreen(
    modifier: Modifier = Modifier,
    viewModel: CameraViewModel,
    onNavigateToGallery: () -> Unit
) {
    var sliderPosition by remember { mutableFloatStateOf(0f) }
    val context = LocalContext.current
    var showLoading by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()

    val imageCapture = remember {
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
    }

    Column(modifier.fillMaxSize()) {
        Box(modifier.weight(1f)) {
            when (uiState) {
                is CameraUiState.Capturing -> Loading()
                CameraUiState.Done -> Done()
                is CameraUiState.Error -> Error()
                CameraUiState.Ready -> {}
            }
            CameraPreview(modifier, imageCapture)
        }

        Column(
            modifier
                .background(Color.Black)
                .padding(WindowInsets.navigationBars.asPaddingValues()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier.height(16.dp))

            Slider(
                modifier = Modifier.padding(horizontal = 64.dp),
                valueRange = 0f..1f,
                value = sliderPosition,
                onValueChange = { sliderPosition = it }
            )

            Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                RoundedBorderBox(
                    modifier.offset {
                        IntOffset(-(sliderPosition * 100).roundToInt() + 128, 0)
                    },
                    borderColor = Color.Red
                )
                Spacer(modifier.width(8.dp))
                RoundedBorderBox(
                    modifier,
                    borderColor = Color.Green
                )
                Spacer(modifier.width(8.dp))
                RoundedBorderBox(
                    modifier.offset {
                        IntOffset((sliderPosition * 100).roundToInt() - 128, 0)
                    },
                    borderColor = Color.Blue
                )
            }
            Spacer(modifier.height(32.dp))

            ButtonRow(
                modifier = modifier,
                onCaptureImage = {
                    viewModel.captureImages(imageCapture, context, sliderPosition)
                },
                onViewImages = {
                    onNavigateToGallery()
                }
            )
            Spacer(modifier.height(16.dp))
        }
    }
}
