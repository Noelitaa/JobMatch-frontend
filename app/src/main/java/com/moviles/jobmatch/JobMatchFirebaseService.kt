package com.moviles.jobmatch

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.moviles.jobmatch.data.AuthSession
import com.moviles.jobmatch.data.repository.AppContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class JobMatchFirebaseService : FirebaseMessagingService() {

    companion object {
        const val CHANNEL_ID = "jobmatch_notifications"
        const val CHANNEL_NAME = "JobMatch"
        private const val TAG = "JobMatchFCM"
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "FCM token refreshed")
        val userId = AuthSession.currentUser?.userId ?: return
        CoroutineScope(Dispatchers.IO).launch {
            AppContainer.fcmRepository.registerToken(
                token = token,
                deviceInfo = "Android ${android.os.Build.VERSION.RELEASE}"
            )
            Log.d(TAG, "Refreshed token sent to backend for user $userId")
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val title = remoteMessage.notification?.title
            ?: remoteMessage.data["title"]
            ?: "JobMatch"
        val body = remoteMessage.notification?.body
            ?: remoteMessage.data["body"]
            ?: ""
        val type = remoteMessage.data["type"]
        val entityId = remoteMessage.data["entityId"]

        Log.d(TAG, "Notification received — type: $type, entityId: $entityId")
        showNotification(title, body, type, entityId)
    }

    private fun showNotification(title: String, body: String, type: String?, entityId: String?) {
        createChannel()

        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            putExtra("type", type)
            putExtra("entityId", entityId)
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun createChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notificaciones de JobMatch"
            enableVibration(true)
        }
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }
}
