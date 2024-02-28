package com.artemis.activitymonitoring.util

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import java.util.UUID

object DeviceUtils {
    @SuppressLint("HardwareIds")
    fun getDeviceId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    fun getUID(): UUID {
        return UUID.randomUUID()
    }
}