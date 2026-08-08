package com.client.pcremote.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.sp
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun Touchpad(
    modifier: Modifier = Modifier,
    sensitivity: Float = 0.5f,
    onMouseMove: (Int, Int) -> Unit // Callback to the parent
) {
    var queuedX by remember { mutableFloatStateOf(0f) }
    var queuedY by remember { mutableFloatStateOf(0f) }

    // The Background Network Sender (60 FPS Throttle)
    LaunchedEffect(Unit) {
        while (true) {
            val dx = queuedX.toInt()
            val dy = queuedY.toInt()

            if (dx != 0 || dy != 0) {
                onMouseMove(dx, dy) // Tell the parent the mouse moved
                queuedX -= dx
                queuedY -= dy
            }

            kotlinx.coroutines.delay(16.milliseconds)
        }
    }

    Box(
        modifier = modifier
            .background(Color.DarkGray)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        queuedX = 0f
                        queuedY = 0f
                    }
                ) { change, dragAmount ->
                    change.consume()
                    queuedX += dragAmount.x * sensitivity
                    queuedY += dragAmount.y * sensitivity
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Touchpad", color = Color.LightGray, fontSize = 24.sp)
    }
}