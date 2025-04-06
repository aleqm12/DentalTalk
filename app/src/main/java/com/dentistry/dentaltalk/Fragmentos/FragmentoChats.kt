package com.dentistry.dentaltalk.Fragmentos

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dentistry.dentaltalk.Adaptador.AdaptadorUsuario
import com.dentistry.dentaltalk.Modelo.ListaChats
import com.dentistry.dentaltalk.Modelo.Usuario
import com.dentistry.dentaltalk.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class FragmentoChats : Fragment() {

    private var usuarioAdaptador : AdaptadorUsuario?=null
    private var usuarioLista : List<Usuario>?=null
    private var usuarioListaChats : List<ListaChats>?=null
    lateinit var RV_ListaChats : RecyclerView
    private var firebaseUser: FirebaseUser?=null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla la vista para este fragmento
        val view: View = inflater.inflate(R.layout.fragment_fragmento_chats, container, false)

        // Configura el RecyclerView para mostrar la lista de chats
        RV_ListaChats = view.findViewById(R.id.RV_ListaChats)
        RV_ListaChats.setHasFixedSize(true)
        RV_ListaChats.layoutManager = LinearLayoutManager(context)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        usuarioListaChats = ArrayList()
        val ref = FirebaseDatabase.getInstance().reference.child("ListaMensajes").child(firebaseUser!!.uid)
        ref!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                (usuarioListaChats as ArrayList).clear()
                for (dataSnapshot in snapshot.children){
                    val chatList = dataSnapshot.getValue(ListaChats::class.java)
                    (usuarioListaChats as ArrayList).add(chatList!!)// Agrega a la lista
                }
                // Recupera la información de los usuarios para los chats
                RecuperarListaChats()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        return view
    }

    // Recupera la información de los usuarios correspondientes a los chats
    private fun RecuperarListaChats(){
        usuarioLista = ArrayList()
        val reference = FirebaseDatabase.getInstance().reference.child("Usuarios")
        reference!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                (usuarioLista as ArrayList).clear()
                for (dataSnapshot in snapshot.children){

                    val user = dataSnapshot.getValue(Usuario::class.java)  // Obtiene cada usuario
                    for (cadaLista in usuarioListaChats!!){
                        // Compara el UID del usuario con el UID en la lista de chats
                        if (user!!.getUid().equals(cadaLista.getUid())){
                            (usuarioLista as ArrayList).add(user)
                        }
                    }
                   usuarioAdaptador = AdaptadorUsuario(context!!, (usuarioLista as ArrayList<Usuario>), true)
                    RV_ListaChats.adapter = usuarioAdaptador
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}