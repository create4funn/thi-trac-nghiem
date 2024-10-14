package com.example.thitracnghiem.FCMService

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.thitracnghiem.Activity.MainActivity
import com.example.thitracnghiem.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Kiểm tra nếu tin nhắn có chứa thông báo
        remoteMessage.notification?.let {
            sendNotification(it.body.toString(), it.title.toString())
        }

        // Kiểm tra nếu tin nhắn có chứa dữ liệu
        remoteMessage.data.isNotEmpty().let {
            val title = remoteMessage.data["title"] ?: "No title"
            val body = remoteMessage.data["body"] ?: "No body"
            sendNotification(body, title)  // Hoặc xử lý theo logic của bạn
        }
    }

    private fun sendNotification(messageBody: String, title: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        val channelId = "default_channel"
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.baseline_directions_24)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Tạo kênh thông báo nếu phiên bản Android là Oreo trở lên
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel title",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }
}

