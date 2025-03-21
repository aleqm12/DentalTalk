package com.dentistry.dentaltalk.Perfil

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.dentistry.dentaltalk.Modelo.Usuario
import com.dentistry.dentaltalk.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

private lateinit var P_imagen: ImageView
private lateinit var P_n_usuario: TextView
private lateinit var P_email: TextView
private lateinit var P_nombres: EditText
private lateinit var P_apellidos: EditText
private lateinit var P_profesion: EditText
private lateinit var P_domicilio: EditText
private lateinit var P_edad: EditText
private lateinit var P_telefono: EditText
private lateinit var Btn_guardar:Button
private lateinit var Editar_imagen : ImageView

var user: FirebaseUser?=null
var reference: DatabaseReference?=null

class PerfilActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_perfil)
        IncializarVariables()
        ObtenerDatos()
        Btn_guardar.setOnClickListener {
            ActualizarInformacion()
        }

        Editar_imagen.setOnClickListener{
           val intent = Intent(applicationContext, EditarImagenPerfil::class.java)
            startActivity(intent)

        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }
    }

    private fun IncializarVariables(){

        P_imagen = findViewById(R.id.P_imagen)
        P_n_usuario = findViewById(R.id.P_n_usuario)
        P_email = findViewById(R.id.P_email)
        P_nombres = findViewById(R.id.P_nombres)
        P_apellidos = findViewById(R.id.P_apellidos)
        P_profesion = findViewById(R.id.P_profesion)
        P_domicilio = findViewById(R.id.P_domicilio)
        P_edad = findViewById(R.id.P_edad)
        P_telefono = findViewById(R.id.P_telefono)
        Btn_guardar = findViewById(R.id.Btn_guardar)
        Editar_imagen = findViewById(R.id.Eitar_imagen)

        user = FirebaseAuth.getInstance().currentUser
        reference = FirebaseDatabase.getInstance().reference.child("Usuarios").child(user!!.uid)


    }

    private fun ObtenerDatos (){

        reference!!.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){

                    //Obtenemos los datos de FIrebase
                    val usuario : Usuario?=snapshot.getValue(Usuario::class.java)
                    val str_n_usuario = usuario!!.getN_Usuario()
                    val str_email = usuario.getEmail()
                    val str_nombres = usuario.getNombres()
                    val str_apellidos = usuario.getApellidos()
                    val str_profesion = usuario.getProfesion()
                    val str_domicilio = usuario.getDomicilio()
                    val str_edad = usuario.getEdad()
                    val str_telefono = usuario.getTelefono()

                    //Seteamos la informacion en las vistas

                    P_n_usuario.text = str_n_usuario
                    P_email.text = str_email
                    P_nombres.setText(str_nombres)
                    P_apellidos.setText(str_apellidos)
                    P_profesion.setText(str_profesion)
                    P_domicilio.setText(str_domicilio)
                    P_edad.setText(str_edad)
                    P_telefono.setText(str_telefono)
                    Glide.with(applicationContext).load(usuario.getImagen()).placeholder(R.drawable.ic_item_usuario).into(P_imagen)


                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })
    }

    private fun ActualizarInformacion(){

        val str_nombres = P_nombres.text.toString()
        val str_apellidos = P_apellidos.text.toString()
        val str_profesion= P_profesion.text.toString()
        val str_domicilio = P_domicilio.text.toString()
        val str_edad = P_edad.text.toString()
        val str_telefono = P_telefono.text.toString()

        val hashMap = HashMap<String, Any>()
        hashMap ["nombres"]= str_nombres
        hashMap["apellidos"] = str_apellidos
        hashMap ["profesion"] = str_profesion
        hashMap ["domicilio"] = str_domicilio
        hashMap ["edad"] = str_edad
        hashMap ["telefono"] = str_telefono


        reference!!.updateChildren(hashMap).addOnCompleteListener{task->

            if (task.isSuccessful){
                Toast.makeText(applicationContext,"Se han actualizado los datos", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(applicationContext,"No se han actualizado los datos", Toast.LENGTH_SHORT).show()
            }


        }.addOnFailureListener{ e->
            Toast.makeText(applicationContext,"Ha ocurrido un error ${e.message}", Toast.LENGTH_SHORT).show()
        }

    }
}