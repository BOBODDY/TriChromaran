package dev.mathewsmobile.trichromarancompose.ui.component

import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.mathewsmobile.trichromarancompose.data.model.Image

@Composable
fun Done(modifier: Modifier = Modifier) {
    Overlay {
        Icon(Icons.Default.Check, contentDescription = null)
    }
}