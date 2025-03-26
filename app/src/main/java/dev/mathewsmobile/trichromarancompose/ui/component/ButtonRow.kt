package dev.mathewsmobile.trichromarancompose.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import dev.mathewsmobile.trichromarancompose.ui.theme.TriChromaranComposeTheme

@Composable
fun ButtonRow(
    modifier: Modifier = Modifier,
    shutterEnabled: Boolean,
    isLoading: Boolean,
    mostRecentImage: String? = null,
    onCaptureImage: () -> Unit,
    onViewImages: () -> Unit,
) {
    Box(modifier
        .fillMaxWidth()
        .padding(16.dp)) {
        Box(modifier
            .align(Alignment.CenterStart)
        ) {
            RoundedBorderBox(
                modifier = modifier
                    .clickable {
                        onViewImages()
                    },
            ) {
                AsyncImage(
                    modifier = modifier.fillMaxSize(),
                    model = mostRecentImage,
                    contentDescription = "Most recent image",
                )
            }
            if (isLoading) {
                CircularProgressIndicator()
            }
        }

        Button(
            modifier = modifier.align(Alignment.Center),
            enabled = shutterEnabled,
            onClick = {
                onCaptureImage()
            }
        ) {
//            Box(
//                modifier
//                    .clip(CircleShape)
//                    .size(64.dp)
//                    .background(Color.LightGray)
//            )

            if (isLoading) {
                CircularProgressIndicator()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ButtonRowPreview() {
    TriChromaranComposeTheme {
        ButtonRow(
            shutterEnabled = true,
            isLoading = false,
            onCaptureImage = {},
            onViewImages = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ButtonRowLoadingPreview() {
    TriChromaranComposeTheme {
        ButtonRow(
            shutterEnabled = false,
            isLoading = true,
            onCaptureImage = {},
            onViewImages = {}
        )
    }
}