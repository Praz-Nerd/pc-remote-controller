package com.client.pcremote.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.client.pcremote.api.WebSocketManager
import com.client.pcremote.models.MouseMoveCommand
import com.client.pcremote.models.RemoteAction
import com.client.pcremote.models.RemoteCommand

@Composable
fun RemoteScreen(wsManager: WebSocketManager) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 1. The Touchpad Zone
        Box(
            modifier = Modifier
                .weight(1f) // Fills all available vertical space above the buttons
                .fillMaxWidth()
                .background(Color.DarkGray)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume() // Prevent UI scrolling

                        val dx = dragAmount.x.toInt()
                        val dy = dragAmount.y.toInt()

                        // Send typed object to server
                        wsManager.sendCommand(MouseMoveCommand(dx, dy))
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Touchpad", color = Color.LightGray, fontSize = 24.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. The Mouse Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                modifier = Modifier.weight(1f).padding(end = 8.dp),
                onClick = { wsManager.sendCommand(RemoteCommand(RemoteAction.LEFT_CLICK)) }
            ) {
                Text("Left Click")
            }

            Button(
                modifier = Modifier.weight(1f).padding(start = 8.dp),
                onClick = { wsManager.sendCommand(RemoteCommand(RemoteAction.RIGHT_CLICK)) }
            ) {
                Text("Right Click")
            }
        }
    }
}