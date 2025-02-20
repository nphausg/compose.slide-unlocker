package com.patrick.elmquist.demo.slidetounlock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.patrick.elmquist.demo.slidetounlock.ui.theme.DemoSlideToUnlockTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DemoSlideToUnlockTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(24.dp)
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

                        SlideToUnlock(
                            isLoading = isLoading,
                            hintText = "Swipe to unlock reward",
                            onUnlockRequested = {
                                isLoading = true
                            }
                        )
                    }
                }
            }
        }
    }
}
