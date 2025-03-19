package com.dentistry.dentaltalk.Perfil

import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dentistry.dentaltalk.R

class EditarImagenPerfil : AppCompatActivity() {

    private lateinit var ImagenPerfilActualizar: ImageView
    private lateinit var BtnElegirImagen : Button
    private lateinit var BtnActualizarImagen : Button
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_imagen_perfil)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        ImagenPerfilActualizar = findViewById(R.id.ImagenPerfilActualizar)
        BtnElegirImagen = findViewById(R.id.BtnElegirImagenDe)
        BtnActualizarImagen = findViewById(R.id.BtnActualizarImagen)

        BtnElegirImagen.setOnClickListener {
            Toast.makeText(applicationContext, "Seleccionar imagen de", Toast.LENGTH_SHORT).show()
            MostrarDialog()
        }

        BtnActualizarImagen.setOnClickListener {
            Toast.makeText(applicationContext, "Actualizar imagen", Toast.LENGTH_SHORT).show()
        }
    }


    private fun AbrirGaleria(){

        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galeriaActivityResultLauncher.launch(intent)
    }

    private  val galeriaActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback <ActivityResult>{resultado->
            if (resultado.resultCode == RESULT_OK){
                val data = resultado.data
                imageUri = data!!.data
                ImagenPerfilActualizar.setImageURI(imageUri)
            }else{
                Toast.makeText(applicationContext, "Cancelado por el usuario", Toast.LENGTH_SHORT).show()
            }
        }
    )

    private fun AbrirCamara (){
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "Titulo")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Descripcion")
        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri)
        camaraActivityResultLauncher.launch(intent)
    }

    private  val camaraActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){resultado_camara->
            if (resultado_camara.resultCode == RESULT_OK){
                ImagenPerfilActualizar.setImageURI(imageUri)
            }else{
                Toast.makeText(applicationContext, "Cancelado por el usuario", Toast.LENGTH_SHORT).show()
            }
        }



    private fun MostrarDialog(){
        val Btn_abrir_galeria: Button
        val Btn_abrir_camara : Button

        val dialog = Dialog(this@EditarImagenPerfil)

        dialog.setContentView(R.layout.cuadro_de_dialogo_seleccionar)

        Btn_abrir_galeria = dialog.findViewById(R.id.Btnabrirgaleria)
        Btn_abrir_camara = dialog.findViewById(R.id.BtnAbrircamara)

        Btn_abrir_galeria.setOnClickListener {

            //Toast.makeText(applicationContext, "Abrir Galeria", Toast.LENGTH_SHORT).show()
            AbrirGaleria()
            dialog.dismiss()
        }

        Btn_abrir_camara.setOnClickListener {
            //Toast.makeText(applicationContext, "Abrir Camara", Toast.LENGTH_SHORT).show()
            AbrirCamara()
            dialog.dismiss()
        }

        dialog.show()


    }
}