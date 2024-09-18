package com.woojun.shocki.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.woojun.shocki.R
import com.woojun.shocki.database.TokenManager
import com.woojun.shocki.dto.CategoryResponse
import com.woojun.shocki.dto.FCMResponse
import com.woojun.shocki.network.RetrofitAPI
import com.woojun.shocki.network.RetrofitClient
import com.woojun.shocki.view.main.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FCMService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        remoteMessage.notification?.let {
            sendNotification(it.title.toString(), it.body.toString())
        }

        remoteMessage.data.isNotEmpty().let {
            val title = remoteMessage.data["title"].toString()
            val body = remoteMessage.data["body"].toString()
            sendNotification(title, body)
        }
    }

    private fun sendNotification(title: String, body: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = "shocki_default_channel"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.logo_icon)
            .setColor(resources.getColor(R.color.black))
            .setBadgeIconType(R.drawable.logo_icon)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            channelId,
            "Shocki",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)
        notificationManager.notify(0, notificationBuilder.build())
    }

}