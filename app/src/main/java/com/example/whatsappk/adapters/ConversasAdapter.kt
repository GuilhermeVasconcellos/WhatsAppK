package com.example.whatsappk.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.whatsappk.databinding.ItemConversasBinding
import com.example.whatsappk.model.Chat
import com.squareup.picasso.Picasso

class ConversasAdapter(
    private val onClick: (Chat) -> Unit
) : Adapter<ConversasAdapter.ConversasViewHolder>() {

    private var listConversas = emptyList<Chat>()
    fun addList(lista: List<Chat>) {
        listConversas = lista
        notifyDataSetChanged()
    }

    inner class ConversasViewHolder(
        private val binding: ItemConversasBinding
    ) : ViewHolder(binding.root) {

        fun bind(chat: Chat) {
            binding.tvNomeConversa.text = chat.name
            binding.tvLastMessage.text = chat.lastMessage
            Picasso.get()
                .load(chat.photo)
                .into(binding.ivConversa)

            binding.clItemConversa.setOnClickListener {
                onClick(chat)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversasViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val itemView = ItemConversasBinding.inflate(inflater, parent, false)
        return ConversasViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ConversasAdapter.ConversasViewHolder, position: Int) {
        val chat = listConversas[position]
        holder.bind(chat)
    }

    override fun getItemCount(): Int {
        return listConversas.size
    }

}