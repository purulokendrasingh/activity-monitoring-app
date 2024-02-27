package com.artemis.activitymonitoring.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.*
import android.util.Log
import com.artemis.activitymonitoring.util.DeviceUtils
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class BatteryUsageService : Service() {

    companion object {
        private const val TAG = "BatteryUsageService"
    }

    private lateinit var batteryManager: BatteryManager
    private val client = OkHttpClient()

    override fun onCreate() {
        super.onCreate()
        batteryManager = getSystemService(Context.BATTERY_SERVICE) as BatteryManager

        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_BATTERY_CHANGED)
        registerReceiver(batteryReceiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(batteryReceiver)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_BATTERY_CHANGED) {
                val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                val batteryPercentage = level * 100 / scale.toFloat()
                val voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)
                val temperature: Int = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1)
                val plugged: Int = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)

                sendBatteryInfoToBackend(batteryPercentage, voltage, temperature, plugged)
            }
        }
    }

    private fun sendBatteryInfoToBackend(batteryPercentage: Float, voltage: Int, temperature: Int, plugged: Int) {
        val json = """
            {
                "id": "${DeviceUtils.getDeviceId()}",
                "device_id": "${DeviceUtils.getAndroidId(this)}", 
                "battery_percentage": $batteryPercentage,
                "voltage": $voltage,
                "temperature": $temperature,
                "plugged": $plugged
            }
        """.trimIndent()
        val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())
        val request = Request.Builder()
            .url("https://as-monitoringapp.azurewebsites.net/battery-usage/create")
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
