package com.example.newsapp.core.fcmMessaging

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.example.newsapp.MainActivity
import com.example.newsapp.R

class LocalNotifier(private val context: Context) {

    init {
        // Create the channel once when the notifier is first constructed.
        createChannel()
    }

    fun showNotification(
        title: String,
        body: String,
        deepLink: String? = null
    ) {
        // Android 13+ : posting without permission silently fails. Guard it.
        if (!hasNotificationPermission()) return

        val manager = context.getSystemService(NotificationManager::class.java)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)   // swap for R.drawable.ic_notification later
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body)) // expandable long text
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(buildPendingIntent(deepLink))
            .setAutoCancel(true)   // dismiss on tap
            .build()

        // Unique ID → notifications stack instead of overwriting each other
        val notificationId = System.currentTimeMillis().toInt()
        manager.notify(notificationId, notification)
    }

    private fun buildPendingIntent(deepLink: String?): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            deepLink?.let { putExtra(EXTRA_DEEP_LINK, it) }
        }
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            // FLAG_IMMUTABLE is REQUIRED on Android 12+ (S)
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun hasNotificationPermission(): Boolean {
        // Permission only exists on Android 13+ (TIRAMISU); below that it's always granted.
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    companion object {
        const val EXTRA_DEEP_LINK = "deepLink"

        private const val CHANNEL_ID = "default_channel"   // must match manifest meta-data!
        private const val CHANNEL_NAME = "General Notifications"
        private const val CHANNEL_DESCRIPTION = "App news and updates"
    }
}