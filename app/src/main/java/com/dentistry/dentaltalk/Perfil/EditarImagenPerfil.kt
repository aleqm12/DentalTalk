package com.dentistry.dentaltalk.Perfil

import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dentistry.dentaltalk.R

class EditarImagenPerfil : AppCompatActivity() {

    private lateinit var BtnElegirImagen : Button
    private lateinit var BtnActualizarImagen : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_imagen_perfil)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

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

    private fun MostrarDialog(){
        val Btn_abrir_galeria: Button
        val Btn_abrir_camara : Button

        val dialog = Dialog(this@EditarImagenPerfil)

        dialog.setContentView(R.layout.cuadro_de_dialogo_seleccionar)

        Btn_abrir_galeria = dialog.findViewById(R.id.Btnabrirgaleria)
        Btn_abrir_camara = dialog.findViewById(R.id.BtnAbrircamara)

        Btn_abrir_galeria.setOnClickListener {

            Toast.makeText(applicationContext, "Abrir Galeria", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        Btn_abrir_camara.setOnClickListener {
            Toast.makeText(applicationContext, "Abrir Camara", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialog.show()


    }
}