package com.dentistry.dentaltalk

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class Inicio : AppCompatActivity() {


    private lateinit var Btn_ir_logeo : Button
    private lateinit var Btn_login_google : Button

    var firebaseUser: FirebaseUser?=null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_inicio)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }


        Btn_ir_logeo = findViewById(R.id.Btn_ir_logeo)
        Btn_login_google = findViewById(R.id.Btn_login_google)


        Btn_ir_logeo.setOnClickListener{

            val intent = Intent (this@Inicio,LoginActivity::class.java)
            Toast.makeText(applicationContext, "Login",Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }

        Btn_login_google.setOnClickListener{
            Toast.makeText(applicationContext, "Login con google",Toast.LENGTH_SHORT).show()
        }
    }

    private fun comprobarSesion(){
        firebaseUser = FirebaseAuth.getInstance().currentUser

        if (firebaseUser!=null){
            val intent = Intent(this@Inicio, MainActivity:: class.java)
            Toast.makeText(applicationContext, "La sesion esta activa",Toast.LENGTH_SHORT).show()
            startActivity(intent)
            finish()
        }
    }

    override fun onStart() {
        comprobarSesion()
        super.onStart()
    }
}