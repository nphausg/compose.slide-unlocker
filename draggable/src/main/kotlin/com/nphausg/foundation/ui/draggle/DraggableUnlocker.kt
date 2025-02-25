/*
 * Copyright (c) 2025 nphausg.
 * All rights reserved.
 */

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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    startColor: () -> Color = { DraggableDefaults.Track.StartColor },
    stopColor: () -> Color = { DraggableDefaults.Track.StopColor },
    startIcon: @Composable () -> Unit = DraggableDefaults.Thumb.StartIcon,
    endIcon: @Composable () -> Unit = DraggableDefaults.Thumb.EndIcon
) {
    val density = LocalDensity.current
    val hapticFeedback = LocalHapticFeedback.current
    val horizontalPadding = 10.dp
    var fullWidth by remember { mutableIntStateOf(0) }
    // Define the track start and end points (full width)
    val startOfTrackPx = 0f
    val endOfTrackPx = remember(fullWidth) {
        // with(density) { fullWidth - (2 * horizontalPadding + DraggableDefaults.Thumb.Size).toPx() }
        with(density) { fullWidth - DraggableDefaults.Thumb.Size.toPx() }
    }

    val decayAnimationSpec = rememberSplineBasedDecay<Float>()
    val draggableState = remember {
        AnchoredDraggableState(
            initialValue = if (isLoading) Anchor.End else Anchor.Start,
            anchors = DraggableAnchors {
                Anchor.Start at startOfTrackPx
                Anchor.End at endOfTrackPx
            },
            positionalThreshold = { distance -> distance * 0.5f },
            velocityThreshold = { with(density) { 2048.dp.toPx() } },
            snapAnimationSpec = tween(),
            decayAnimationSpec = decayAnimationSpec,
        )
    }
//    val isReachToEnd by remember(draggableState.targetValue) {
//        derivedStateOf {
//            draggableState.targetValue == Anchor.End
//        }
//    }
    val draggableOffset = draggableState.requireOffset()

//    val thumbOffsetModifier = if (isReachToEnd) {
//        Modifier.offset { IntOffset(draggableState.offset.roundToInt() / 2, 0) }
//    } else {
//        Modifier.offset { IntOffset(draggableState.offset.roundToInt(), 0) }
//    }
    // Animate to start or end position based on loading state
    LaunchedEffect(isLoading) {
        draggableState.animateTo(if (isLoading) Anchor.End else Anchor.Start)
    }

    val overscrollEffect = ScrollableDefaults.overscrollEffect()

    DraggableTrack(
        modifier = modifier
            .height(DraggableDefaults.Track.Height)
            .fillMaxWidth(),
        enabled = true,
        startColor = startColor,
        stopColor = stopColor,
        draggableOffset = draggableOffset,
        onSizeChanged = {
            fullWidth = it.width
            draggableState.updateAnchors(
                DraggableAnchors {
                    Anchor.Start at startOfTrackPx
                    Anchor.End at endOfTrackPx
                })
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
                endIcon = endIcon,
                isLoading = isLoading,
                startIcon = startIcon,
                modifier = Modifier.offset { IntOffset(draggableState.offset.roundToInt(), 0) }
                    .anchoredDraggable(
                        enabled = !isLoading,
                        state = draggableState,
                        orientation = Orientation.Horizontal,
                        overscrollEffect = overscrollEffect
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
