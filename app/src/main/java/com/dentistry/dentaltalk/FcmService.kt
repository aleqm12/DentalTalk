package com.dentistry.dentaltalk

import android.app.NotificationManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FcmService: FirebaseMessagingService() {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        showNotification(message) // Llamar al método para mostrar la notificación
    }

    // Crear la notificación
    @RequiresApi(Build.VERSION_CODES.M)
    private fun showNotification(message: RemoteMessage) {
        val notificationManager =  getSystemService(NotificationManager::class.java)
        val notification = NotificationCompat.Builder(this, MyApp.NOTIFICATION_CHANNEL_ID)
            .setContentTitle(message.notification?.title)  // Título de la notificación
            .setContentText(message.notification?.body) // Cuerpo de la notificación
            .setSmallIcon(R.drawable.dental_app) // Icono pequeño
            .setAutoCancel(true) // Cancelar al tocar
            .build()

        // Mostrar la notificación
        notificationManager.notify(1, notification)
    }

}
