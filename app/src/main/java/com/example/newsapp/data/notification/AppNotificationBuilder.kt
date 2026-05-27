package com.example.newsapp.data.notification

/**
 * Sealed class representing notification types your app supports.
 * Adding a new type? Just add a new data class here — the builder handles the rest.
 *
 * WHY SEALED CLASS:
 * - Exhaustive when() checks at compile time
 * - Each type carries its own data — no stringly-typed bundles
 * - Easy to add new types without modifying existing code
 */
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.newsapp.R

/**
 * Sealed class for notification types.
 * Need a new type later? Add a data class here + a builder method below.
 */
sealed class NotificationType {

    abstract val channelId: String
    abstract val id: Int

    data class General(
        val title: String,
        val body: String,
        val articleId: String? = null,
        override val id: Int = DEFAULT_ID,
    ) : NotificationType() {
        override val channelId = NotificationChannelManager.Channels.GENERAL
    }

    data class Alert(
        val title: String,
        val body: String,
        override val id: Int = ALERT_ID,
    ) : NotificationType() {
        override val channelId = NotificationChannelManager.Channels.ALERTS
    }

    companion object {
        const val DEFAULT_ID = 1000
        const val ALERT_ID = 2000
    }
}

/**
 * Builds and shows notifications. Stateless — no references held.
 *
 * USAGE:
 *   AppNotificationBuilder.show(context, NotificationType.General(
 *       title = "Hello",
 *       body = "This is a notification"
 *   ))
 */
object AppNotificationBuilder {

    @DrawableRes
    private val DEFAULT_SMALL_ICON = androidx.core.R.drawable.notification_icon_background // ← replace with your icon

    fun show(context: Context, type: NotificationType) {
        if (!NotificationPermissionHelper.hasPermission(context)) return

        val notification = build(context, type)
        NotificationManagerCompat.from(context).notify(type.id, notification)
    }

    fun cancel(context: Context, notificationId: Int) {
        NotificationManagerCompat.from(context).cancel(notificationId)
    }

    fun cancelAll(context: Context) {
        NotificationManagerCompat.from(context).cancelAll()
    }

    private fun build(context: Context, type: NotificationType): Notification {
        return when (type) {
            is NotificationType.General -> buildGeneral(context, type)
            is NotificationType.Alert -> buildAlert(context, type)
        }
    }

    private fun buildGeneral(
        context: Context,
        type: NotificationType.General
    ): Notification {
        return baseBuilder(context, type.channelId)
            .setContentTitle(type.title)
            .setContentText(type.body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(type.body))
            .setAutoCancel(true)
            .setContentIntent(createContentIntent(context, type))
            .build()
    }

    private fun buildAlert(
        context: Context,
        type: NotificationType.Alert
    ): Notification {
        return baseBuilder(context, type.channelId)
            .setContentTitle(type.title)
            .setContentText(type.body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(type.body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setContentIntent(createContentIntent(context, type))
            .build()
    }

    // ── Helpers ──

    private fun baseBuilder(
        context: Context,
        channelId: String
    ): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, channelId)
            .setSmallIcon(DEFAULT_SMALL_ICON)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
    }

    private fun createContentIntent(
        context: Context,
        type: NotificationType
    ): PendingIntent {
        val intent = Intent(context, Class.forName("com.example.newsapp.MainActivity")).apply {
            flags =  Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("notification_type", type::class.simpleName)

            when (type) {
                is NotificationType.General -> {
                    type.articleId?.let { putExtra("article_id", it) }
                }
                is NotificationType.Alert -> { }
            }
        }

        return PendingIntent.getActivity(
            context,
            type.id,
            intent,
            pendingIntentFlags()
        )
    }

    private fun pendingIntentFlags(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
    }
}