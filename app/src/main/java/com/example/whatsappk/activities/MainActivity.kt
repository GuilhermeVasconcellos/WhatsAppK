package com.example.whatsappk.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuProvider
import com.example.whatsappk.R
import com.example.whatsappk.adapters.ViewPagerAdapter
import com.example.whatsappk.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initToolbar()
        initTabNavigation()

    }

    private fun initTabNavigation() {

        val tabLayout = binding.tabLayoutPrincipal
        val viewPager = binding.viewPagerPrincipal

        // adapter
        val tabs = listOf("CONVERSAS", "CONTATOS")
        viewPager.adapter = ViewPagerAdapter(
            tabs, supportFragmentManager, lifecycle
        )

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabs[position]
        }.attach()
    }

    private fun initToolbar() {
        val toolbar = binding.incMainToolbar.materialToolbar
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "WhatsApp"
        }

        addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu_principal, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    when(menuItem.itemId) {
                        R.id.itemPerfil -> {
                            startActivity(Intent(applicationContext, PerfilActivity::class.java))
                        }
                        R.id.itemSair -> {
                            logOutUser()
                        }
                    }
                    return true
                }

            }
        )
    }

    private fun logOutUser() {
        AlertDialog.Builder(this)
            .setTitle("Log out")
            .setMessage("Deseja realmente sair?")
            .setPositiveButton("Sim") { dialog, position ->
                firebaseAuth.signOut()
                startActivity(Intent(applicationContext, LoginActivity::class.java))
            }
            .setNegativeButton("Cancelar") { dialog, position -> }
            .create()
            .show()
    }

}