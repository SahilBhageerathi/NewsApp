package com.example.newsapp.presentation.viewmodel

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.newsapp.data.notification.AppNotificationBuilder
import com.example.newsapp.data.notification.NotificationPermissionHelper
import com.example.newsapp.data.notification.NotificationType
import com.example.newsapp.presentation.navigation.AppNavigator
import com.example.newsapp.presentation.navigation.NavigationEffect

// ── NotificationViewModel.kt ──

class NotificationViewModel(
    application: Application,
    private val navigator: AppNavigator
) : AndroidViewModel(application) {

    private val context get() = getApplication<Application>()

    fun onPermissionResult(granted: Boolean) {
        if (granted) {
//            showWelcomeNotification()
        }
        // If denied, the UI layer handles showing a rationale
    }

    fun handleNotificationIntent(intent: Intent?) {
        val articleId = intent?.getStringExtra("article_id") ?: return

        Log.d("NOTIF", "Article ID from intent: $articleId")
        navigator.navigate(NavigationEffect.OpenDetailPageFormNotification(articleId))


    }

    fun showWelcomeNotification(articleId: String, title: String) {
        val hasPermission = NotificationPermissionHelper.hasPermission(context)
        Log.d("NOTIF", "Permission granted: $hasPermission")
        AppNotificationBuilder.show(
            context,
            NotificationType.General(
                title = title,
                body = "Notifications are now enabled.",
                articleId = articleId
            )
        )
    }

    fun showAlert(title: String, body: String) {
        AppNotificationBuilder.show(
            context,
            NotificationType.Alert(title = title, body = body)
        )
    }


}