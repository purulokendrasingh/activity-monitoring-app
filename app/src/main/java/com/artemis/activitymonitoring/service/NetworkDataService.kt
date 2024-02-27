package com.artemis.activitymonitoring.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.telecom.Call
import android.util.Log
import com.artemis.activitymonitoring.util.DeviceUtils
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class NetworkDataService : Service() {

    companion object {
        private const val TAG = "NetworkDataService"
    }

    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback

    private val client = OkHttpClient()

    override fun onCreate() {
        super.onCreate()
        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        startNetworkMonitoring()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopNetworkMonitoring()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun startNetworkMonitoring() {
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                Log.d(TAG, "Network available: $network")
                sendNetworkDataToBackend()
            }

            override fun onLost(network: Network) {
                Log.d(TAG, "Network lost: $network")
                sendNetworkDataToBackend()
            }
        }

        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

    private fun stopNetworkMonitoring() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    private fun sendNetworkDataToBackend() {
        val json = """
            {
                "id": "${DeviceUtils.getDeviceId()}",
                "device_id": "${DeviceUtils.getAndroidId(this)}",
                "is_network_available": ${isNetworkAvailable()},
                 "is_wifi_connected": ${isConnectedViaWiFi()},
                 "is_mobile_data_connected": ${isConnectedViaMobileData()}
            }
        """.trimIndent()
        val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())
        val request = Request.Builder()
            .url("https://as-monitoringapp.azurewebsites.net/connectivity/create")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: okhttp3.Call, response: Response) {
                Log.d(TAG, "Data sent successfully")
            }

            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.e(TAG, "Failed to send data: ${e.message}")
            }
        })
    }

    private fun isNetworkAvailable(): Boolean {
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun isConnectedViaWiFi(): Boolean {
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        return networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ?: false
    }

    private fun isConnectedViaMobileData(): Boolean {
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        return networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ?: false
    }
}
