package com.example.catsadoption_shop.data

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.util.concurrent.TimeUnit

object InsecureApiClient {

    // MUST be your FastAPI backend
    private const val BASE_URL = "http://10.0.2.2:8000/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Extra interceptor to print the exact URL being called
    private val debugInterceptor = Interceptor { chain ->
        val request = chain.request()
        println(">>> InsecureApiClient CALLING: ${request.method} ${request.url}")
        chain.proceed(request)
    }

    private val client: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(debugInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        // First: get raw strings
        .addConverterFactory(ScalarsConverterFactory.create())
        // Then: allow Moshi for other calls if needed
        .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
        .build()

    val api: InsecureApiService = retrofit.create(InsecureApiService::class.java)
}
