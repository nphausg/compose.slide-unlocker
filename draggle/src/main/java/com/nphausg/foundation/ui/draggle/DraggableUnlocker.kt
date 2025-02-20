@file:OptIn(ExperimentalFoundationApi::class)

package com.nphausg.foundation.ui.draggle

import androidx.compose.animation.core.tween
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.onSizeChanged
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
    hintText: String,
    onUnlock: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val hapticFeedback = LocalHapticFeedback.current
    val horizontalPadding = 10.dp
    var fullWidth by remember { mutableIntStateOf(1400) }
    // Define the track start and end points (full width)
    val startOfTrackPx = 0f
    val endOfTrackPx = remember(fullWidth) {
        with(density) { fullWidth - (2 * horizontalPadding + Thumb.Size).toPx() }
    }
    // Wait for size to be updated
    val snapThreshold = 0.5f
    val decayAnimationSpec = rememberSplineBasedDecay<Float>()
    val draggableState = remember {
        AnchoredDraggableState(
            initialValue = if (isLoading) Anchor.End else Anchor.Start,
            anchors = DraggableAnchors {
                Anchor.Start at startOfTrackPx
                Anchor.End at endOfTrackPx
            },
            positionalThreshold = { distance -> distance * snapThreshold },
            velocityThreshold = { with(density) { 125.dp.toPx() } },
            snapAnimationSpec = tween(),
            decayAnimationSpec = decayAnimationSpec,
        )
    }

    val swipeFraction = draggableState.requireOffset()

    // Animate to start or end position based on loading state
    LaunchedEffect(isLoading) {
        draggableState.animateTo(if (isLoading) Anchor.End else Anchor.Start)
    }

    val backgroundColor by remember(swipeFraction) {
        derivedStateOf { calculateTrackColor(swipeFraction) }
    }
    val overScrollEffect = ScrollableDefaults.overscrollEffect()

    Box(
        modifier = modifier
            .height(56.dp)
            .fillMaxWidth()
            .onSizeChanged(onSizeChanged = {
                fullWidth = it.width
                draggableState.updateAnchors(
                    DraggableAnchors {
                        Anchor.Start at startOfTrackPx
                        Anchor.End at endOfTrackPx
                    }
                )
            })
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(percent = 50),
            )
            .padding(
                PaddingValues(
                    horizontal = horizontalPadding,
                    vertical = 8.dp,
                )
            ),
        content = {
            Hint(
                text = hintText,
                swipeFraction = swipeFraction,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(PaddingValues(horizontal = Thumb.Size + 8.dp)),
            )

            Thumb(
                isLoading = isLoading,
                modifier = Modifier
                    .offset {
                        IntOffset(draggableState.offset.roundToInt(), 0)
                    }
                    .anchoredDraggable(
                        enabled = !isLoading,
                        state = draggableState,
                        orientation = Orientation.Horizontal,
                        overscrollEffect = overScrollEffect
                    ),
            )
        },
    )

    // Check when the user has swiped to the end (Anchor.End) and trigger unlock
    LaunchedEffect(draggableState.targetValue) {
        if (draggableState.targetValue == Anchor.End) {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
            onUnlock() // Trigger unlock action
        }
    }
}

val StartColor = Color(0xFF4CAF50)
val StopColor = Color(0xFF117322)
fun calculateTrackColor(swipeFraction: Float): Color {
    val endOfColorChangeFraction = 0.4f
    val fraction = (swipeFraction / endOfColorChangeFraction).coerceIn(0f..1f)
    return lerp(StartColor, StopColor, fraction)
}

@Composable
fun Thumb(
    isLoading: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(Thumb.Size)
            .background(color = Color.White, shape = CircleShape)
            .padding(8.dp),
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.padding(2.dp),
                color = StartColor,
                strokeWidth = 2.dp
            )
        } else {
            Icon(
                tint = StartColor,
                contentDescription = "",
                imageVector = Icons.Filled.PlayArrow
            )
        }
    }
}

@Composable
fun Hint(
    text: String,
    swipeFraction: Float,
    modifier: Modifier = Modifier,
) {
    val hintTextColor by remember(swipeFraction) {
        derivedStateOf { calculateHintTextColor(swipeFraction) }
    }

    Text(
        text = text,
        color = hintTextColor,
        style = MaterialTheme.typography.titleSmall,
        modifier = modifier
    )
}

fun calculateHintTextColor(swipeFraction: Float): Color {
    val endOfFadeFraction = 0.35f
    val fraction = (swipeFraction / endOfFadeFraction).coerceIn(0f..1f)
    return lerp(Color.White, Color.White.copy(alpha = 0f), fraction)
}

//
//
object Thumb {
    val Size = 40.dp
}
