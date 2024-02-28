package com.artemis.activitymonitoring.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.*
import android.util.Log
import com.artemis.activitymonitoring.util.DeviceUtils
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException

class DeviceStateService : Service() {

    companion object {
        private const val TAG = "DeviceStateService"
    }

    private lateinit var powerManager: PowerManager
    private lateinit var wakeLock: PowerManager.WakeLock
    private lateinit var screenStateReceiver: ScreenStateReceiver

    private val client = OkHttpClient()

    override fun onCreate() {
        super.onCreate()
        powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DeviceStateService:WakeLock")
        wakeLock.acquire()

        startDeviceStateMonitoring()
    }

    override fun onDestroy() {
        super.onDestroy()
        wakeLock.release()

        unregisterReceiver(screenStateReceiver)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun startDeviceStateMonitoring() {
        screenStateReceiver = ScreenStateReceiver()
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_SCREEN_ON)
        filter.addAction(Intent.ACTION_SCREEN_OFF)
        registerReceiver(screenStateReceiver, filter)
    }

    inner class ScreenStateReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Intent.ACTION_SCREEN_ON -> {
                    Log.d("DeviceStateMonitoring", "Screen is turned on")
                    sendDataToBackend(true)
                }
                Intent.ACTION_SCREEN_OFF -> {
                    Log.d("DeviceStateMonitoring", "Screen is turned off")
                    sendDataToBackend(false)
                }
            }
        }
    }

    private fun sendDataToBackend(isScreenOn: Boolean) {
        val json = """
            {
                "id": "${DeviceUtils.getUID()}",
                "device_id": "${DeviceUtils.getDeviceId(this)}",
                "is_screen_on": $isScreenOn 
            }
        """.trimIndent()
        val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())
        val request = Request.Builder()
            .url("https://as-monitoringapp.azurewebsites.net/device-state/create")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                Log.d(TAG, "Data sent successfully")
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Failed to send data: ${e.message}")
            }
        })
    }
}
