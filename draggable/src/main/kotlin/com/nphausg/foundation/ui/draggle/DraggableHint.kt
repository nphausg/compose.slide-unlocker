/*
 * Copyright (c) 2025 nphausg.
 * All rights reserved.
 */
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

/**
 * Calculates the hint text color based on the draggable offset.
 *
 * @param draggableOffset The current offset of the draggable element.
 * @return The color of the hint text.
 */
fun calculateHintTextColor(draggableOffset: Float): Color {
    val startColor = Color.White
    val endColor = Color.White.copy(alpha = 0f)
    val endOfFadeFraction = 0.35f
    val fraction = (draggableOffset / endOfFadeFraction).coerceIn(0f..1f)
    return lerp(startColor, endColor, fraction)
}

/**
 * A composable function that displays a hint text which fades out as the draggable offset increases.
 *
 * @param text A lambda function that provides the hint text.
 * @param draggableOffset The current offset of the draggable element.
 * @param modifier The modifier to be applied to the Text composable.
 */
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