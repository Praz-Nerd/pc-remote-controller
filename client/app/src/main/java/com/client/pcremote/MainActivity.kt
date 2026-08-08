package com.example.pcremote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.client.pcremote.api.WebSocketManager
import com.client.pcremote.ui.components.RemoteScreen

class MainActivity : ComponentActivity() {

    private val wsManager = WebSocketManager("ws://192.168.1.100:5000/ws")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        wsManager.connect()

        setContent {
            RemoteScreen(wsManager)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        wsManager.disconnect() // Clean up when the app closes
    }
}