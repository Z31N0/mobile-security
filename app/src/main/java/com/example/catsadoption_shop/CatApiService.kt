package com.example.catsadoption_shop

import retrofit2.http.GET
import retrofit2.http.Query

data class CatResponse(
    val id: String,
    val url: String,
    val width: Int?,
    val height: Int?
)

interface CatApiService {
    @GET("v1/images/search")
    suspend fun getCats(
        @Query("limit") limit: Int = 15
    ): List<CatResponse>
}
