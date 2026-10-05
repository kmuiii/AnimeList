package com.example.animelist.data.repository

import com.example.animelist.data.model.Anime
import com.example.animelist.data.remote.AnimeApi
import com.example.animelist.data.remote.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// repository pattern untuk mengelola data anime dari retrofit API.
class AnimeRepository(
    private val api: AnimeApi = RetrofitInstance.api
) {

    // mengambil daftar anime secara asinkron
    suspend fun getAnimeList(search: String? = null): Result<List<Anime>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.getAnimeList(search)
                val list = response.data ?: emptyList()
                Result.success(list)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // mengambil detail anime berdasarkan id
    suspend fun getAnimeDetail(id: String): Result<Anime> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.getAnimeDetail(id)
                val anime = response.data ?: throw Exception("Detail anime tidak ditemukan")
                Result.success(anime)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
