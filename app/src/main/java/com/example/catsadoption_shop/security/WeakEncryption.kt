package com.example.catsadoption_shop.security

import android.util.Base64

// Storing credentials with weak encryption (base64)

object WeakEncryption {
    fun encrypt(password: String): String {
        return Base64.encodeToString(password.toByteArray(), Base64.DEFAULT)
    }

    fun decrypt(encryptedPassword: String): String {
        return String(Base64.decode(encryptedPassword, Base64.DEFAULT))
    }
}