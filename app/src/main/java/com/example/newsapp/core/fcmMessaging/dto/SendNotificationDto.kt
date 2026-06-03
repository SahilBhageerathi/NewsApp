package com.example.newsapp.core.fcmMessaging.dto

data class SendNotificationDto (
    val to:String?,
    val notification : NotificationBody
)

data class NotificationBody (
    val title:String,
    val body:String,
)