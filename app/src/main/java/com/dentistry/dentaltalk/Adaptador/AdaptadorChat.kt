package com.dentistry.dentaltalk.Adaptador

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dentistry.dentaltalk.Modelo.Chat
import com.dentistry.dentaltalk.R
import com.github.chrisbanes.photoview.PhotoView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase


class AdaptadorChat(contexto: Context, chatLista:List<Chat>, imagenUrl: String)
    :RecyclerView.Adapter<AdaptadorChat.ViewHolder?>(){

    private val contexto : Context
    private val chatLista: List<Chat>
    private val imagenUrl: String //Url de la imagen
    var firebaseUser : FirebaseUser =FirebaseAuth.getInstance().currentUser!! // Usuario actual de Firebase

    //Se inician las variables
    init {
        this.contexto = contexto
        this.chatLista = chatLista
        this.imagenUrl = imagenUrl
    }

    // Clase interna para manejar las vistas de cada elemento del RecyclerView
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        /*Vistas de item mensaje izquierdo*/
        var imagen_perfil_mensaje: ImageView?=null
        var TXT_ver_mensaje: TextView?=null
        var imagen_enviada_izquierdo: ImageView?=null
        var TXT_mensaje_visto : TextView?=null


        /*Vistas de item mensaje derecho*/
        var imagen_enviada_derecha: ImageView?=null

        init {
            // Asigna las vistas a las variables
            imagen_perfil_mensaje = itemView.findViewById(R.id.imagen_perfil_mensaje)
            TXT_ver_mensaje = itemView.findViewById(R.id.TXT_ver_mensaje)
            imagen_enviada_izquierdo = itemView.findViewById(R.id.imagen_enviada_izquierdo)
            TXT_mensaje_visto = itemView.findViewById(R.id.TXT_mensaje_visto)
            imagen_enviada_derecha = itemView.findViewById(R.id.imagen_enviada_derecha)
        }


    }
    // Crea un nuevo ViewHolder para un mensaje
    override fun onCreateViewHolder(parent: ViewGroup, position:Int): ViewHolder {
        return if (position == 1){
            // Si es un mensaje enviado por el usuario
            val view : View = LayoutInflater.from(contexto).inflate(com.dentistry.dentaltalk.R.layout.item_mensaje_derecho, parent, false)
            ViewHolder(view)
        }else{
            // Si es un mensaje recibido
            val view: View = LayoutInflater.from(contexto).inflate(com.dentistry.dentaltalk.R.layout.item_mensaje_izquierdo,parent,false)
            ViewHolder(view)
        }
    }

    // Retorna el número de elementos en la lista
    override fun getItemCount(): Int {
        return chatLista.size
    }

    // Asigna los datos a las vistas del ViewHolder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val chat : Chat = chatLista[position]// Obtiene el mensaje actual
        // Carga la imagen de perfil del usuario
        Glide.with(contexto).load(imagenUrl).placeholder(R.drawable.ic_imagen_chat).into(holder.imagen_perfil_mensaje!!)

        /*Si el mensaje contiene una imagen*/
        if (chat.getMensaje().equals("Se ha enviado la imagen") && !chat.getUrl().equals("")){

            /*Condicion es para el usuario que envia una imagen como mensaje*/
            if (chat.getEmisor().equals(firebaseUser!!.uid)){
                holder.TXT_ver_mensaje!!.visibility = View.GONE
                holder.imagen_enviada_derecha!!.visibility = View.VISIBLE
                Glide.with(contexto).load(chat.getUrl()).placeholder(R.drawable.ic_imagen_enviada).into(holder.imagen_enviada_derecha!!)

                // Acción al hacer clic en la imagen enviada
                holder.imagen_enviada_derecha!!.setOnClickListener {
                    val opciones = arrayOf<CharSequence>("Ver imagen completa","Eliminar imagen", "Cancelar")
                    val builder : AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("¿Que desea realizar?")
                    builder.setItems(opciones, DialogInterface.OnClickListener{
                        dialogInterface, i ->
                        if (i == 0){
                            VisualizarImagen(chat.getUrl())
                        }
                        else if (i == 1){
                            EliminarMensaje(position, holder)
                        }
                    })
                    builder.show()
                }
            }
            /*Condicion  para el usuario  el cual nos envia una imagen como mensaje*/
            else if (!chat.getEmisor().equals(firebaseUser!!.uid)){
                holder.TXT_ver_mensaje!!.visibility = View.GONE
                holder.imagen_enviada_izquierdo!!.visibility = View.VISIBLE
                Glide.with(contexto).load(chat.getUrl()).placeholder(R.drawable.ic_imagen_enviada).into(holder.imagen_enviada_izquierdo!!)

                // Acción al hacer clic en la imagen recibida
                holder.imagen_enviada_izquierdo!!.setOnClickListener {
                    val opciones = arrayOf<CharSequence>("Ver imagen completa","Cancelar")
                        val builder : AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("¿Que desea realizar?")
                    builder.setItems(opciones, DialogInterface.OnClickListener{
                            dialogInterface, i ->
                        if (i == 0){
                            VisualizarImagen(chat.getUrl())
                        }

                    })
                    builder.show()
                }

            }
        }

        /*Si el mensaje contiene solo texto*/

        else{
            holder.TXT_ver_mensaje!!.text = chat.getMensaje()
            if (firebaseUser!!.uid == chat.getEmisor()){
                holder.TXT_ver_mensaje!!.setOnClickListener {
                    val opciones = arrayOf<CharSequence>("Eliminar mensaje", "Cancelar")
                    val builder : AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("¿Que desea realizar?")
                    builder.setItems(opciones, DialogInterface.OnClickListener{
                            dialogInterface, i->
                        if (i == 0){
                            EliminarMensaje(position, holder)
                        }
                    })
                    builder.show()
                }
            }
        }



    }
    // Determina el tipo de vista (derecha o izquierda) basado en el emisor
    override fun getItemViewType(position: Int): Int {
        return if (chatLista[position].getEmisor().equals(firebaseUser!!.uid)){
            1
        }else{
            0
        }
    }
    // Muestra una imagen en un diálogo
    private fun VisualizarImagen(imagen: String?){
        val Img_visualizar: PhotoView
        val Btn_cerrar_v : Button

        val dialog = Dialog(contexto)

        dialog.setContentView(R.layout.visualizar_imagen_completa)

        Img_visualizar = dialog.findViewById(R.id.Img_visualizar)
        Btn_cerrar_v = dialog.findViewById(R.id.Btn_cerrar_v)


        Glide.with(contexto).load(imagen).placeholder(R.drawable.ic_imagen_enviada).into(Img_visualizar)

        Btn_cerrar_v.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
        dialog.setCanceledOnTouchOutside(false)
    }

    // Elimina un mensaje de la base de datos
    private fun EliminarMensaje(position: Int, holder: ViewHolder){

        val reference = FirebaseDatabase.getInstance().reference.child("Chats")
            .child(chatLista.get(position).getId_Mensaje()!!)
            .removeValue()
            .addOnCompleteListener { tarea->
                if (tarea.isSuccessful){
                    Toast.makeText(holder.itemView.context,"Mensaje eliminado", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(holder.itemView.context,"No se ha eliminado el mensaje, intente nuevamente", Toast.LENGTH_SHORT).show()
                }
            }

    }
}