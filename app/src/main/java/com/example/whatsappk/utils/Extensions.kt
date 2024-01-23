package com.example.whatsappk.utils

import android.app.Activity
import android.widget.Toast

fun Activity.showMessage(message: String) {
    Toast.makeText(
        applicationContext,
        message,
        Toast.LENGTH_LONG
    ).show()
}