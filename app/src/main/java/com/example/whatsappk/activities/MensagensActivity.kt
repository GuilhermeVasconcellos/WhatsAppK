package com.example.whatsappk.activities

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.whatsappk.adapters.MensagensAdapter
import com.example.whatsappk.databinding.ActivityMensagensBinding
import com.example.whatsappk.model.Chat
import com.example.whatsappk.model.Message
import com.example.whatsappk.model.User
import com.example.whatsappk.utils.Constants
import com.example.whatsappk.utils.showMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso

@Suppress("DEPRECATION")
class MensagensActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMensagensBinding.inflate(layoutInflater)
    }
    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }
    private lateinit var snapshotListener: ListenerRegistration

    private var dadosDestinatario: User? = null
    private var dadosUsuarioLogado: User? = null

    private lateinit var mensagensAdapter: MensagensAdapter

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        recuperaDadosUsuarios()
        initToolbar()
        initClickEvents()
        initRecyclerView()
        initListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
        snapshotListener.remove()
    }

    private fun initRecyclerView() {
        with(binding) {
            mensagensAdapter = MensagensAdapter()
            rvMessages.adapter = mensagensAdapter
            rvMessages.layoutManager = LinearLayoutManager(applicationContext)
        }
    }

    private fun initListeners() {
        val idUserSender = firebaseAuth.currentUser?.uid
        val idUserReceiver = dadosDestinatario?.id
        if(idUserSender != null && idUserReceiver != null) {
            snapshotListener = firestore.collection(Constants.BD_MENSAGENS)
                .document(idUserSender)
                .collection(idUserReceiver)
                .orderBy("date", Query.Direction.ASCENDING)
                .addSnapshotListener { querySnapshot, error ->
                    if(error != null) {
                        showMessage("Erro ao recuperar as mensagens...")
                    }

                    val listaMensagens = mutableListOf<Message>()
                    val docs = querySnapshot?.documents
                    docs?.forEach {
                        val message = it.toObject(Message::class.java)
                        if(message != null) {
                            listaMensagens.add(message)
                        }
                    }

                    if(listaMensagens.isNotEmpty()) {
                        // carregar informações no adapter
                        mensagensAdapter.addList(listaMensagens)
                    }

                }
        }
    }

    private fun initClickEvents() {
        binding.fabSendMessage.setOnClickListener {
            val txtMsg = binding.editMessage.text.toString()
            saveMessage(txtMsg)
        }
    }

    private fun saveMessage(textMsg: String) {
        if(textMsg.isNotEmpty()) {
            if(dadosUsuarioLogado != null && dadosDestinatario != null) {
                val msg = Message(
                    dadosUsuarioLogado!!.id,
                    textMsg
                )
                // salva mensagem para o remetente na nuvem
                firestore.collection(Constants.BD_MENSAGENS)
                    .document(dadosUsuarioLogado!!.id)
                    .collection(dadosDestinatario!!.id)
                    .add(msg)
                    .addOnFailureListener {
                        showMessage("Erro ao enviar a mensagem...")
                    }
                // salva foto e nome destinatário -> exibir na aba Conversas
                val chatSender = Chat(
                    dadosUsuarioLogado!!.id,
                    dadosDestinatario!!.id,
                    dadosDestinatario!!.photo,
                    dadosDestinatario!!.name,
                    textMsg
                )
                // salva conversa no Firestore
                firestore.collection(Constants.BD_CONVERSAS)
                    .document(dadosUsuarioLogado!!.id)
                    .collection(Constants.BD_ULTIMAS_CONVERSAS)
                    .document(dadosDestinatario!!.id)
                    .set(chatSender)
                    .addOnFailureListener {
                        showMessage("erro ao salvar conversa...")
                    }
                // salva a mesma mensagem para o destinatário na nuvem
                firestore.collection(Constants.BD_MENSAGENS)
                    .document(dadosDestinatario!!.id)
                    .collection(dadosUsuarioLogado!!.id)
                    .add(msg)
                    .addOnFailureListener {
                        showMessage("Erro ao enviar a mensagem...")
                    }
                // salva foto e nome remetente -> exibir na aba Conversas
                val chatReceiver = Chat(
                    dadosDestinatario!!.id,
                    dadosUsuarioLogado!!.id,
                    dadosUsuarioLogado!!.photo,
                    dadosUsuarioLogado!!.name,
                    textMsg
                )

                binding.editMessage.setText("")
            }
        }
    }

    private fun initToolbar() {
        val toolbar = binding.tbMessages
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = ""
            if(dadosDestinatario != null) {

                binding.tvNomePerfil.text = dadosDestinatario!!.name
                Picasso.get()
                    .load(dadosDestinatario!!.photo)
                    .into(binding.ivFotoPerfil)
            }else{
                Log.i("FragmentContatos", dadosDestinatario.toString())
            }
            setDisplayHomeAsUpEnabled(true)
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun recuperaDadosUsuarios() {

        // dados Remetente (usuário logado)
        firestore.collection("users")
            .document(firebaseAuth.currentUser!!.uid)
            .get()
            .addOnSuccessListener {
                val loggedUser = it.toObject(User::class.java)
                dadosUsuarioLogado = loggedUser
            }

        // dados Destinatário
        val extras = intent.extras
        if(extras != null) {

            // recuperar os dados do destinatário
            dadosDestinatario = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                extras.getParcelable(
                    "dadosDestinatario",
                    User::class.java
                )
            } else {
                extras.getParcelable(
                    "dadosDestinatario"
                )
            }
        }
    }
}