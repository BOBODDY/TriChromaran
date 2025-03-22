package dev.mathewsmobile.trichromarancompose.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.mathewsmobile.trichromarancompose.data.db.ImageDatabase

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    fun providesImageDatabase(@ApplicationContext appContext: Context): ImageDatabase {
        return Room.databaseBuilder(
            appContext,
            ImageDatabase::class.java,
            "images"
        ).build()
    }

    @Provides
    fun providesImageDao(imageDatabase: ImageDatabase) = imageDatabase.imageDao()
}