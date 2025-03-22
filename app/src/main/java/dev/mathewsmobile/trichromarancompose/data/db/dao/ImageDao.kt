package dev.mathewsmobile.trichromarancompose.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.mathewsmobile.trichromarancompose.data.model.Image
import kotlinx.coroutines.flow.Flow

@Dao
interface ImageDao {
    @Query("SELECT * FROM image")
    fun getAll(): Flow<List<Image>>

    @Query("SELECT * FROM image WHERE id = :id")
    suspend fun getById(id: Int): Image?

    @Insert
    suspend fun insert(image: Image)

}