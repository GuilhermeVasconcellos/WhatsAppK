package com.example.whatsappk.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.whatsappk.fragments.ContatosFragment
import com.example.whatsappk.fragments.ConversasFragment

class ViewPagerAdapter(
    private val tabs: List<String>,
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return tabs.size
    }

    override fun createFragment(position: Int): Fragment {
        when(position) {
            1 -> return ContatosFragment()
        }
        return ConversasFragment()
    }
}