package com.example.whatsappk.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.whatsappk.activities.MensagensActivity
import com.example.whatsappk.adapters.ContatosAdapter
import com.example.whatsappk.databinding.FragmentContatosBinding
import com.example.whatsappk.model.User
import com.example.whatsappk.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class ContatosFragment : Fragment() {

    private lateinit var binding: FragmentContatosBinding
    private lateinit var eventoSnapshot: ListenerRegistration
    private lateinit var contatosAdapter: ContatosAdapter

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentContatosBinding.inflate(inflater, container, false)

        contatosAdapter = ContatosAdapter {
            val intent = Intent(context, MensagensActivity::class.java)
            intent.putExtra("dadosDestinatario", it)
//            intent.putExtra("origem", Constants.ORIGEM_CONTATO)
            startActivity(intent)
        }
        binding.rvContatos.adapter = contatosAdapter
        binding.rvContatos.layoutManager = LinearLayoutManager(context)
        binding.rvContatos.addItemDecoration(
            DividerItemDecoration(
                context, LinearLayoutManager.VERTICAL
            )
        )

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        addListenerContatos()
    }

    override fun onDestroy() {
        super.onDestroy()
        eventoSnapshot.remove()
    }

    private fun addListenerContatos() {
        eventoSnapshot = firestore
            .collection(Constants.BD_USUARIOS)
            .addSnapshotListener { querySnapshot, _ ->
                val listUsers = mutableListOf<User>()
                val docs = querySnapshot?.documents
                docs?.forEach {
                    val user = it.toObject(User::class.java)
                    if(user != null) {
                        Log.i("FragmentContatos", "Nome: ${user.name}")
                        if(user.id != firebaseAuth.currentUser?.uid) {
                            listUsers.add(user)
                        }
                    }else {
                        Log.i("FragmentContatos", "Não foram encontrados contatos")
                    }
                }

                // lista de contatos (atualizar no RecyclerView)
                if(listUsers.isNotEmpty()) {
                    contatosAdapter.addList(listUsers)
                }
            }
    }

}