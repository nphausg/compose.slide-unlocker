/*
 * Copyright (c) 2025 nphausg.
 * All rights reserved.
 */

package com.nphausg.foundation.ui.draggle

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize

@Composable
fun DraggableTrack(
    modifier: Modifier,
    draggableOffset: Float,
    enabled: Boolean = false,
    startColor: () -> Color = { DraggableDefaults.Track.StartColor },
    stopColor: () -> Color = { DraggableDefaults.Track.StopColor },
    onSizeChanged: (IntSize) -> Unit,
    content: @Composable BoxScope.() -> Unit
) {

    val backgroundColor by animateColorAsState(
        targetValue = if (enabled) {
            val endOfColorChangeFraction = 0.6f
            val fraction = (draggableOffset / endOfColorChangeFraction).coerceIn(0f..1f)
            lerp(startColor(), stopColor(), fraction)
        } else {
            Color.Transparent
        },
        animationSpec = tween(500)
    )
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(DraggableDefaults.Track.Height)
            .onSizeChanged(onSizeChanged)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(percent = 80),
            ),
        content = content
    )
}