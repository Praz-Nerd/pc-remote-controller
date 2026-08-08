package com.client.pcremote.api

import android.content.Context
import android.widget.Toast
import com.client.pcremote.models.RemoteCommand
import com.google.gson.Gson
import okhttp3.*

class WebSocketManager(ctx: Context) :
    ContextAwareApiService(ctx) {
    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null
    private val gson = Gson()

    fun connect(serverUrl: String) {
        disconnect()
        val request = Request.Builder().url(serverUrl).build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                showToast("Connected to PC!")
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                showToast("Connection failed: ${t.message}")
            }
        })
    }

    fun sendCommand(command: RemoteCommand) {
        val jsonString = gson.toJson(command)
        webSocket?.send(jsonString)
    }

    fun disconnect() {
        webSocket?.close(1000, "App closed")
    }
}