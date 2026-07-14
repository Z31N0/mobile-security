package com.example.catsadoption_shop.data

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface InsecureApiService {

    // --- IDOR endpoint (main demo) ---
    @GET("users/{id}")
    suspend fun getUser(
        @Path("id") id: Int
    ): Response<UserProfile>
}
