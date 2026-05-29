package com.example.newsapp.core.Notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.koin.core.context.GlobalContext

class CounterNotificationReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            "ACTION_INCREMENT" -> {
                val currentCount = intent.getIntExtra("count", 0)
                val newCount = currentCount + 1
                val notificationId = intent.getIntExtra("notification_id", 0)

                val service = GlobalContext.get().get<LocalNotificationService>()
                service.showNotification(
                    id = notificationId,
                    count = newCount
                )
            }
        }
    }
}