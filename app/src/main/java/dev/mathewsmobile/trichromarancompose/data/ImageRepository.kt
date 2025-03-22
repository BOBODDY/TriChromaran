package dev.mathewsmobile.trichromarancompose.data

import dev.mathewsmobile.trichromarancompose.data.db.dao.ImageDao
import dev.mathewsmobile.trichromarancompose.data.model.Image
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ImageRepository @Inject constructor(
    private val imageDao: ImageDao
) {

    suspend fun insertImage(image: Image) {
        imageDao.insert(image)
    }

    fun getAllImages(): Flow<List<Image>> {
        return imageDao.getAll()
    }

    suspend fun getImageById(id: Int): Image? {
        return imageDao.getById(id)
    }

}