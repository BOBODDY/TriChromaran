package dev.mathewsmobile.trichromarancompose.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.mathewsmobile.trichromarancompose.ui.theme.TriChromaranComposeTheme

@Composable
fun ButtonRow(
    modifier: Modifier = Modifier,
    onCaptureImage: () -> Unit,
    onViewImages: () -> Unit,
) {
    Box(modifier.fillMaxWidth().padding(16.dp)) {
        RoundedBorderBox(
            modifier = modifier
                .align(Alignment.CenterStart)
                .clickable {
                    onViewImages()
                },
        )

        Button(
            modifier = modifier.align(Alignment.Center),
            onClick = {
                onCaptureImage()
            }
        ) {
            Box(
                modifier
                    .clip(CircleShape)
                    .size(64.dp)
                    .background(Color.LightGray)
            )

        }
    }
}

@Preview
@Composable
fun ButtonRowPreview() {
    TriChromaranComposeTheme {
        ButtonRow(
            onCaptureImage = {},
            onViewImages = {}
        )
    }
}