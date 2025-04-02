package com.dentistry.dentaltalk.Perfil

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.dentistry.dentaltalk.Modelo.Usuario
import com.dentistry.dentaltalk.R
import com.github.chrisbanes.photoview.PhotoView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PerfilVisitado : AppCompatActivity() {

    private lateinit var PV_ImagenU: ImageView

    private lateinit var PV_NombreU: TextView
    private lateinit var PV_EmailU: TextView
    private lateinit var PV_Uid: TextView
    private lateinit var PV_nombres: TextView
    private lateinit var PV_apellidos: TextView
    private lateinit var PV_profesion: TextView
    private lateinit var PV_telefono: TextView
    private lateinit var PV_edad: TextView
    private lateinit var PV_domicilio: TextView
    private lateinit var PV_proveedor: TextView

    private lateinit var Btn_llamar: Button
    private lateinit var Btn_enviar_sms: Button

    var uid_usuario_visitado = ""

    var user: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_perfil_visitado)




        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        InicializarVistas()
        ObtenerUid()
        LeerInformacionUsuario()

        PV_ImagenU.setOnClickListener {
            ObtenerImagen()
        }

        Btn_llamar.setOnClickListener {
            //RealizarLlamada()
            if (ContextCompat.checkSelfPermission(applicationContext,
                    android.Manifest.permission.CALL_PHONE)== PackageManager.PERMISSION_GRANTED){
                RealizarLlamada()
            }else{
                requestCallPhonePermiso.launch(android.Manifest.permission.CALL_PHONE)
            }

        }

        Btn_enviar_sms.setOnClickListener {
            if (ContextCompat.checkSelfPermission(applicationContext,
                    Manifest.permission.SEND_SMS)== PackageManager.PERMISSION_GRANTED){
                EnviarSMS()
            }else{
                requestSendMessagePermiso.launch(Manifest.permission.SEND_SMS)
            }

        }
    }


    private fun ObtenerImagen(){
        val reference = FirebaseDatabase.getInstance().reference
            .child("Usuarios")
            .child(uid_usuario_visitado)

        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
               val usuario : Usuario? = snapshot.getValue(Usuario::class.java)
                //Obtener la imagen
                val imagen = usuario!!.getImagen()

                VisualizarImagen(imagen)
            }


            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun VisualizarImagen(imagen : String?) {

        val Img_visualizar: PhotoView
        val Btn_cerrar_v : Button

        val dialog = Dialog(this@PerfilVisitado)

        dialog.setContentView(R.layout.visualizar_imagen_completa)

        Img_visualizar = dialog.findViewById(R.id.Img_visualizar)
        Btn_cerrar_v = dialog.findViewById(R.id.Btn_cerrar_v)


        Glide.with(applicationContext).load(imagen).placeholder(R.drawable.ic_imagen_enviada).into(Img_visualizar)

        Btn_cerrar_v.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
        dialog.setCanceledOnTouchOutside(false)
    }

    private fun InicializarVistas(){
        PV_NombreU = findViewById(R.id.PV_NombreU)
        PV_EmailU = findViewById(R.id.PV_EmailU)
        PV_Uid = findViewById(R.id.PV_Uid)
        PV_nombres = findViewById(R.id.PV_nombres)
        PV_apellidos = findViewById(R.id.PV_apellidos)
        PV_profesion = findViewById(R.id.PV_profesion)
        PV_telefono = findViewById(R.id.PV_telefono)
        PV_edad = findViewById(R.id.PV_edad)
        PV_domicilio = findViewById(R.id.PV_domicilio)
        PV_proveedor = findViewById(R.id.PV_proveedor)
        PV_ImagenU = findViewById(R.id.PV_ImagenU)


        Btn_llamar = findViewById(R.id.Btn_llamar)
        Btn_enviar_sms = findViewById(R.id.Btn_enviar_SMS)

        user= FirebaseAuth.getInstance().currentUser
    }

    private fun ObtenerUid(){
        intent = intent
        uid_usuario_visitado = intent.getStringExtra("uid").toString()
    }

    private fun LeerInformacionUsuario(){

        val reference = FirebaseDatabase.getInstance().reference
            .child("Usuarios")
            .child(uid_usuario_visitado)

        reference.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val usuario : Usuario? = snapshot.getValue(Usuario::class.java)
                //Obtener informacion en tiempo real.

                PV_NombreU.text = usuario!!.getN_Usuario()
                PV_EmailU.text = usuario!!.getEmail()
                PV_Uid.text =usuario!!.getUid()
                PV_nombres.text = usuario!!.getNombres()
                PV_apellidos.text = usuario!!.getApellidos()
                PV_profesion.text = usuario!!.getProfesion()
                PV_telefono.text = usuario!!.getTelefono()
                PV_edad.text = usuario!!.getEdad()
                PV_domicilio.text = usuario!!.getDomicilio()
                PV_proveedor.text = usuario!!.getProveedor()

                Glide.with(applicationContext).load(usuario.getImagen())
                    .placeholder(R.drawable.imagen_usuario_visitado)
                    .into(PV_ImagenU)

            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }


        })
    }

    private fun RealizarLlamada(){

        val numeroUsuario = PV_telefono.text.toString()
        if (numeroUsuario.isEmpty()){
            Toast.makeText(applicationContext, "El Usuario no cuenta con un numero telefónico",
                Toast.LENGTH_SHORT).show()
        }else{
            val intent = Intent(Intent.ACTION_CALL)
            intent.setData(Uri.parse("tel: $numeroUsuario"))
            startActivity(intent)
        }
    }

    private fun EnviarSMS(){
        val numeroUsuario = PV_telefono.text.toString()
        if(numeroUsuario.isEmpty()){
            Toast.makeText(applicationContext, "El Usuario no cuenta con un numero telefónico", Toast.LENGTH_SHORT).show()
        }else{
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.setData(Uri.parse("smsto: $numeroUsuario"))
            intent.putExtra("sms_body","")
            startActivity(intent)
        }
    }

    private val requestCallPhonePermiso=
        registerForActivityResult(ActivityResultContracts.RequestPermission()){Permiso_concedido->
            if (Permiso_concedido){
                RealizarLlamada()
            }else{
                Toast.makeText(applicationContext, "El permiso de realizar llamadas telefónicas no ha sido concedido",
                    Toast.LENGTH_SHORT).show()
            }

        }

    private val requestSendMessagePermiso =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){Permiso_concedido->
            if (Permiso_concedido){
                EnviarSMS()
            }else{
                Toast.makeText(applicationContext, "El permiso de enviar SMS no ha sido concedido",
                    Toast.LENGTH_SHORT).show()
            }
        }

    private fun ActualizarEstado(estado: String){
        val reference = FirebaseDatabase.getInstance().reference.child("Usuarios")
            .child(user!!.uid)
        val hashMap = HashMap<String, Any>()
        hashMap["estado"]= estado
        reference!!.updateChildren(hashMap)
    }

    override fun onResume(){
        super.onResume()
        ActualizarEstado("online")
    }

    override fun onPause(){
        super.onPause()
        ActualizarEstado("offline")
    }



}