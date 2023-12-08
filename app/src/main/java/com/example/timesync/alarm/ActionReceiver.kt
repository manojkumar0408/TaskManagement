package com.example.timesync.alarm

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.timesync.Constants
import java.util.Calendar

class ActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.i("actionss", "received")
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val id = intent.getLongExtra(Constants.ID, -1)
        val title = intent.getStringExtra(Constants.TITLE)
        val description = intent.getStringExtra(Constants.DESCRIPTION)
        val priority = intent.getStringExtra(Constants.PRIORITY)
        val status = intent.getStringExtra(Constants.STATUS)
        val dateTimeLong = intent.getLongExtra(Constants.EXTRA_ALERTMILLI, 1)
        val category = intent.getStringExtra(Constants.CATEGORY)
        val action = intent.getStringExtra("action")
        if (action == Constants.ONGOING) {
            val intentStatus = Intent("ChangeTaskStatus")
            intentStatus.putExtra(Constants.ID, id)
            intentStatus.putExtra(Constants.TITLE, title)
            intentStatus.putExtra(Constants.DESCRIPTION, description)
            intentStatus.putExtra(Constants.PRIORITY, priority)
            intentStatus.putExtra(Constants.EXTRA_ALERTMILLI, dateTimeLong)
            intentStatus.putExtra(Constants.STATUS, "ongoing")
            intentStatus.putExtra(Constants.CATEGORY, category)
            context.sendBroadcast(intentStatus)
            notificationManager.cancel(TAG, id.toInt())
        } else if (action == Constants.POSTPONE) {
            val intentStatus = Intent("PostPoneTask")
            val cal = Calendar.getInstance()
            cal.add(Calendar.MINUTE, 2)
            val newMillis = cal.timeInMillis
            intentStatus.putExtra(Constants.ID, id)
            intentStatus.putExtra(Constants.TITLE, title)
            intentStatus.putExtra(Constants.DESCRIPTION, description)
            intentStatus.putExtra(Constants.PRIORITY, priority)
            intentStatus.putExtra(Constants.EXTRA_ALERTMILLI, newMillis)
            intentStatus.putExtra(Constants.STATUS, "ongoing")
            intentStatus.putExtra(Constants.CATEGORY, category)
            context.sendBroadcast(intentStatus)
            notificationManager.cancel(TAG, id.toInt())
        }
    }

    companion object {
        private const val TAG = "CreateNotification"
    }
}