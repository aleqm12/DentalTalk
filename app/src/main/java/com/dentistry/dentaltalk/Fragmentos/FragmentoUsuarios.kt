package com.dentistry.dentaltalk.Fragmentos

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dentistry.dentaltalk.Adaptador.AdaptadorUsuario
import com.dentistry.dentaltalk.Modelo.Usuario
import com.dentistry.dentaltalk.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class FragmentoUsuarios : Fragment() {

    private var usuarioAdaptador : AdaptadorUsuario?=null
    private var usuarioLista : List <Usuario>?=null
    private var rvUsuarios: RecyclerView?=null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_fragmento_usuarios, container, false)

        rvUsuarios = view.findViewById(R.id.RV_usuarios)
        rvUsuarios!!.setHasFixedSize(true)
        rvUsuarios!!.layoutManager = LinearLayoutManager (context)


        usuarioLista = ArrayList()
        ObtenerUsuarioBD()



        return view
    }

    private fun ObtenerUsuarioBD() {
        val firebaseUser = FirebaseAuth.getInstance().currentUser!!.uid
        val reference = FirebaseDatabase.getInstance().reference.child("Usuarios").orderByChild("n_usuario")
        reference.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                (usuarioLista as ArrayList<Usuario>).clear()
                for (sh in snapshot.children){
                    val usuario : Usuario?=sh.getValue(Usuario::class.java)
                    if (!(usuario!!.getUid()).equals(firebaseUser)){
                        (usuarioLista as ArrayList<Usuario>).add(usuario)
                    }
                }

                usuarioAdaptador = AdaptadorUsuario(context!!, usuarioLista!!)
                rvUsuarios!!.adapter = usuarioAdaptador
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

}