package dev.mathewsmobile.trichromarancompose.ui.screen

import android.net.Uri
import android.widget.ImageButton
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import dev.mathewsmobile.trichromarancompose.ext.toTime
import kotlinx.serialization.Serializable

@Serializable
data class ViewImageRoute(
    val imagePath: String,
    val timestamp: Long
)

@Composable
fun ViewImageScreen(
    modifier: Modifier = Modifier,
    imagePath: String,
    timestamp: Long,
    onDismiss: () -> Unit
) {
    Box(
        modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .background(Color.DarkGray.copy(alpha = 0.55f)),
    ) {
        Button(modifier = modifier.align(Alignment.TopStart).padding(16.dp), onClick = { onDismiss() }) {
            Icon(Icons.Default.Close, tint = Color.White, contentDescription = "Close")
        }
        Column(
            modifier = modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (LocalInspectionMode.current) {
                Box(modifier.size(256.dp, 512.dp).background(Color.Blue))
            } else {
                AsyncImage(
                    model = imagePath.toUri(),
                    contentDescription = "View image",
                )
            }
            Spacer(modifier.height(16.dp))
            Text(
                text = "Taken at ${timestamp.toTime()}",
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview
@Composable
private fun ViewImagePreview() {
    ViewImageScreen(
        imagePath = "",
        timestamp = 0L,
        modifier = Modifier,
        onDismiss = {}
    )
}