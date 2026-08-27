package com.client.pcremote.ui.components


import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.client.pcremote.R
import com.client.pcremote.api.DatagramManager
import com.client.pcremote.models.MouseMoveCommand
import com.client.pcremote.models.RemoteAction
import com.client.pcremote.models.RemoteCommand

@Composable
fun RemoteScreen(networkManager: DatagramManager) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 1. The Touchpad Zone
        Touchpad(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            onTap = {
                networkManager.sendCommand(RemoteCommand(RemoteAction.LEFT_CLICK))
            },
            onMouseMove = { dx, dy ->
                networkManager.sendCommand(MouseMoveCommand(dx, dy))
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 2. The Mouse Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                modifier = Modifier.weight(1f).padding(end = 8.dp),
                onClick = { networkManager.sendCommand(RemoteCommand(RemoteAction.LEFT_CLICK)) }
            ) {
                Text(stringResource(R.string.left_click))
            }

            Button(
                modifier = Modifier.weight(1f).padding(start = 8.dp),
                onClick = { networkManager.sendCommand(RemoteCommand(RemoteAction.RIGHT_CLICK)) }
            ) {
                Text(stringResource(R.string.right_click))
            }
        }
    }
}