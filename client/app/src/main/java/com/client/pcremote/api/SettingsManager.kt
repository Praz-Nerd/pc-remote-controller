package com.client.pcremote.api

import android.content.Context
import androidx.core.content.edit

class SettingsManager(context: Context) {
    private val prefs = context.getSharedPreferences("pc_remote_prefs", Context.MODE_PRIVATE)

    fun saveServer(ip: String, port: String) {
        prefs.edit {
            putString("server_ip", ip)
                .putString("server_port", port)
        }
    }

    fun getServerIp(): String? = prefs.getString("server_ip", null)

    fun getServerPort(): String? = prefs.getString("server_port", null)
}