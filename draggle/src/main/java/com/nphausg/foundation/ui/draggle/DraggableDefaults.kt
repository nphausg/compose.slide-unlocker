package com.nphausg.foundation.ui.draggle

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object DraggableDefaults {
    object Thumb {
        val Size = 40.dp
    }

    object Track {
        val Height = 56.dp
        val ContentPadding = PaddingValues(8.dp)
        val StartColor = Color(0xFF4CAF50)
        val StopColor = Color(0xFF117322)
    }
}