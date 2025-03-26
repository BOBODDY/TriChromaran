package dev.mathewsmobile.trichromarancompose.data.usecase

import dev.mathewsmobile.trichromarancompose.data.ImageRepository
import dev.mathewsmobile.trichromarancompose.data.model.Image
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLastImageUseCase @Inject constructor(
    private val imageRepository: ImageRepository
) {
    operator fun invoke(): Flow<Image?> {
        return imageRepository.getLastImage()
    }
}