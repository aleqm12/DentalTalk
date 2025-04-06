package com.dentistry.dentaltalk

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase

class Inicio : AppCompatActivity() {


    private lateinit var Btn_ir_logeo : MaterialButton
    private lateinit var Btn_login_google : MaterialButton


    var firebaseUser: FirebaseUser?=null
    private lateinit var auth: FirebaseAuth

    private lateinit var progressDialog: ProgressDialog
    private lateinit var mGoogleSignInClient: GoogleSignInClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_inicio)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }

        // Inicializar botones
        Btn_ir_logeo = findViewById(R.id.Btn_ir_logeo)
        Btn_login_google = findViewById(R.id.Btn_login_google)

        // Inicializar Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Configurar el ProgressDialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        // Configurar Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        // Crear el cliente de Google Sign-In
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        // Configurar el botón para ir a la actividad de inicio de sesión
        Btn_ir_logeo.setOnClickListener {

            val intent = Intent(this@Inicio, LoginActivity::class.java)
            startActivity(intent)
        }

        // Configurar el botón de inicio de sesión con Google
        Btn_login_google.setOnClickListener {
           EmpezarinicioSesionGoogle()
        }

    }

    // Iniciar el flujo de inicio de sesión de Google
    private fun EmpezarinicioSesionGoogle() {
        val googleSignIntent = mGoogleSignInClient.signInIntent
        googleSignInARL.launch(googleSignIntent)
    }

    // Manejar el resultado de la actividad de inicio de sesión de Google
    private val googleSignInARL = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) {resultado->
        if (resultado.resultCode == RESULT_OK){
            val data = resultado.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {

                val account = task.getResult(ApiException::class.java)
                AutenticarGoogleFirebase(account.idToken)

            }catch (e:Exception){
                Toast.makeText(applicationContext, "Ha ocurrido una excepcion debido a ${e.message}", Toast.LENGTH_SHORT).show()
            }

        }else {
            Toast.makeText(applicationContext, "Cancelado", Toast.LENGTH_SHORT).show()
        }

    }

    // Autenticar al usuario en Firebase con el token de Google
    private fun AutenticarGoogleFirebase(idToken: String?) {
        val credencial = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credencial)
            .addOnSuccessListener { authResult->
                if (authResult.additionalUserInfo!!.isNewUser){
                    GuardarInfoBD()

                /*Si el usuario previamente ya se registro*/
                }else{
                    startActivity(Intent(this, MainActivity::class.java))
                    finishAffinity()
                }

            }.addOnFailureListener { e->

                Toast.makeText(applicationContext, "${e.message}",Toast.LENGTH_SHORT).show()
            }

    }

    // Guardar la información del usuario en la base de datos
    private fun GuardarInfoBD(){

        progressDialog.setMessage("Se esta registrando su informacion...")
        progressDialog.show()

        /*Obtener informacion de una cuenta de google*/
        val uidGoogle = auth.uid
        val correoGoogle = auth.currentUser?.email
        val n_Google = auth.currentUser?.displayName
        val nombre_usuario_G : String = n_Google.toString()

        // Crear un mapa con la información del usuario
        val hashMap = HashMap<String, Any?>()
        hashMap["uid"]=uidGoogle
        hashMap["n_usuario"]= nombre_usuario_G
        hashMap["email"] = correoGoogle
        hashMap["imagen"]=""
        hashMap["buscar"]= nombre_usuario_G.lowercase()

        /*Nuevos datos de usuario*/

        hashMap["nombres"] = ""
        hashMap["apellidos"] = ""
        hashMap["edad"] = ""
        hashMap["profesion"] = ""
        hashMap["domicilio"]= ""
        hashMap["telefono"]= ""
        hashMap["estado"] = "offline"
        hashMap["proveedor"] = "Google"

        /* Referencia a la base de Datos*/
         val reference = FirebaseDatabase.getInstance().getReference("Usuarios")
        reference.child(uidGoogle!!)
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                startActivity(Intent(applicationContext, MainActivity::class.java))
                Toast.makeText(applicationContext, "Se ha registrado exitosamente",Toast.LENGTH_SHORT).show()
                finishAffinity()
            }
            .addOnFailureListener { e->
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "{${e.message}}",Toast.LENGTH_SHORT).show()

            }

    }

    // Comprobar si hay una sesión activa
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
        comprobarSesion() // Verificar sesión activa al iniciar
        super.onStart()
    }
}

