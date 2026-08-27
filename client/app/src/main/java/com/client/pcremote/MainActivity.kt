package com.client.pcremote

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.Modifier
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import com.client.pcremote.api.DatagramManager
import com.client.pcremote.api.HttpManager
import com.client.pcremote.api.SettingsManager
import com.client.pcremote.models.RemoteAction
import com.client.pcremote.models.RemoteCommand
import com.client.pcremote.ui.components.ControlBar
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

    private var isSetupComplete by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        isSetupComplete = settingsManager.getServerIp() != null

        setContent {
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
                Column(
                    modifier = Modifier
                        .statusBarsPadding()
                        .navigationBarsPadding()
                ) {
                    ControlBar(
                        onRefresh = { httpManager.refreshServer() },
                        onOpenSettings = { isSetupComplete = false }
                    )
                    RemoteScreen(
                        networkManager = datagramManager
                    )
                }
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (isSetupComplete) {
            when (keyCode) {
                KeyEvent.KEYCODE_VOLUME_UP -> {
                    datagramManager.sendCommand(RemoteCommand(RemoteAction.VOLUME_UP))
                    return true
                }
                KeyEvent.KEYCODE_VOLUME_DOWN -> {
                    datagramManager.sendCommand(RemoteCommand(RemoteAction.VOLUME_DOWN))
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()
        datagramManager.cleanup()
    }
}