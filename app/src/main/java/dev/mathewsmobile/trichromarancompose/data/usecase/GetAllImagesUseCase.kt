package dev.mathewsmobile.trichromarancompose.data.usecase

import android.util.Log
import dev.mathewsmobile.trichromarancompose.data.ImageRepository
import dev.mathewsmobile.trichromarancompose.data.model.Image
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class GetAllImagesUseCase @Inject constructor(
    private val imageRepository: ImageRepository
) {

    operator fun invoke(): Flow<List<Image>> {
        Log.d("GetAllImagesUseCase", "Getting all images")
        return imageRepository.getAllImages().onEach {
            Log.i("GetAllImagesUseCase", "Images: $it")

        }
    }
}