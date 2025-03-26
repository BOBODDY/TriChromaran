package dev.mathewsmobile.trichromarancompose.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import dev.mathewsmobile.trichromarancompose.data.model.Image
import dev.mathewsmobile.trichromarancompose.ui.theme.TriChromaranComposeTheme

@Composable
fun Gallery(
    modifier: Modifier = Modifier,
    images: List<Image>,
    onImageSelected: (Image) -> Unit = {}
) {
    Column(modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
        Text(
            text = "Photo history",
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            fontSize = 32.sp
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = modifier.fillMaxSize(),
        ) {
            items(images) { image ->
                if (LocalInspectionMode.current) {
                    Box(
                        modifier = modifier
                            .fillMaxSize()
                            .background(Color.Blue)
                    )
                } else {
                    AsyncImage(
                        model = image.path.toUri(),
                        contentDescription = null,
                        modifier = Modifier.padding(8.dp).clickable {
                            onImageSelected(image)
                        },
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun GalleryPreview() {
    TriChromaranComposeTheme {
        Gallery(
            Modifier.background(Color.White),
            listOf(
                Image(path = "", takenAt = 1234567L),
                Image(path = "", takenAt = 1234567L),
                Image(path = "", takenAt = 1234567L),
                Image(path = "", takenAt = 1234567L)
            )
        )
    }
}