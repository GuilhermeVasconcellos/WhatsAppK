package com.example.whatsappk.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.whatsappk.databinding.ItemContatosBinding
import com.example.whatsappk.model.User
import com.squareup.picasso.Picasso

class ContatosAdapter(
    private val onClick: (User) -> Unit
) : Adapter<ContatosAdapter.ContatosViewHolder>() {

    private var listContatos = emptyList<User>()
    fun addList(lista: List<User>) {
        listContatos = lista
        notifyDataSetChanged()
    }

    inner class ContatosViewHolder(
        private val binding: ItemContatosBinding
    ) : ViewHolder(binding.root) {

        fun bind(user: User) {
            binding.tvNomeContato.text = user.name
            binding.tvEmailContato.text = user.email
            Picasso.get()
                .load(user.photo)
                .into(binding.ivContato)

            binding.clItemContato.setOnClickListener {
                onClick(user)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContatosViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val itemView = ItemContatosBinding.inflate(inflater, parent, false)
        return ContatosViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return listContatos.size
    }

    override fun onBindViewHolder(holder: ContatosViewHolder, position: Int) {
        val user = listContatos[position]
        holder.bind(user)
    }
}