package com.nphausg.foundation.ui.draggle

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize

private fun calculateTrackColor(draggableOffset: Float): Color {
    val endOfColorChangeFraction = 0.4f
    val fraction = (draggableOffset / endOfColorChangeFraction).coerceIn(0f..1f)
    return lerp(DraggableDefaults.Track.StartColor, DraggableDefaults.Track.StopColor, fraction)
}

@Composable
fun DraggableTrack(
    modifier: Modifier,
    draggableOffset: Float,
    enabled: Boolean = false,
    onSizeChanged: (IntSize) -> Unit,
    content: @Composable BoxScope.() -> Unit
) {

    val backgroundColor by remember(enabled, draggableOffset) {
        derivedStateOf {
            if (enabled) {
                calculateTrackColor(draggableOffset)
            } else {
                Color.Transparent
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .onSizeChanged(onSizeChanged)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(percent = 80),
            )
            .padding(DraggableDefaults.Track.ContentPadding),
        content = content
    )
}