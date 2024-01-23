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
import com.example.whatsappk.adapters.ConversasAdapter
import com.example.whatsappk.databinding.FragmentConversasBinding
import com.example.whatsappk.model.Chat
import com.example.whatsappk.model.User
import com.example.whatsappk.utils.Constants
import com.example.whatsappk.utils.showMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class ConversasFragment : Fragment() {

    private lateinit var binding: FragmentConversasBinding
    private lateinit var eventoSnapshot: ListenerRegistration
    private lateinit var conversasAdapter: ConversasAdapter

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
        binding = FragmentConversasBinding.inflate(inflater, container, false)

        conversasAdapter = ConversasAdapter {
            val intent = Intent(context, MensagensActivity::class.java)

            val userChat = User(
                id = it.idUserReceiver,
                name = it.name,
                photo = it.photo
            )
            intent.putExtra("dadosDestinatario", userChat)
//            intent.putExtra("origem", Constants.ORIGEM_CONVERSA)
            startActivity(intent)
        }
        binding.rvConversas.adapter = conversasAdapter
        binding.rvConversas.layoutManager = LinearLayoutManager(context)
        binding.rvConversas.addItemDecoration(
            DividerItemDecoration(
                context, LinearLayoutManager.VERTICAL
            )
        )

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        addListenerConversas()
    }

    override fun onDestroy() {
        super.onDestroy()
        eventoSnapshot.remove()
    }

    private fun addListenerConversas() {
        val idUserSender = firebaseAuth.currentUser?.uid
        eventoSnapshot = firestore
            .collection(Constants.BD_CONVERSAS)
            .document(idUserSender!!)
            .collection(Constants.BD_ULTIMAS_CONVERSAS)
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { querySnapshot, error ->
                if(error != null) {
                    activity?.showMessage("Erro ao recuperar conversas..")
                }
                val listChats = mutableListOf<Chat>()
                val docs = querySnapshot?.documents
                docs?.forEach {
                    val chat = it.toObject(Chat::class.java)
                    if(chat != null) {
                        Log.i("FragmentConversas", "Nome: ${chat.name} - ${chat.lastMessage}")
                            listChats.add(chat)
                        }else {
                        Log.i("FragmentConversas", "Não foram encontradas conversas")
                    }
                }

                // lista de contatos (atualizar no RecyclerView)
                if(listChats.isNotEmpty()) {
                    conversasAdapter.addList(listChats)
                }
            }
    }

}