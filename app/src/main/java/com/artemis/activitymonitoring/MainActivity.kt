package com.artemis.activitymonitoring

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.artemis.activitymonitoring.service.*
import com.artemis.activitymonitoring.util.DeviceUtils

class MainActivity : AppCompatActivity() {

    private val LOCATION_PERMISSION_REQUEST_CODE = 1001
    private lateinit var connectionStatusTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startService(Intent(this, SensorDataService::class.java))
        startService(Intent(this, DeviceStateService::class.java))
        startService(Intent(this, UsageStatsService::class.java))
        startService(Intent(this, BatteryUsageService::class.java))
        startService(Intent(this, NetworkDataService::class.java))

        connectionStatusTextView = findViewById(R.id.connection_status_textview)

        connectionStatusTextView.text = DeviceUtils.getDeviceId(this)

        if (!hasLocationPermissions()) {
            requestLocationPermissions()
        } else {
            startLocationDataService()
        }
    }

    private fun hasLocationPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Location permissions granted, start LocationDataService
                startLocationDataService()
            } else {
                // Location permissions denied, handle accordingly (e.g., show explanation, request again)
            }
        }
    }

    private fun startLocationDataService() {
        startService(Intent(this, LocationDataService::class.java))
    }
}