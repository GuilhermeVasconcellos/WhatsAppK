package com.example.whatsappk.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.whatsappk.databinding.ActivityCadastroBinding
import com.example.whatsappk.model.User
import com.example.whatsappk.utils.showMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore

class CadastroActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityCadastroBinding.inflate(layoutInflater)
    }
    private lateinit var name: String
    private lateinit var email: String
    private lateinit var pwd: String
    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initToolbar()
        initClickEvents()
    }

    private fun initClickEvents() {
        binding.btnCadastrar.setOnClickListener {
            if(validarCampos()) {
                enrollUser(name, email, pwd)
            }
        }
    }

    private fun enrollUser(name: String, email: String, pwd: String) {
        firebaseAuth.createUserWithEmailAndPassword(
            email,
            pwd
        ).addOnCompleteListener {
            if(it.isSuccessful) {
                // salva usuário no banco de dados (nuvem) (id/name, email, photo)
                val idUser = it.result.user?.uid
                if(idUser != null) {
                    val user = User(
                        idUser, name, email
                    )
                    salvarUserFirestore(user)
                }
            }
        }.addOnFailureListener {
            try{
                throw it
            }catch(errorWeakPwd: FirebaseAuthWeakPasswordException) {
                errorWeakPwd.printStackTrace()
                showMessage("Senha fraca... utilize letras, números e caracteres especiais...")
            }catch(errorDuplicateUser: FirebaseAuthUserCollisionException) {
                errorDuplicateUser.printStackTrace()
                showMessage("Usuário já existente...")
            }catch(errorInvalidCredentials: FirebaseAuthInvalidCredentialsException) {
                errorInvalidCredentials.printStackTrace()
                showMessage("E-mail inválido... digite outro endereço...")
            }
        }
    }

    private fun salvarUserFirestore(user: User) {
        firestore
            .collection("users")
            .document(user.id)
            .set(user)
            .addOnSuccessListener {
                showMessage("Cadastro realizado com sucesso!")
                // vai para a tela principal do app
                startActivity(Intent(applicationContext, MainActivity::class.java))
            }.addOnFailureListener {
                showMessage("Erro ao cadastrar usuário!")
            }
    }

    private fun validarCampos(): Boolean {

        name = binding.editName.text.toString()
        email = binding.editEmail.text.toString()
        pwd = binding.editPwd.text.toString()

        if(name.isNotEmpty()) {
            binding.textInputName.error = null
            if(email.isNotEmpty()) {
                binding.textInputEmail.error = null
                if(pwd.isNotEmpty()) {
                    binding.textInputPwd.error = null
                    return true
                }else {
                    binding.textInputPwd.error = "Preencha a senha!"
                    return false
                }
            }else {
                binding.textInputEmail.error = "Preencha seu endereço de email!"
                return false
            }
        }else {
            binding.textInputName.error = "Preencha seu nome!"
            return false
        }
    }

    private fun initToolbar() {
        val toolbar = binding.incToolbar.materialToolbar
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Faça o seu cadastro"
            setDisplayHomeAsUpEnabled(true)
        }
    }
}