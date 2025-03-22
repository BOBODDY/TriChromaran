package dev.mathewsmobile.trichromarancompose.ui.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun RoundedBorderBox(
    modifier: Modifier = Modifier,
    borderColor: Color = Color.LightGray,
    cornerRadius: Int = 16,
    borderWidth: Int = 4,
    boxSize: Int = 50
) {
    Box(
        modifier = modifier
            .size(boxSize.dp)
            .border(
                width = borderWidth.dp,
                color = borderColor,
                shape = RoundedCornerShape(cornerRadius.dp)
            )
            .padding(16.dp)
    )
}