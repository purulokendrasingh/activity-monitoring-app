package com.artemis.activitymonitoring.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.app.usage.UsageEvents
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import okhttp3.*
import java.io.IOException
import java.util.*

class UsageStatsService : Service() {

    companion object {
        private const val TAG = "UsageStatsService"
        private const val INTERVAL = 60 * 1000L // Interval for querying usage stats (in milliseconds)
    }

    private lateinit var usageStatsManager: UsageStatsManager
    private lateinit var previousUsageStats: MutableList<UsageStats>

    private val client = OkHttpClient()

    override fun onCreate() {
        super.onCreate()
        usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        previousUsageStats = mutableListOf()

        startUsageStatsCollection()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun startUsageStatsCollection() {
        val handler = Handler(Looper.getMainLooper())
        handler.post(object : Runnable {
            override fun run() {
                collectUsageStats()
                handler.postDelayed(this, INTERVAL)
            }
        })
    }

    private fun collectUsageStats() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, -1) // Collect stats for the last day

        val endTime = System.currentTimeMillis()
        val startTime = calendar.timeInMillis

        val currentUsageStats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            startTime,
            endTime
        )

        val newUsageStats = findNewUsageStats(currentUsageStats)
        if (newUsageStats.isNotEmpty()) {
            processUsageStats(newUsageStats)
        }

        previousUsageStats.clear()
        previousUsageStats.addAll(currentUsageStats)
    }

    private fun findNewUsageStats(currentUsageStats: MutableList<UsageStats>): MutableList<UsageStats> {
        val newUsageStats = mutableListOf<UsageStats>()
        for (currentStat in currentUsageStats) {
            var isNew = true
            for (previousStat in previousUsageStats) {
                if (currentStat.packageName == previousStat.packageName && currentStat.lastTimeUsed == previousStat.lastTimeUsed) {
                    isNew = false
                    break
                }
            }
            if (isNew) {
                newUsageStats.add(currentStat)
            }
        }
        return newUsageStats
    }

    private fun processUsageStats(usageStats: MutableList<UsageStats>) {
        usageStats.forEach { stats ->
            Log.d(TAG, "Package Name: ${stats.packageName}, Last Time Used: ${stats.lastTimeUsed}, Total Time in Foreground: ${stats.totalTimeInForeground}")
        }

        sendUsageStatsToBackend(usageStats)
    }

    private fun sendUsageStatsToBackend(usageStats: MutableList<UsageStats>) {
        // Implement sending data to backend using REST API
        // Use OkHttpClient to make HTTP requests
//        val client = OkHttpClient()
//        val requestBody = RequestBody.create(MediaType.parse("application/json"), usageStats.toString())
//        val request = Request.Builder()
//            .url("YOUR_BACKEND_URL")
//            .post(requestBody)
//            .build()
//
//        client.newCall(request).enqueue(object : Callback {
//            override fun onFailure(call: Call, e: IOException) {
//                Log.e(TAG, "Failed to send usage stats to backend: ${e.message}")
//            }
//
//            override fun onResponse(call: Call, response: Response) {
//                Log.d(TAG, "Usage stats sent to backend successfully")
//            }
//        })
        usageStats.forEach { stats ->
            Log.d(TAG, "Package Name: ${stats.packageName}, Last Time Used: ${stats.lastTimeUsed}, Total Time in Foreground: ${stats.totalTimeInForeground}")
        }
    }
}