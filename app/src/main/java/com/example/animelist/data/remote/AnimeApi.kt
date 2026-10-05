package com.example.animelist.data.remote

import com.example.animelist.data.model.Anime
import com.example.animelist.data.model.AnimeApiResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// interface retrofit untuk mendefinisikan endpoint API
interface AnimeApi {

    // get daftar anime berdasarkan query pencarian (q).
    @GET("anime")
    suspend fun getAnimeList(
        @Query("q") search: String? = null
    ): AnimeApiResponse<List<Anime>>

    // get detail anime berdasarkan id
    @GET("anime/{id}")
    suspend fun getAnimeDetail(
        @Path("id") id: String
    ): AnimeApiResponse<Anime>
}

