package com.dentistry.dentaltalk

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)
        MostrarBienvenida()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    //Funcion para mostrar la bienvenida
    private fun MostrarBienvenida (){

        //Funcion COuntDownTimer: Permite ejecutar ciertas lineas de codigo pasado un cierto tiempo.
        //5000= seg, 1000=1seg y este segundo paramtero es el coneo regresivo para que desaparezca.

        object: CountDownTimer(5000, 1000){
            override fun onTick(millisUntilFinished: Long) {
                //TODO("Not yet implemented")
            }
            //Despues de los 5 segundos pasamos a la ActivityMain
            override fun onFinish() {
               val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
                finish()
            }

        }.start()

    }
}