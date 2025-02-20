package com.nphausg.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nphausg.app.theme.AppTheme
import com.nphausg.foundation.ui.draggle.DraggableUnlocker
import com.nphausg.foundation.ui.draggle.Thumb
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
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

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        verticalArrangement = Arrangement.Center
                    ) {
                        DraggableUnlocker(
                            modifier = Modifier.padding(innerPadding),
                            isLoading = isLoading,
                            hintText = "Order Collected",
                            onUnlock = {
                                isLoading = true
                            }
                        )
                    }

                }
            }
        }
    }
}

//
private const val TIMEOUT = 4

@Composable
@Preview(showBackground = true)
private fun SlideUnlockerPreview() {
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
    AppTheme {

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
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
            DraggableUnlocker(
                hintText = "Order Collected",
                isLoading = isLoading,
                onUnlock = { isLoading = true },
            )
        }
    }
}
