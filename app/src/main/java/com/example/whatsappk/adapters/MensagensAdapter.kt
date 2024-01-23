package com.example.whatsappk.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.whatsappk.databinding.ItemMsgReceiverBinding
import com.example.whatsappk.databinding.ItemMsgSenderBinding
import com.example.whatsappk.model.Message
import com.example.whatsappk.utils.Constants
import com.google.firebase.auth.FirebaseAuth

class MensagensAdapter : Adapter<ViewHolder>() {

    private lateinit var bindingSender: ItemMsgSenderBinding
    private lateinit var bindingReceiver: ItemMsgReceiverBinding
    private var listMessages = emptyList<Message>()
    fun addList(lista: List<Message>) {
        listMessages = lista
        notifyDataSetChanged()
    }

    class SenderMessagesViewHolder(
        private val binding: ItemMsgSenderBinding
    ) : ViewHolder(binding.root) {

        companion object {
            fun inflateLayout(parent: ViewGroup) : SenderMessagesViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val itemViewSender = ItemMsgSenderBinding.inflate(inflater, parent, false)
                return SenderMessagesViewHolder(itemViewSender)
            }
        }
        fun bind(message: Message) {
            binding.tvSender.text = message.message
        }
    }

    class ReceiverMessagesViewHolder(
        private val binding: ItemMsgReceiverBinding
    ) : ViewHolder(binding.root) {

        companion object {
            fun inflateLayout(parent: ViewGroup) : ReceiverMessagesViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val itemViewReceiver = ItemMsgReceiverBinding.inflate(inflater, parent, false)
                return ReceiverMessagesViewHolder(itemViewReceiver)
            }
        }
        fun bind(message: Message) {
            binding.tvReceiver.text = message.message
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message = listMessages[position]
        val idLoggedUser = FirebaseAuth.getInstance().currentUser?.uid.toString()
        return if(idLoggedUser == message.idUser) {
            Constants.TIPO_REMETENTE
        }else {
            Constants.TIPO_DESTINATARIO
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return if(viewType == Constants.TIPO_REMETENTE) {
            SenderMessagesViewHolder.inflateLayout(parent)
        }else {
            ReceiverMessagesViewHolder.inflateLayout(parent)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mensagem = listMessages[position]
        when(holder) {
            is SenderMessagesViewHolder -> holder.bind(mensagem)
            is ReceiverMessagesViewHolder -> holder.bind(mensagem)
        }
    }

    override fun getItemCount(): Int {
        return listMessages.size
    }
}