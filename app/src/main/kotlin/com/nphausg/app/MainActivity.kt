package com.nphausg.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nphausg.app.theme.AppTheme
import com.nphausg.foundation.ui.draggle.DraggableDefaults
import com.nphausg.foundation.ui.draggle.DraggableDefaults.Thumb.Size
import com.nphausg.foundation.ui.draggle.DraggableUnlocker
import com.nphausg.foundation.ui.draggle.EndIcon
import com.nphausg.foundation.ui.draggle.StartIcon
import kotlinx.coroutines.delay

private const val TIMEOUT = 4

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
private fun MainScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        GrabDraggableUnlocker()
        OKXDraggableUnlocker()
    }
}

@Composable
private fun GrabDraggableUnlocker(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
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
        Text(
            text = "Grab",
            style = MaterialTheme.typography.titleLarge
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Normal")
            DraggableDefaults.Thumb.StartIcon()
            Text(text = "Loading")
            DraggableDefaults.Thumb.EndIcon()
        }
        DraggableUnlocker(
            modifier = Modifier,
            isLoading = isLoading,
            hintText = { "Order Collected" },
            onUnlock = { isLoading = true }
        )
    }
}

@Composable
private fun OKXDraggableUnlocker(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
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
        Text(
            text = "OKX",
            style = MaterialTheme.typography.titleLarge
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Normal")
            OKXStartIcon(modifier = Modifier)
            Text(text = "Loading")
            OKXEndIcon(modifier = Modifier)
        }
        DraggableUnlocker(
            modifier = Modifier,
            isLoading = isLoading,
            startColor = { Color.Black },
            startIcon = { OKXStartIcon() },
            endIcon = { OKXEndIcon() },
            hintText = { "Slide to update" },
            onUnlock = { isLoading = true }
        )
    }
}

@Composable
private fun OKXStartIcon(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(Size)
            .background(color = Color.White, shape = CircleShape)
            .padding(DraggableDefaults.Thumb.Padding),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            tint = Color.Black,
            contentDescription = "",
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight
        )
    }
}

@Composable
private fun OKXEndIcon(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(Size)
            .background(color = Color.Green, shape = CircleShape)
            .padding(DraggableDefaults.Thumb.Padding),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = modifier,
            tint = Color.Black,
            contentDescription = "",
            imageVector = Icons.Filled.Check
        )
    }
}

@Composable
@Preview(
    showBackground = true,
    backgroundColor = 0xE8EEF4FF
)
private fun MainScreenPreview() {
    MainScreen()
}
