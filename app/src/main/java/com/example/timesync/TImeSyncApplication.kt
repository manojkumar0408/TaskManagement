package com.example.timesync

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class TImeSyncApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel1 = NotificationChannel(
                CHANNEL_ID, "Task Notification", NotificationManager.IMPORTANCE_HIGH
            )
            channel1.description = "Receive alerts for tasks"
            val manager: NotificationManager = getSystemService<NotificationManager>(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(channel1)
        }
    }

    companion object {
        const val CHANNEL_ID = "taskAlert"
    }
}