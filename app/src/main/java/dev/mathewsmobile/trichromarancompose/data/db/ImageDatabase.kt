package dev.mathewsmobile.trichromarancompose.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.mathewsmobile.trichromarancompose.data.db.dao.ImageDao
import dev.mathewsmobile.trichromarancompose.data.model.Image

@Database(entities = [Image::class], version = 1)
abstract class ImageDatabase : RoomDatabase() {
    abstract fun imageDao(): ImageDao
}