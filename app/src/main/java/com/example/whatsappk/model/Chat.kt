package com.example.whatsappk.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Chat(
    val idUserSender: String = "",
    val idUserReceiver: String = "",
    val photo: String = "",
    val name: String = "",
    val lastMessage: String = "",
    @ServerTimestamp
    val date: Date? = null
)
