package com.example.whatsappk.activities

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.whatsappk.databinding.ActivityPerfilBinding
import com.example.whatsappk.utils.showMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class PerfilActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityPerfilBinding.inflate(layoutInflater)
    }

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val storage by lazy {
        FirebaseStorage.getInstance()
    }
    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private var idUser: String? = null
    private var hasPermissionCamera = false
    private var hasPermissionGallery = false

    private val permissioManager = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ){
        if( it != null) {
            // mostra a imagem selecionada na Galeria
            binding.ivPerfil.setImageURI(it)
            // faz o upload para o Firebase Storage
            uploadImageStorage(it)
        }else {
            showMessage("Nenhuma imagem selecionada.")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initToolbar()
        requestPermisions()
        initClickEvents()
        idUser = firebaseAuth.currentUser?.uid
//        showMessage("id: $idUser")
    }

    override fun onStart() {
        super.onStart()
        recuperarDadosPerfil()
    }

    private fun recuperarDadosPerfil() {
        firestore
            .collection("users")
            .document(idUser!!)
            .get()
            .addOnSuccessListener {
                val dados = it.data
                if(dados != null) {
                    val nome = dados["name"] as String
                    val foto = dados["photo"] as String

                    binding.editNomePerfil.setText(nome)
                    if(foto.isNotEmpty()) {
                        Picasso.get()
                            .load(foto)
                            .into(binding.ivPerfil)
                    }
                }
            }

    }

    private fun uploadImageStorage(uri: Uri) {
        // photos -> users -> id_user -> profile.jpg
        if(idUser != null) {
            storage
                .getReference("photos")
                .child("users")
                .child(idUser!!)
                .child("profile.jpg")
                .putFile(uri)
                .addOnSuccessListener {
                    showMessage("Imagem carregada na nuvem...")
                    it.metadata
                        ?.reference
                        ?.downloadUrl
                        ?.addOnSuccessListener {
                            val dados = mapOf(
                                "photo" to it.toString()
                            )
                            updateProfileData(idUser!!, dados)
                        }
                }.addOnFailureListener {
                    showMessage("Erro ao fazer o upload da imagem...")
                }
        }

    }

    private fun updateProfileData(idUser: String, dados: Map<String, String>) {
        firestore
            .collection("users")
            .document(idUser)
            .update(dados)
            .addOnSuccessListener {
                showMessage("Perfil atualizado com sucesso!")
            }
            .addOnFailureListener {
                showMessage("Erro ao atualizar o perfil...")
            }
    }

    private fun initClickEvents() {

        binding.fabCarregarImagem.setOnClickListener {
            if(hasPermissionGallery) {
                permissioManager.launch("image/*")
            }else {
                showMessage("Sem permissão para acessar a Galeria")
                requestPermisions()
            }
        }
        binding.btnAtualizar.setOnClickListener {
            val nomeUsuario = binding.editNomePerfil.text.toString()
            if(nomeUsuario.isNotEmpty()) {
                val idUser = firebaseAuth.currentUser?.uid
                if(idUser != null) {
                    val dados = mapOf(
                        "name" to nomeUsuario
                    )
                    updateProfileData(idUser, dados)
                }
            }else {
                showMessage("Preencha o nome de perfil do usuário...")
            }


        }
    }

    private fun requestPermisions() {
        // verifica se o usuário já tem a permissão
        hasPermissionCamera = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            hasPermissionGallery = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        }else {
//            showMessage("Verificar permissão para acesso a Galeria!")
            hasPermissionGallery = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }

        // Lista de permissões negadas
        val listaPermissoesNegadas = mutableListOf<String>()
        if(!hasPermissionCamera) listaPermissoesNegadas.add(Manifest.permission.CAMERA)
        if(!hasPermissionGallery) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                listaPermissoesNegadas.add(Manifest.permission.READ_MEDIA_IMAGES)
            }else {
                listaPermissoesNegadas.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        if(listaPermissoesNegadas.isNotEmpty()) {
            // solicita múltiplas permissões
            val permissionManager = registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) {
                hasPermissionCamera = it[Manifest.permission.CAMERA] ?: hasPermissionCamera
                hasPermissionGallery = it[if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    Manifest.permission.READ_MEDIA_IMAGES
                } else {
                    showMessage("Versão do Android precisa ser atualizada!")
                    Manifest.permission.READ_EXTERNAL_STORAGE
                }] ?: hasPermissionGallery
            }
            permissionManager.launch(listaPermissoesNegadas.toTypedArray())
        }
    }

    private fun initToolbar() {
        val toolbar = binding.incToolbarPerfil.materialToolbar
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Editar perfil"
            setDisplayHomeAsUpEnabled(true)
        }
    }
}