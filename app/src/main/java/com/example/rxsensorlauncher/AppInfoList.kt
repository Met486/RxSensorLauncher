package com.example.rxsensorlauncher

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager

fun create(context: Context): List<AppInfo> {
    val pm = context.packageManager
    val intent = Intent(Intent.ACTION_MAIN)
        .also { it.addCategory(Intent.CATEGORY_LAUNCHER) }
    return pm.queryIntentActivities(intent, PackageManager.MATCH_ALL)
        .asSequence()
        .mapNotNull { it.activityInfo }
        .filter { it.packageName != context.packageName }
        .map {
            AppInfo(
                it.loadIcon(pm),
                it.loadLabel(pm).toString(),
                ComponentName(it.packageName, it.name)
            )
        }
        .sortedBy { it.label }
        .toList()
}
