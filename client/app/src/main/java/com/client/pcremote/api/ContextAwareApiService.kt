package com.client.pcremote.api

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast

open class ContextAwareApiService (private val ctx: Context) {
    protected fun showToast (message: String, duration: Int = Toast.LENGTH_SHORT) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(ctx, message, duration).show()
        }
    }
    protected fun showToast (resId: Int, duration: Int = Toast.LENGTH_SHORT) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(ctx, resId, duration).show()
        }
    }
}