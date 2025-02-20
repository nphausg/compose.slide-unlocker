@file:OptIn(ExperimentalFoundationApi::class)

package com.patrick.elmquist.demo.slidetounlock

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.tween
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.times
import androidx.compose.material3.Icon
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import com.patrick.elmquist.demo.slidetounlock.ui.theme.DemoSlideToUnlockTheme
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

val horizontalPadding = 10.dp

@Composable
fun SlideToUnlock(
    isLoading: Boolean,
    hintText: String,
    modifier: Modifier = Modifier,
    onUnlockRequested: () -> Unit
) {
    val density = LocalDensity.current
    val hapticFeedback = LocalHapticFeedback.current
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
            velocityThreshold = { with(density) { Track.VelocityThreshold.toPx() } },
            snapAnimationSpec = tween(),
            decayAnimationSpec = decayAnimationSpec,
        )
    }

    val swipeFraction = draggableState.requireOffset()

    // Animate to start or end position based on loading state
    LaunchedEffect(isLoading) {
        draggableState.animateTo(if (isLoading) Anchor.End else Anchor.Start)
    }
    val overscrollEffect = ScrollableDefaults.overscrollEffect()
    Track(
        modifier = modifier,
        swipeFraction = swipeFraction,
        onSizeChanged = {
            fullWidth = it.width
            draggableState.updateAnchors(
                DraggableAnchors {
                    Anchor.Start at startOfTrackPx
                    Anchor.End at endOfTrackPx
                }
            )
        }
    ) {
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
                    overscrollEffect = overscrollEffect
                )
        )
    }

    // Check when the user has swiped to the end (Anchor.End) and trigger unlock
    LaunchedEffect(draggableState.targetValue) {
        if (draggableState.targetValue == Anchor.End) {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
            onUnlockRequested() // Trigger unlock action
        }
    }
}

enum class Anchor { Start, End }

@Composable
fun Track(
    swipeFraction: Float,
    modifier: Modifier = Modifier,
    onSizeChanged: (IntSize) -> Unit,
    content: @Composable (BoxScope.() -> Unit),
) {

    val backgroundColor by remember(swipeFraction) {
        derivedStateOf { calculateTrackColor(swipeFraction) }
    }

    Box(
        modifier = modifier
            .height(56.dp)
            .fillMaxWidth()
            .onSizeChanged(onSizeChanged)
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
        content = content
    )
}

val AlmostBlack = Color(0xFF111111)
val Yellow = Color(0xFFFFDB00)
fun calculateTrackColor(swipeFraction: Float): Color {
    val endOfColorChangeFraction = 0.4f
    val fraction = (swipeFraction / endOfColorChangeFraction).coerceIn(0f..1f)
    return lerp(AlmostBlack, Yellow, fraction)
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
                color = Color.Black,
                strokeWidth = 2.dp
            )
        } else {
            Image(
                painter = painterResource(R.drawable.arrow_right),
                contentDescription = null,
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

object Thumb {
    val Size = 40.dp
}

private object Track {
    val VelocityThreshold = 125.dp * 10
}

const val TIMEOUT = 4

@Composable
@Preview
private fun SlideToUnlockPreview() {

    var isLoading by remember { mutableStateOf(false) }
    var timeLeftInSec by remember { mutableIntStateOf(TIMEOUT) }
    LaunchedEffect(timeLeftInSec, isLoading) {
        while (timeLeftInSec > 0) {
            delay(1_000)
            timeLeftInSec--
        }
        isLoading = false
        timeLeftInSec = TIMEOUT
    }
    val previewBackgroundColor = Color(0xFFEDEDED)
    DemoSlideToUnlockTheme {
        val spacing = 88.dp
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .background(previewBackgroundColor)
                .padding(horizontal = 24.dp)
        ) {

            Column(modifier = Modifier.width(IntrinsicSize.Max)) {
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(text = "Normal")
                    Spacer(modifier = Modifier.weight(1f))
                    Thumb(isLoading = false)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(text = "Loading")
                    Spacer(modifier = Modifier.widthIn(min = 16.dp))
                    Thumb(isLoading = true)
                }
            }
            Spacer(modifier = Modifier.height(spacing))

            Text(text = "Inactive")
            Track(
                swipeFraction = 0f,
                modifier = Modifier.fillMaxWidth(),
                content = {},
                onSizeChanged = {}
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Active")
            Track(
                swipeFraction = 1f,
                modifier = Modifier.fillMaxWidth(),
                content = {},
                onSizeChanged = {}
            )


            Spacer(modifier = Modifier.height(spacing))

            SlideToUnlock(
                hintText = "Order Collected",
                isLoading = isLoading,
                onUnlockRequested = { isLoading = true },
            )
        }
    }
}
