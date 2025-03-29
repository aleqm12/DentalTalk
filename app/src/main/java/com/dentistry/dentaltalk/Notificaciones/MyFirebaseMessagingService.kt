package com.dentistry.dentaltalk.Notificaciones

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import com.dentistry.dentaltalk.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.text.replace

class MyFirebaseMessagingService: FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val enviado = message.data["enviado"]
        val usuario = message.data["usuario"]
        val sharedPref = getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        val UsuarioActualConectado = sharedPref.getString("usuarioActual", "none")
        val firebaseUser = FirebaseAuth.getInstance().currentUser


        if (firebaseUser!=null && enviado == firebaseUser.uid){
            if (UsuarioActualConectado != null){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    EnviarNotificacionOreo(message)
                }else{
                    EnviarNotificacion(message)
                }
            }
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun EnviarNotificacion(message: RemoteMessage){
        val usuario = message.data["usuario"]
        val icono = message.data["icono"]
        val titulo = message.data ["titulo"]
        val cuerpo = message.data ["cuerpo"]

        val notificacion = message.notification
        val j = usuario!!.replace("[\\D]".toRegex(), "").toInt()
        val intent = Intent(this, MainActivity::class.java)

        val bundle = Bundle()
        bundle.putString("usuarioid", usuario)
        intent.putExtras(bundle)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this,j, intent, PendingIntent.FLAG_ONE_SHOT)
        val sonido = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val builder : NotificationCompat.Builder = NotificationCompat.Builder(this)
            .setSmallIcon(icono!!.toInt())
            .setContentTitle(titulo)
            .setContentText(cuerpo)
            .setAutoCancel(true)
            .setSound(sonido)
            .setContentIntent(pendingIntent)

        val noti = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        var i = 0
        if (j > 0){
            i = j
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun EnviarNotificacionOreo(message: RemoteMessage) {

        val usuario = message.data["usuario"]
        val icono = message.data["icono"]
        val titulo = message.data ["titulo"]
        val cuerpo = message.data ["cuerpo"]

        val notificacion = message.notification
        val j = usuario!!.replace("[\\D]".toRegex(), "").toInt()
        val intent = Intent(this, MainActivity::class.java)

        val bundle = Bundle()
        bundle.putString("usuarioid", usuario)
        intent.putExtras(bundle)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this,j, intent, PendingIntent.FLAG_ONE_SHOT)
        val sonido = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val oreoNotification = OreoNotification(this)

        val builder : Notification.Builder = oreoNotification.getOreoNotificacion(
            titulo, cuerpo, pendingIntent, sonido, icono
        )
        var i = 0
        if (j > 0){
            i = j
        }

        oreoNotification.getManager!!.notify(i, builder.build())
    }
}