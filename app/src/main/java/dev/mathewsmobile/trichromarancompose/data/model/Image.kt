package dev.mathewsmobile.trichromarancompose.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Image(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val path: String,
    val takenAt: Long,
)
