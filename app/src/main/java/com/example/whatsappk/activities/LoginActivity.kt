package com.example.whatsappk.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.whatsappk.databinding.ActivityLoginBinding
import com.example.whatsappk.utils.showMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

class LoginActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }
    private lateinit var email: String
    private lateinit var pwd: String
    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initClickEvents()

    }

    override fun onStart() {
        super.onStart()
        checkLoggedUser()
    }

    private fun checkLoggedUser() {
        val currentUser = firebaseAuth.currentUser
        if(currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    fun loginUser(email: String, pwd: String) {
        firebaseAuth
            .signInWithEmailAndPassword(email, pwd)
            .addOnSuccessListener {
                showMessage("Usuário logado com sucesso!")
                startActivity(Intent(this, MainActivity::class.java))
            }.addOnFailureListener {
                try{
                    throw it
                }catch(errorWeakPwd: FirebaseAuthWeakPasswordException) {
                    errorWeakPwd.printStackTrace()
                    showMessage("Senha fraca... utilize letras, números e caracteres especiais...")
                }catch(errorDuplicateUser: FirebaseAuthInvalidUserException) {
                    errorDuplicateUser.printStackTrace()
                    showMessage("Usuário não cadastrado...")
                }catch(errorInvalidCredentials: FirebaseAuthInvalidCredentialsException) {
                    errorInvalidCredentials.printStackTrace()
                    showMessage("Credenciais inválidas... tente novamente...")
                }
            }
    }

    private fun validarCampos(): Boolean {

        email = binding.editEmail.text.toString()
        pwd = binding.editSenha.text.toString()

        if(email.isNotEmpty()) {
            binding.textInputLoginEmail.error = null
            if(pwd.isNotEmpty()) {
                binding.textInputLoginSenha.error = null
                return true
            }else {
                binding.textInputLoginSenha.error = "Preencha a senha!"
                return false
            }
        }else {
            binding.textInputLoginEmail.error = "Preencha seu endereço de email!"
            return false
        }

    }

    private fun initClickEvents() {
        binding.btnLogin.setOnClickListener {
            if(validarCampos()) {
                loginUser(email, pwd)
            }
        }
        binding.textCadastro.setOnClickListener {
            startActivity(Intent(this, CadastroActivity::class.java))
        }
    }
}