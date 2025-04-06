package com.dentistry.dentaltalk

import android.app.ProgressDialog
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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegistroActivity : AppCompatActivity() {

    private lateinit var  R_Et_nombre_usuario : EditText
    private lateinit var  R_Et_email : EditText
    private lateinit var  R_Et_password: EditText
    private lateinit var  R_Et_r_password : EditText
    private lateinit var  Btn_registrar : Button

    private lateinit var  auth: FirebaseAuth
    private lateinit var rerference: DatabaseReference

    private lateinit var  progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro)
        //supportActionBar!!.title="Registro"
        InicializarVariables()  // Inicializa las variables de la interfaz

        // Configura el botón de registro para validar los datos
        Btn_registrar.setOnClickListener{
            ValidarDatos()
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


    // Inicializa los elementos de la interfaz
    private fun InicializarVariables (){
        R_Et_nombre_usuario = findViewById(R.id.R_Et_nombre_usuario)
        R_Et_email= findViewById(R.id.R_Et_email)
        R_Et_password =findViewById(R.id.R_Et_password)
        R_Et_r_password = findViewById(R.id.R_Et_r_password)
        Btn_registrar = findViewById(R.id.Btn_registrar)
        auth = FirebaseAuth.getInstance()// Instancia de Firebase.

        //Configurar Progress Dialog
        progressDialog= ProgressDialog(this)
        progressDialog.setTitle("Registrando informacion")
        progressDialog.setCanceledOnTouchOutside(false)
    }

    // Obtiene los datos ingresados por el usuario
    private fun ValidarDatos() {
        val nombre_usuario : String = R_Et_nombre_usuario.text.toString()
        val email: String = R_Et_email.text.toString()
        val password: String = R_Et_password.text.toString()
        val r_password: String = R_Et_r_password.text.toString()


        // Validaciones de los datos ingresados
        if (nombre_usuario.isEmpty()){
            Toast.makeText(applicationContext, "Ingrese nombre de usuario",Toast.LENGTH_SHORT).show()
        }
        else if (email.isEmpty()){
            Toast.makeText(applicationContext, "Ingrese su correo",Toast.LENGTH_SHORT).show()
        }

        else if (password.isEmpty()){
            Toast.makeText(applicationContext, "Ingrese su contraseña",Toast.LENGTH_SHORT).show()
        }
        else if (r_password.isEmpty()){
            Toast.makeText(applicationContext, "Por favor repita su contraseña",Toast.LENGTH_SHORT).show()
        }
        else if (!password.equals(r_password)){
            Toast.makeText(applicationContext, "Las contraseñas no coinciden",Toast.LENGTH_SHORT).show()
        }
        else{

            RegistrarUsuario(email,password)
        }
    }

    // Crea un nuevo usuario en Firebase
    private fun RegistrarUsuario(email: String, password: String) {
        progressDialog.setMessage("Espere Por favor")
        progressDialog.show()
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener{ task->
            if (task.isSuccessful){
                progressDialog.dismiss()
                var uid : String = ""
                uid = auth.currentUser!!.uid
                rerference = FirebaseDatabase.getInstance().reference.child("Usuarios").child(uid)

                // Crea un mapa con la información del usuario
                val hashMap = HashMap<String, Any>()
                val h_nombre_usuario : String = R_Et_nombre_usuario.text.toString()
                val h_email : String = R_Et_email.text.toString()

                hashMap["uid"]=uid
                hashMap["n_usuario"]=h_nombre_usuario
                hashMap["email"] = h_email
                hashMap["imagen"]=""
                hashMap["buscar"]= h_nombre_usuario.lowercase()

                /*Nuevos datos de usuario*/

                hashMap["nombres"] = ""
                hashMap["apellidos"] = ""
                hashMap["edad"] = ""
                hashMap["profesion"] = ""
                hashMap["domicilio"]= ""
                hashMap["telefono"]= ""
                hashMap["estado"] = "offline"
                hashMap["proveedor"] = "Email"

                // Actualiza la base de datos con la nueva información del usuario
                rerference.updateChildren(hashMap).addOnCompleteListener{task2->
                    if (task2.isSuccessful){
                        val intent = Intent(this@RegistroActivity,MainActivity::class.java)
                        Toast.makeText(applicationContext," Se ha registrado con exito", Toast.LENGTH_SHORT).show()
                        startActivity(intent)
                    }

                }.addOnFailureListener{e->
                    // Manejo de errores al actualizar la base de datos
                    Toast.makeText(applicationContext,"{${e.message}", Toast.LENGTH_SHORT).show()

                }
            }else{
                progressDialog.dismiss()
                // Manejo de errores al crear el usuario
                Toast.makeText(applicationContext,"Ha ocurrido un error", Toast.LENGTH_SHORT).show()
            }
        }
            .addOnFailureListener{e->
                progressDialog.dismiss()
                // Manejo de errores en la creación del usuario
                Toast.makeText(applicationContext,"{${e.message}", Toast.LENGTH_SHORT).show()

            }
    }
}