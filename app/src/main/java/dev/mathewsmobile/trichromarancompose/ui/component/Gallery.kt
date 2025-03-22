package dev.mathewsmobile.trichromarancompose.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.core.net.toUri
import coil.compose.AsyncImage
import dev.mathewsmobile.trichromarancompose.data.model.Image
import dev.mathewsmobile.trichromarancompose.ui.theme.TriChromaranComposeTheme

@Composable
fun Gallery(
    modifier: Modifier = Modifier,
    images: List<Image>,
    selectedImage: String? = null,
    onImageSelected: (Image) -> Unit = {}
) {
    Box(modifier.fillMaxSize()) {
        selectedImage?.let {
            Box(modifier.fillMaxSize().background(Color.Gray.copy(alpha = 0.55f))) {
                AsyncImage(
                    model = it.toUri(),
                    contentDescription = null,
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = modifier.fillMaxSize(),
        ) {
            item {
                Text(text = "Previous images")
            }
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
            emptyList()
        )
    }
}