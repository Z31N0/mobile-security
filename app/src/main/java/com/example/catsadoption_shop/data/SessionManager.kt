package com.example.catsadoption_shop.data

import android.content.Context

class SessionManager(context: Context) {

    private val prefs = context.getSharedPreferences("cats_session", Context.MODE_PRIVATE)

    fun saveSession(userId: Int, role: String, username: String) {
        prefs.edit()
            .putInt("user_id", userId)
            .putString("role", role)
            .putString("username", username)
            .apply()
    }

    fun getUserId(): Int? {
        val id = prefs.getInt("user_id", -1)
        return if (id == -1) null else id
    }

    fun getRole(): String? = prefs.getString("role", null)

    fun getUsername(): String? = prefs.getString("username", null)

    fun clear() {
        prefs.edit().clear().apply()
    }
}
