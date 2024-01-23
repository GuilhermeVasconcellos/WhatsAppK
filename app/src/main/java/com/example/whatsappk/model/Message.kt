package com.example.whatsappk.model

import java.util.Date
import com.google.firebase.firestore.ServerTimestamp

data class Message(
    val idUser: String = "",
    val message: String = "",
    @ServerTimestamp
    val date: Date? = null
)
