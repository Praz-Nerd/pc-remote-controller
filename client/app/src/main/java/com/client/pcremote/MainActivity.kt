package com.client.pcremote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.client.pcremote.api.DatagramManager
import com.client.pcremote.api.HttpManager
import com.client.pcremote.api.SettingsManager
import com.client.pcremote.ui.components.RemoteScreen
import com.client.pcremote.ui.components.SetupScreen

class MainActivity : ComponentActivity() {

    private val datagramManager by lazy {
        DatagramManager(this)
    }

    private val httpManager by lazy {
        HttpManager(this)
    }

    private val settingsManager by lazy {
        SettingsManager(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var isSetupComplete by remember { 
                mutableStateOf(settingsManager.getServerIp() != null) 
            }

            LaunchedEffect(Unit) {
                if (isSetupComplete) {
                    httpManager.refreshServer()
                }
            }

            if (!isSetupComplete) {
                SetupScreen(
                    initialIp = settingsManager.getServerIp() ?: "",
                    initialPort = settingsManager.getServerPort() ?: "5201",
                    onBack = if (settingsManager.getServerIp() != null) {
                        { isSetupComplete = true }
                    } else null,
                    onConnectClicked = { ip, port ->
                        settingsManager.saveServer(ip, port)
                        httpManager.registerServer(
                            onSuccess = { isSetupComplete = true }
                        )
                    }
                )
            } else {
                RemoteScreen(
                    networkManager = datagramManager,
                    onOpenSettings = {
                        isSetupComplete = false
                    }
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        datagramManager.cleanup()
    }
}