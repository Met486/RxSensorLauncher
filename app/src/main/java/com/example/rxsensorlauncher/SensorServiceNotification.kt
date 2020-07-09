package com.example.rxsensorlauncher

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat

class SensorServiceNotification {

    val FOREGROUND_SERVICE_NOTIFICATION_ID = R.string.foreground_service_notification_id

    private var CHANNEL_ID = "channel_id"
    private var CHANNEL_NAME = "channel_name"

    fun  createNotificationChannel(context: Context) {

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
            return
        }

        val manager : NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if(manager == null || manager.getNotificationChannel(CHANNEL_ID) != null){
            return
        }

        val notificationChannel : NotificationChannel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )

        manager.createNotificationChannel(notificationChannel)
    }

    fun createServiceNotification(context:Context,pendingIntent: PendingIntent): Notification {
        return NotificationCompat.Builder(context,CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_android_black_24dp)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentIntent(pendingIntent)
            .build()
    }

}