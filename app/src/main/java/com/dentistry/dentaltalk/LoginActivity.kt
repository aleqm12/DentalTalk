package com.dentistry.dentaltalk

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {


    private lateinit var L_Et_email:EditText
    private lateinit var L_Et_password:EditText
    private lateinit var Btn_login:Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        supportActionBar!!.title="Login"
        InicializarVariables()

        Btn_login.setOnClickListener{
            ValidarDatos()
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }



    private fun InicializarVariables(){
        L_Et_email = findViewById(R.id.L_Et_email)
        L_Et_password = findViewById(R.id.L_Et_password)
        Btn_login = findViewById(R.id.Btn_login)
        auth = FirebaseAuth.getInstance()
    }

    private fun ValidarDatos() {
        val email: String = L_Et_email.text.toString()
        val password: String = L_Et_password.text.toString()

        if (email.isEmpty()){
            Toast.makeText(applicationContext, "Ingrese su correo electrónico", Toast.LENGTH_SHORT).show()
        }

        if (password.isEmpty()){
            Toast.makeText(applicationContext, "Ingrese su contraseña", Toast.LENGTH_SHORT).show()
        }
        else{
            LoginUsuario(email,password)
        }


    }

    private fun LoginUsuario(email: String, password: String) {

        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener{task->
            if (task.isSuccessful){
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                Toast.makeText(applicationContext, "Ha iniciado sesión", Toast.LENGTH_SHORT).show()
                startActivity(intent)
                finish()

            }else {
                Toast.makeText(applicationContext, "Ha ocurrido un error", Toast.LENGTH_SHORT).show()
            }

        }.addOnFailureListener{e->

            Toast.makeText(applicationContext," {${e.message}}", Toast.LENGTH_SHORT).show()
        }

    }
}