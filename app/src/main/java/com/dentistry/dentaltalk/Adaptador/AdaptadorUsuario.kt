package com.dentistry.dentaltalk.Adaptador

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dentistry.dentaltalk.Chat.MensajesActivity
import com.dentistry.dentaltalk.Modelo.Usuario
import com.dentistry.dentaltalk.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.dentistry.dentaltalk.Modelo.Chat
import kotlin.jvm.java


class AdaptadorUsuario (context : Context, listaUsuarios : List <Usuario>, chatLeido: Boolean) : RecyclerView.Adapter<AdaptadorUsuario.ViewHolder>(){


    private val context: Context
    private val listaUsuarios : List<Usuario>
    private val chatLeido: Boolean
    var ultimoMensaje: String= ""

    init {
        this.context = context
        this.listaUsuarios = listaUsuarios
        this.chatLeido = chatLeido
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var  nombre_usuario : TextView
       // var email_usuario : TextView
        var imagen_usuario : ImageView
        var imagen_online : ImageView
        var imagen_offline : ImageView
        var TXT_ultimo_mensaje: TextView

        init {
            nombre_usuario = itemView.findViewById(R.id.Item_nombre_usuario)
            //email_usuario = itemView.findViewById(R.id.Item_email_usuario)
            imagen_usuario = itemView.findViewById(R.id.Item_image)
            imagen_online = itemView.findViewById(R.id.imagen_online)
            imagen_offline = itemView.findViewById(R.id.imagen_offline)
            TXT_ultimo_mensaje = itemView.findViewById(R.id.TXT_ultimo_mensaje)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view : View = LayoutInflater.from(context).inflate(R.layout.item_usuario,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listaUsuarios.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val usuario : Usuario = listaUsuarios[position]
        holder.nombre_usuario.text = usuario.getN_Usuario()
        //holder.email_usuario.text = usuario.getEmail()
        Glide.with(context).load(usuario.getImagen()).placeholder(R.drawable.ic_item_usuario).into(holder.imagen_usuario)

        holder.itemView.setOnClickListener{
            val intent = Intent(context, MensajesActivity::class.java)
            //Enviamos el uid del usuario seleccionado
            intent.putExtra("uid_usuario", usuario.getUid())
            Toast.makeText(context, "El usuario seleccionado es:" +usuario.getN_Usuario(),Toast.LENGTH_SHORT).show()
            context.startActivity(intent)
        }

        if (chatLeido){
            ObtenerUltimoMensaje(usuario.getUid(), holder.TXT_ultimo_mensaje)
        }else{
            holder.TXT_ultimo_mensaje.visibility = View.GONE
        }

        if (chatLeido){
            if (usuario.getEstado()=="online"){
                holder.imagen_online.visibility = View.VISIBLE
                holder.imagen_offline.visibility = View.GONE
            }else{
                holder.imagen_online.visibility = View.GONE
                holder.imagen_offline.visibility = View.VISIBLE
            }
        }
        else{
            holder.imagen_online.visibility = View.GONE
            holder.imagen_offline.visibility = View.GONE
        }


    }

    private fun ObtenerUltimoMensaje(ChatUsuarioUid: kotlin.String?, txtUltimoMensaje :TextView) {

        ultimoMensaje = "defaultMensaje"
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val reference = FirebaseDatabase.getInstance().reference.child("Chats")
        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot in snapshot.children){
                    val chat: Chat?= dataSnapshot.getValue(Chat::class.java)
                    if (firebaseUser!= null && chat!= null){
                        if (chat.getReceptor() == firebaseUser!!.uid &&
                            chat.getEmisor()==ChatUsuarioUid ||
                            chat.getReceptor() == ChatUsuarioUid &&
                            chat.getEmisor()== firebaseUser!!.uid){
                            ultimoMensaje =chat.getMensaje()!!
                        }
                    }
                }

                when(ultimoMensaje){
                    "defaultMensaje" -> txtUltimoMensaje.text = "No hay mensaje"
                    "Se ha enviado la imagen" -> txtUltimoMensaje.text = "Imagen enviada"
                    else ->  txtUltimoMensaje.text = ultimoMensaje
                }
                ultimoMensaje = "defaultMensaje"
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}