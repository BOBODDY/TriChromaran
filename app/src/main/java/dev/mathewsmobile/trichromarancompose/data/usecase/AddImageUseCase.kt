package dev.mathewsmobile.trichromarancompose.data.usecase

import android.util.Log
import dev.mathewsmobile.trichromarancompose.data.ImageRepository
import dev.mathewsmobile.trichromarancompose.data.model.Image
import java.time.Instant
import javax.inject.Inject

class AddImageUseCase @Inject constructor(
    private val imageRepository: ImageRepository
) {
    suspend operator fun invoke(image: Image) {
        Log.d("AddImageUseCase", "Adding image: $image")
        imageRepository.insertImage(image)
    }

    suspend operator fun invoke(path: String) {
        Log.d("AddImageUseCase", "Creating image for $path and saving in database")
        val image = Image(
            path = path,
            takenAt = Instant.now().toEpochMilli()
        )
        imageRepository.insertImage(image)
    }
}