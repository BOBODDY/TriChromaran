package dev.mathewsmobile.trichromarancompose.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun Error(modifier: Modifier = Modifier) {
    Overlay {
        Icon(Icons.Default.Close, contentDescription = null)
    }
}