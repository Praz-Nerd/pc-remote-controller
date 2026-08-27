package com.client.pcremote.api

import android.content.Context
import com.client.pcremote.models.RemoteCommand
import com.google.gson.Gson
import okhttp3.*
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.util.concurrent.Executors

class DatagramManager(private val context: Context) : ContextAwareApiService(context) {
    private val httpClient = OkHttpClient()
    private val gson = Gson()
    private val settingsManager = SettingsManager(context)
    private val udpWorker = Executors.newSingleThreadExecutor()
    private var udpSocket = DatagramSocket()
    
    private val udpPort: Int
        get() = settingsManager.getServerPort()?.toIntOrNull() ?: 5201

    fun sendCommand(command: RemoteCommand) {
        // Network calls must run on a background thread
        udpWorker.execute {
            try {
                val ip = settingsManager.getServerIp() ?: return@execute
                if (ip.isBlank()) return@execute

                val jsonString = gson.toJson(command)
                val bytes = jsonString.toByteArray()
                val address = InetAddress.getByName(ip)

                val packet = DatagramPacket(bytes, bytes.size, address, udpPort)
                udpSocket.send(packet)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun cleanup() {
        udpSocket.close()
        udpWorker.shutdown()
    }
}