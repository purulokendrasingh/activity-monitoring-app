package com.artemis.activitymonitoring.service

import android.Manifest
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import com.artemis.activitymonitoring.util.DeviceUtils
import okhttp3.OkHttpClient
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.math.BigDecimal
import java.math.RoundingMode

class LocationDataService : Service() {

    companion object {
        private const val TAG = "LocationDataService"
    }

    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private var locationValueMap: MutableMap<String, Double> = mutableMapOf()

    private val client = OkHttpClient()

    override fun onCreate() {
        super.onCreate()
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        startLocationUpdates()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e(TAG, "Location permissions not granted")
            return
        }

        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                sendLocationToBackend(location)
            }
        }

        // Request location updates from both GPS and network providers
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)
    }

    private fun stopLocationUpdates() {
        locationManager.removeUpdates(locationListener)
    }

    private fun sendLocationToBackend(location: Location) {
        val latitude = BigDecimal(location.latitude.toString()).setScale(6, RoundingMode.HALF_EVEN).toDouble()
        val longitude = BigDecimal(location.longitude.toString()).setScale(6, RoundingMode.HALF_EVEN).toDouble()
        if (!locationValueMap.containsKey("latitude") || !locationValueMap.containsKey("longitude")) {
            locationValueMap["latitude"] = latitude
            locationValueMap["longitude"] = longitude
        }
        else if (locationValueMap["latitude"] != latitude || locationValueMap["longitude"] != longitude) {
            locationValueMap["latitude"] = latitude
            locationValueMap["longitude"] = longitude
            val json = """
                {
                    "id": "${DeviceUtils.getUID()}",
                    "device_id": "${DeviceUtils.getDeviceId(this)}",
                    "latitude": $latitude,
                    "longitude": $longitude
                }
            """.trimIndent()
            val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())
            val request = Request.Builder()
                .url("https://as-monitoringapp.azurewebsites.net/location-data/create")
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
}
