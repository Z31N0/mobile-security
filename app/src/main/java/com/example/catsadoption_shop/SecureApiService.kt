package com.example.catsadoption_shop


import retrofit2.http.GET

data class SecureCatData(
    val id: String,
    val name: String,
    val description: String
)

// Note: The npoint bin returns the JSON at the base URL, so we call the base with @GET(".")
interface SecureApiService {
    @GET(".")
    suspend fun getSecureCat(): SecureCatData
}
