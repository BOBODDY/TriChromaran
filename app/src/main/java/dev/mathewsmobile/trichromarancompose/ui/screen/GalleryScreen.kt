package dev.mathewsmobile.trichromarancompose.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import dev.mathewsmobile.trichromarancompose.data.model.Image
import dev.mathewsmobile.trichromarancompose.ui.component.Gallery
import dev.mathewsmobile.trichromarancompose.viewmodel.GalleryViewModel
import kotlinx.serialization.Serializable

@Serializable
object GalleryScreenRoute

@Composable
fun GalleryScreen(
    modifier: Modifier = Modifier,
    viewModel: GalleryViewModel,
    onViewImage: (Image) -> Unit
) {
    val uiState = viewModel.uiState.collectAsState()

    Gallery(
        modifier,
        uiState.value.images,
        onImageSelected = {
            onViewImage(it)
        },
    )
}
