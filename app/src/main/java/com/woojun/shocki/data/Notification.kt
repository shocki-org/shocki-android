package com.woojun.shocki.data

data class Notification(
    val notificationColor: NotificationColor,
    val notificationText: String,
    val content: String,
    val date: String,
)