package com.example.catsadoption_shop.data

import com.squareup.moshi.JsonClass

// Request body for /auth/login and /auth/register
@JsonClass(generateAdapter = true)
data class AuthRequest(
    val username: String,
    val password: String
)

// Response from /auth/login and /auth/register
@JsonClass(generateAdapter = true)
data class AuthResponse(
    val userId: Int,
    val username: String
)