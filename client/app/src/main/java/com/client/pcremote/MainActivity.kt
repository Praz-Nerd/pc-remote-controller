package com.client.pcremote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.client.pcremote.api.SettingsManager
import com.client.pcremote.api.WebSocketManager
import com.client.pcremote.ui.components.RemoteScreen
import com.client.pcremote.ui.components.SetupScreen

class MainActivity : ComponentActivity() {
    private val wsManager by lazy {
        WebSocketManager(this)
    }

    private val settingsManager by lazy {
        SettingsManager(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //wsManager.connect()

        setContent {
            val savedIp = settingsManager.getServerIp()
            val savedPort = settingsManager.getServerPort()
            var isSetupComplete by remember { mutableStateOf(savedIp != null) }

            if (!isSetupComplete) {
                SetupScreen(
                    savedIp ?: "",
                    savedPort ?: "",
                    {ip, port ->
                        settingsManager.saveServer(ip, port)
                        isSetupComplete = true
                    }
                )
            }
            else {
                // LaunchedEffect runs exactly once when this block enters the screen
                LaunchedEffect(Unit) {
                    val ip = settingsManager.getServerIp()
                    val port = settingsManager.getServerPort()
                    wsManager.connect("ws://$ip:$port/ws")
                }

                // Pass a callback to RemoteScreen so we can disconnect and go back to settings
                RemoteScreen(
                    wsManager = wsManager,
                    onOpenSettings = {
                        wsManager.disconnect()
                        isSetupComplete = false // Flips the screen back to Setup
                    }
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        wsManager.disconnect() // Clean up when the app closes
    }
}