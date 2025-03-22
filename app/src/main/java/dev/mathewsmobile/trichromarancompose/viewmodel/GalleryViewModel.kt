package dev.mathewsmobile.trichromarancompose.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.mathewsmobile.trichromarancompose.data.model.Image
import dev.mathewsmobile.trichromarancompose.data.usecase.GetAllImagesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GalleryUiState(
    val images: List<Image> = emptyList()
)

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val getAllImagesUseCase: GetAllImagesUseCase
) : ViewModel() {

    init {
        viewModelScope.launch {
            getAllImagesUseCase().collect { images ->
                Log.d("GalleryViewModel", "Got images: $images")
                _uiState.value = _uiState.value.copy(images = images)
            }
        }
    }

    private val _uiState = MutableStateFlow(GalleryUiState())
    val uiState: StateFlow<GalleryUiState> = _uiState
}
