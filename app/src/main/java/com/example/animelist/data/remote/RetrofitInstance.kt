package com.example.animelist.data.remote

import com.example.animelist.data.model.Anime
import com.example.animelist.data.model.AnimeDeserializer
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// singleton object untuk menginisialisasi retrofit client
object RetrofitInstance {
    // base url endpoint REST API anime
    private const val BASE_URL = "https://api.tenrai.org/v1/"

    // konfigurasi gson converter dengan custom deserializer untuk data anime
    private val gson = GsonBuilder()
        .registerTypeAdapter(Anime::class.java, AnimeDeserializer())
        .create()

    // inisialisasi retrofit client secara lazy
    val api: AnimeApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(AnimeApi::class.java)
    }
}

