package dev.mathewsmobile.trichromarancompose.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.mathewsmobile.trichromarancompose.ui.theme.TriChromaranComposeTheme

@Composable
fun RoundedBorderBox(
    modifier: Modifier = Modifier,
    borderColor: Color = Color.LightGray,
    cornerRadius: Int = 16,
    borderWidth: Int = 4,
    boxSize: Int = 50,
    content: @Composable () -> Unit = {}
) {
    Box(modifier.padding(16.dp)) {
        val shape = RoundedCornerShape(cornerRadius.dp)
        Box(
            modifier = modifier
                .size(boxSize.dp)
                .border(
                    width = borderWidth.dp,
                    color = borderColor,
                    shape = shape
                )
        ) {
            Box(modifier.clip(shape)) {
                content()
            }
        }
    }
}

@Preview
@Composable
private fun RoundedBorderBoxPreviewNoContent() {
    TriChromaranComposeTheme {
        RoundedBorderBox()

    }
}

@Preview
@Composable
private fun RoundedBorderBoxPreviewContent() {
    TriChromaranComposeTheme {
        RoundedBorderBox() {
            Box(Modifier
                .background(Color.Blue)
                .fillMaxSize())
        }

    }
}