package com.wahdanz.testbridschallenge.utils

import android.app.AppOpsManager
import android.content.Context
import android.content.pm.PackageManager
import android.provider.Settings

object Utils {
    fun isAccessibilitySettingsOn(context: Context): Boolean {
        var accessibilityEnabled = 0
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                context.contentResolver,
                Settings.Secure.ACCESSIBILITY_ENABLED
            )
        } catch (e: Settings.SettingNotFoundException) {
        }

        if (accessibilityEnabled == 1) {
            val services = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            if (services != null) {
                return services.toLowerCase().contains(context.packageName.toLowerCase())
            }
        }
        return false
    }

    fun getUsageStatsPermissionsStatus(context: Context): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode =
            appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.packageName)
        val granted = if (mode == AppOpsManager.MODE_DEFAULT)
            context.checkCallingOrSelfPermission(android.Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED
        else
            mode == AppOpsManager.MODE_ALLOWED
        return granted
    }
}