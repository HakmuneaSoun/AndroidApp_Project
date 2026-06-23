package com.example.final_project.data.remote

import android.os.Build
import com.example.final_project.BuildConfig

object ApiConstants {

    /**
     * Emulator: 10.0.2.2 maps to the host PC.
     * Physical device: uses DEV_SERVER_HOST from local.properties (your PC LAN IP).
     */
    val serverHost: String
        get() = if (isEmulator()) "10.0.2.2" else BuildConfig.DEV_SERVER_HOST

    val BASE_URL: String
        get() = "http://$serverHost:8081/"

    fun resolveMediaUrl(url: String?): String? {
        if (url.isNullOrBlank()) return null
        val host = serverHost
        return url
            .replace("localhost", host)
            .replace("127.0.0.1", host)
            .replace("10.0.2.2", host)
    }

    fun connectionErrorMessage(): String =
        "Cannot reach server at $BASE_URL. Make sure your phone and PC are on the same network, then rebuild after setting DEV_SERVER_HOST in local.properties."

    private fun isEmulator(): Boolean {
        return Build.FINGERPRINT.startsWith("generic")
            || Build.FINGERPRINT.lowercase().contains("emulator")
            || Build.MODEL.contains("google_sdk", ignoreCase = true)
            || Build.MODEL.contains("Emulator", ignoreCase = true)
            || Build.MODEL.contains("Android SDK built for", ignoreCase = true)
            || Build.MANUFACTURER.contains("Genymotion", ignoreCase = true)
            || Build.HARDWARE.contains("goldfish", ignoreCase = true)
            || Build.HARDWARE.contains("ranchu", ignoreCase = true)
            || Build.PRODUCT.contains("sdk_gphone", ignoreCase = true)
    }
}
