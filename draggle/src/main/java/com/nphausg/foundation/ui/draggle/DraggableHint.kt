package com.nphausg.foundation.ui.draggle

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp

fun calculateHintTextColor(draggableOffset: Float): Color {
    val endOfFadeFraction = 0.35f
    val fraction = (draggableOffset / endOfFadeFraction).coerceIn(0f..1f)
    return lerp(Color.White, Color.White.copy(alpha = 0f), fraction)
}

@Composable
fun DraggableHint(
    text: () -> String,
    draggableOffset: Float,
    modifier: Modifier = Modifier,
) {
    val hintTextColor by remember(draggableOffset) {
        derivedStateOf { calculateHintTextColor(draggableOffset) }
    }

    Text(
        text = text(),
        modifier = modifier,
        color = hintTextColor,
        style = MaterialTheme.typography.titleSmall
    )
}