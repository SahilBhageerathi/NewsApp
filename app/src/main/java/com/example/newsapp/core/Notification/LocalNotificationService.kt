package com.example.newsapp.core.Notification



import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import com.example.newsapp.MainActivity
import com.example.newsapp.core.Notification.LocalNotificationService.Channels.GENERAL

class LocalNotificationService(private val context : Context){
    private val manager = context.getSystemService<NotificationManager>()

    object Channels {
        const val GENERAL = "channel_general"
    }

    fun initialize() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return


        manager?.let { createChannels(it) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannels(manager: NotificationManager) {
        val channels = listOf(
            NotificationChannel(
                Channels.GENERAL,
                "General",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "General app notifications"
            },
        )

        manager.createNotificationChannels(channels)
    }

    fun isChannelEnabled(channelId: String): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return true

        val channel = manager?.getNotificationChannel(channelId) ?: return false
        return channel.importance != NotificationManager.IMPORTANCE_NONE
    }

    fun showNotification(id: Int,count: Int){

        val activityIntent = Intent(context, MainActivity::class.java)
        val activityPendingIntent = PendingIntent.getActivity(
            context,
            1,
            activityIntent,
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        )


        val incrementIntent = Intent(context, CounterNotificationReceiver::class.java).apply {
            action = "ACTION_INCREMENT"
            putExtra("count", count)
            putExtra("notification_id", id)
        }
        val incrementPendingIntent = PendingIntent.getBroadcast(
            context,
            2,
            incrementIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )


        val notification = NotificationCompat.Builder(context, GENERAL)
            .setSmallIcon(androidx.core.R.drawable.notification_icon_background)
            .setContentTitle("Increment Counter")
            .setContentText("The count is $count")
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .addAction(
                androidx.core.R.drawable.notification_icon_background,
                "Increment",
                incrementPendingIntent
            )
            .build()


        manager?.notify(id, notification)


    }
}