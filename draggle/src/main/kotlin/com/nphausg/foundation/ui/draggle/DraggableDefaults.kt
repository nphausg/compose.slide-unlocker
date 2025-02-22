/*
 * Copyright (c) 2025 nphausg.
 * All rights reserved.
 */
package com.nphausg.foundation.ui.draggle

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nphausg.foundation.ui.draggle.DraggableDefaults.Track

object DraggableDefaults {
    object Thumb {
        val Size = 40.dp
        val IconPadding = 8.dp
    }

    object Track {
        val Height = 56.dp
        val ContentPadding = PaddingValues(8.dp)
        val StartColor = Color(0xFF4CAF50)
        val StopColor = Color(0xFF117322)
    }
}

val DraggableDefaults.Thumb.StartIcon: @Composable () -> Unit
    get() = {
        Box(
            modifier = Modifier
                .size(Size)
                .background(color = Color.White, shape = CircleShape)
                .padding(IconPadding),
        ) {
            Icon(
                tint = Track.StopColor,
                contentDescription = "",
                imageVector = Icons.Filled.PlayArrow
            )
        }
    }

val DraggableDefaults.Thumb.EndIcon: @Composable () -> Unit
    get() = {
        Box(
            modifier = Modifier
                .size(Size)
                .background(color = Color.White, shape = CircleShape)
                .padding(IconPadding),
        ) {
            CircularProgressIndicator(
                modifier = Modifier.padding(2.dp),
                color = Track.StartColor,
                strokeWidth = 2.dp
            )
        }
    }