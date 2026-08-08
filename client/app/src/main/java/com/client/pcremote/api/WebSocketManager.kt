package com.client.pcremote.api

import com.client.pcremote.models.RemoteCommand
import com.google.gson.Gson
import okhttp3.*

class WebSocketManager(private val serverUrl: String) {
    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null
    private val gson = Gson()

    fun connect() {
        val request = Request.Builder().url(serverUrl).build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                println("Connected to PC Server!")
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                println("Connection failed: ${t.message}")
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