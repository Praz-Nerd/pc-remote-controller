package com.client.pcremote.api

import android.content.Context
import android.os.Handler
import android.os.Looper
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class HttpManager(context: Context) : ContextAwareApiService(context) {
    private val httpClient = OkHttpClient()
    private val settingsManager = SettingsManager(context)

    fun registerServer(onSuccess: (String) -> Unit = {}, onError: (String) -> Unit = {}) {
        val ip = settingsManager.getServerIp()
        val port = settingsManager.getServerPort()

        if (ip.isNullOrBlank() || port.isNullOrBlank()) {
            showToast("Server configuration missing")
            onError("IP or Port not set")
            return
        }

        val url = "http://$ip:$port/register"
        val request = Request.Builder()
            .url(url)
            .post(ByteArray(0).toRequestBody(null)) // Empty POST body
            .build()

        httpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                val errorMessage = "Registration failed: ${e.message}"
                showToast(errorMessage)
                Handler(Looper.getMainLooper()).post { onError(errorMessage) }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body.string()
                if (response.isSuccessful) {
                    showToast(responseBody.ifBlank { "IP registered successfully" })
                    Handler(Looper.getMainLooper()).post { onSuccess(responseBody) }
                } else {
                    val errorMsg = "Registration error: ${response.code} $responseBody"
                    showToast(errorMsg)
                    Handler(Looper.getMainLooper()).post { onError(errorMsg) }
                }
            }
        })
    }

    fun refreshServer(onSuccess: (String) -> Unit = {}, onError: (String) -> Unit = {}) {
        val ip = settingsManager.getServerIp()
        val port = settingsManager.getServerPort()

        if (ip.isNullOrBlank() || port.isNullOrBlank()) {
            showToast("Server configuration missing")
            onError("IP or Port not set")
            return
        }

        val url = "http://$ip:$port/refresh"
        val request = Request.Builder()
            .url(url)
            .post(ByteArray(0).toRequestBody(null)) // Empty POST body
            .build()

        httpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                val errorMessage = "Refresh failed: ${e.message}"
                showToast(errorMessage)
                Handler(Looper.getMainLooper()).post { onError(errorMessage) }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body.string()
                if (response.isSuccessful) {
                    showToast(responseBody.ifBlank { "IP refreshed successfully" })
                    Handler(Looper.getMainLooper()).post { onSuccess(responseBody) }
                } else {
                    val errorMsg = "Refresh error: ${response.code} $responseBody"
                    showToast(errorMsg)
                    Handler(Looper.getMainLooper()).post { onError(errorMsg) }
                }
            }
        })
    }
}