package com.example.rxsensorlauncher

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null) {
            return
        }
        if (intent == null) {
            return
        }
        if (!intent.action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            return
        }

        Log.i("DEBUG", "ACTION_BOOT_COMPLETED")
    }
}