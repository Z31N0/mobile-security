package com.example.catsadoption_shop.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val username: String,
    val encryptedPassword: String // This is where we'll store the weakly encrypted password
)