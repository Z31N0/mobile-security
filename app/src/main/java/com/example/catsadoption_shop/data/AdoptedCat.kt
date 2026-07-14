package com.example.catsadoption_shop.data

import com.squareup.moshi.JsonClass

// Simple model for a cat shown in the profile
@JsonClass(generateAdapter = true)
data class AdoptedCat(
    val id: Int,
    val imageUrl: String,
)
