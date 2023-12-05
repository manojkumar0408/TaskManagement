package com.example.timesync.alarm

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.timesync.AddNewTaskActivity
import com.example.timesync.Constants
import com.example.timesync.R
import com.example.timesync.TImeSyncApplication
import java.net.IDN

class AlertReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getLongExtra(Constants.ID, -1)
        val title = intent.getStringExtra(Constants.TITLE)
        val description = intent.getStringExtra(Constants.DESCRIPTION)
        val radioChoice = intent.getStringExtra(Constants.PRIORITY)
        val dateTimeLong = intent.getLongExtra(Constants.EXTRA_ALERTMILLI, 1L)
        val taskStatus = intent.getStringExtra(Constants.STATUS)
        val category = intent.getStringExtra(Constants.CATEGORY)
        createNotification(
            context,
            title,
            description,
            "alert",
            id,
            radioChoice,
            dateTimeLong,
            taskStatus,
            category
        )
    }

    fun createNotification(
        context: Context,
        title: String?,
        contentText: String?,
        msgAlert: String?,
        id: Long,
        radioChoice: String?,
        dateTimeLong: Long,
        taskStatus: String?,
        category: String?
    ) {
        val intent = Intent(context, AddNewTaskActivity::class.java)
        intent.putExtra(Constants.ID, id)
        intent.putExtra(Constants.TITLE, title)
        intent.putExtra(Constants.DESCRIPTION, contentText)
        intent.putExtra(Constants.PRIORITY, radioChoice)
        intent.putExtra(Constants.EXTRA_ALERTMILLI, dateTimeLong)
        intent.putExtra(Constants.STATUS, taskStatus)
        intent.putExtra(Constants.CATEGORY, category)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val firstAction = Intent(context, ActionReceiver::class.java)
        firstAction.putExtra(Constants.ID, id)
        firstAction.putExtra(Constants.TITLE, title)
        firstAction.putExtra(Constants.DESCRIPTION, contentText)
        firstAction.putExtra(Constants.PRIORITY, radioChoice)
        firstAction.putExtra(Constants.EXTRA_ALERTMILLI, dateTimeLong)
        firstAction.putExtra(Constants.STATUS, taskStatus)
        firstAction.putExtra(Constants.CATEGORY, category)
        firstAction.putExtra("action", "ongoing")
        val secondAction = Intent(context, ActionReceiver::class.java)
        secondAction.putExtra(Constants.ID, id)
        secondAction.putExtra(Constants.TITLE, title)
        secondAction.putExtra(Constants.DESCRIPTION, contentText)
        secondAction.putExtra(Constants.PRIORITY, radioChoice)
        secondAction.putExtra(Constants.EXTRA_ALERTMILLI, dateTimeLong)
        secondAction.putExtra(Constants.STATUS, taskStatus)
        secondAction.putExtra(Constants.CATEGORY, category)
        secondAction.putExtra("action", "postpone")
        val firstActionPendingIntent = PendingIntent.getBroadcast(
            context, 1, firstAction, PendingIntent.FLAG_UPDATE_CURRENT
        )
        val secondActionPendingIntent = PendingIntent.getBroadcast(
            context, 2, secondAction, PendingIntent.FLAG_UPDATE_CURRENT
        )

//        Intent dialog = new Intent(context, MainActivity.class);
//        dialog.putExtra("fromnotification", true);
//
//        PendingIntent postPoneIntent = PendingIntent.getActivity(context, 0,
//                dialog, 0);
        val notificationIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        val nb: NotificationCompat.Builder =
            NotificationCompat.Builder(context, TImeSyncApplication.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_menu_gallery).setContentTitle(title).setTicker(msgAlert)
                .setContentText(contentText).setPriority(NotificationCompat.PRIORITY_HIGH)
//                .addAction(R.drawable.ic_calendar, "ongoing", firstActionPendingIntent).addAction(
//                    R.drawable.ic_menu_slideshow,
//                    "postpone by 10 minutes",
//                    secondActionPendingIntent
//                )
        nb.setContentIntent(notificationIntent)
        nb.setDefaults(NotificationCompat.DEFAULT_SOUND)
        nb.setAutoCancel(true)
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(TAG, id.toInt(), nb.build())
    }

    companion object {
        private const val TAG = "CreateNotification"
    }
}
