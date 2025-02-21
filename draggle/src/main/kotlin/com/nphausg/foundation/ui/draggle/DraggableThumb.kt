/*
 * Copyright (c) 2025 nphausg.
 * All rights reserved.
 */
package com.nphausg.foundation.ui.draggle

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * A composable function that displays a draggable thumb with an icon.
 * The icon changes based on the loading state.
 *
 * @param isLoading A boolean indicating whether the thumb is in a loading state.
 * @param startIcon A composable function that provides the start icon when not loading.
 * @param endIcon A composable function that provides the end icon when loading.
 */
@Composable
fun DraggableThumb(
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    startIcon: @Composable () -> Unit = DraggableDefaults.Thumb.StartIcon,
    endIcon: @Composable () -> Unit = DraggableDefaults.Thumb.EndIcon
) {
    Box(modifier = modifier.size(DraggableDefaults.Thumb.Size)) {
        if (isLoading) {
            endIcon()
        } else {
            startIcon()
        }
    }
}

/**
 * A preview composable function for DraggableThumb.
 */
@Composable
@Preview
private fun DraggableThumbPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = "Normal")
            Spacer(modifier = Modifier.weight(1f))
            DraggableThumb(isLoading = false)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = "Loading")
            Spacer(modifier = Modifier.weight(1f))
            DraggableThumb(isLoading = true)
        }
    }
}