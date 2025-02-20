@file:OptIn(ExperimentalFoundationApi::class)

package com.nphausg.foundation.ui.draggle

import androidx.compose.animation.core.tween
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import kotlin.math.roundToInt

enum class Anchor { Start, End }

@Composable
fun DraggableUnlocker(
    isLoading: Boolean,
    hintText: () -> String,
    onUnlock: () -> Unit,
    modifier: Modifier = Modifier,
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
    }
) {
    val density = LocalDensity.current
    val hapticFeedback = LocalHapticFeedback.current
    val horizontalPadding = 10.dp
    var fullWidth by remember { mutableIntStateOf(0) }
    // Define the track start and end points (full width)
    val startOfTrackPx = 0f
    val endOfTrackPx = remember(fullWidth) {
        with(density) { fullWidth - (2 * horizontalPadding + DraggableDefaults.Thumb.Size).toPx() }
    }

    fun calculateAnchors() = DraggableAnchors {
        Anchor.Start at startOfTrackPx
        Anchor.End at endOfTrackPx
    }

    val decayAnimationSpec = rememberSplineBasedDecay<Float>()
    val draggableState = remember {
        AnchoredDraggableState(
            initialValue = if (isLoading) Anchor.End else Anchor.Start,
            anchors = calculateAnchors(),
            positionalThreshold = { distance -> distance * 0.5f },
            velocityThreshold = { with(density) { 96.dp.toPx() } },
            snapAnimationSpec = tween(),
            decayAnimationSpec = decayAnimationSpec,
        )
    }

    val draggableOffset = draggableState.requireOffset()

    // Animate to start or end position based on loading state
    LaunchedEffect(isLoading) {
        draggableState.animateTo(if (isLoading) Anchor.End else Anchor.Start)
    }

    val overScrollEffect = ScrollableDefaults.overscrollEffect()

    val thumbModifier = if (isLoading) {
        Modifier.offset { IntOffset(draggableState.offset.roundToInt(), 0) }
    } else {
        Modifier.offset { IntOffset(draggableState.offset.roundToInt(), 0) }
    }

    DraggableTrack(
        modifier = modifier
            .height(DraggableDefaults.Track.Height)
            .fillMaxWidth(),
        enabled = !isLoading,
        draggableOffset = draggableOffset,
        onSizeChanged = {
            fullWidth = it.width
            draggableState.updateAnchors(calculateAnchors())
        },
        content = {
            DraggableHint(
                text = hintText,
                draggableOffset = draggableOffset,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(PaddingValues(horizontal = DraggableDefaults.Thumb.Size + 8.dp)),
            )

            DraggableThumb(
                isLoading = isLoading,
                startIcon = startIcon,
                endIcon = endIcon,
                modifier = thumbModifier.anchoredDraggable(
                    enabled = !isLoading,
                    state = draggableState,
                    orientation = Orientation.Horizontal,
                    overscrollEffect = overScrollEffect
                )
            )
        })

    // Check when the user has swiped to the end (Anchor.End) and trigger unlock
    LaunchedEffect(draggableState.targetValue) {
        if (draggableState.targetValue == Anchor.End) {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
            onUnlock() // Trigger unlock action
        }
    }
}
