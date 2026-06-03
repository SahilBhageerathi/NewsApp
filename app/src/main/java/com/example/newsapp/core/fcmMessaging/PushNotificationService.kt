package com.example.newsapp.core.fcmMessaging

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.newsapp.MainActivity
import com.example.newsapp.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.koin.android.ext.android.inject
import kotlin.jvm.java

class PushNotificationService :  FirebaseMessagingService() {

    private val notifier: LocalNotifier by inject()

    override fun onNewToken(token: String) {
        super.onNewToken(token)
         // Token changed → sync to backend
         // sendTokenToServer(token)
        Log.d(TAG, "FCM Token: $token")
    }

    //when a message is received
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val title = message.data["title"] ?: message.notification?.title ?: return
        val body  = message.data["body"]  ?: message.notification?.body  ?: ""
        val deepLink = message.data["deepLink"]   // optional: for navigation
        Log.d("onMessage Received", "Notifications body: $body")
        notifier.showNotification(title,body)
    }

    companion object {
        private const val TAG = "PushNotificationService"
    }



}