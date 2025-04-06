package com.dentistry.dentaltalk

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging


class MyApp : Application() {
    companion object{
        const val NOTIFICATION_CHANNEL_ID = "DentalTalkNotificaciones" // ID del canal de notificaciones
    }
    override fun onCreate() {
        super.onCreate()
        // Obtener el token de Firebase Messaging
        Firebase.messaging.token.addOnCompleteListener {
            // Verificar si la obtención del token fue exitosa
            if (!it.isSuccessful){
                println("El tokeno no fue generado") // Mensaje de error
                return@addOnCompleteListener
            }

            val token = it.result // Obtener el token
            println("El token es: $token")
        }
        createNotificationChannel() // Crear el canal de notificaciones

    }

    // Método para crear el canal de notificaciones
    private fun createNotificationChannel (){
        // Comprobar si la versión de Android es Oreo o superio
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Notificaciones de DentalTalk",
                NotificationManager.IMPORTANCE_HIGH
            )

            channel.description = "Estas Notificaciones van a ser recibidas desde FCM"
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}