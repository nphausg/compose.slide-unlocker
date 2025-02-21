package com.nphausg.foundation.ui.draggle

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

private val ICON_PADDING = 8.dp

@Composable
fun DraggableThumb(
    isLoading: Boolean,
    startIcon: @Composable BoxScope.() -> Unit = {
        Icon(
            tint = DraggableDefaults.Track.StartColor,
            contentDescription = "",
            imageVector = Icons.Filled.PlayArrow
        )
    },
    endIcon: @Composable BoxScope.() -> Unit = {
        CircularProgressIndicator(
            modifier = Modifier.padding(2.dp),
            color = DraggableDefaults.Track.StartColor,
            strokeWidth = 2.dp
        )
    },
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(DraggableDefaults.Thumb.Size)
            .background(color = Color.White, shape = CircleShape)
            .padding(ICON_PADDING),
    ) {
        if (isLoading) {
            endIcon()
        } else {
            startIcon()
        }
    }
}

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