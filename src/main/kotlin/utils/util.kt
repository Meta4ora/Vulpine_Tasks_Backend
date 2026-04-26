package com.example.utils

import java.security.MessageDigest

fun hash(password: String): String {
    val bytes = MessageDigest.getInstance("SHA-256")
        .digest(password.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
}