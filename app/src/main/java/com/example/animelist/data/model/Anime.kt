package com.example.animelist.data.model

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import java.lang.reflect.Type


// wrapper class untuk response API Tenrai
data class AnimeApiResponse<T>(
    @SerializedName("status") val status: Boolean? = true, // status keberhasilan response API
    @SerializedName("message") val message: String? = null, // pesan informasi atau error dari server
    @SerializedName("data") val data: T? = null // payload data utama bernilai generic T
)

// model data utama anime
data class Anime(
    @SerializedName("id") val id: String = "", // id unik anime
    @SerializedName("judul", alternate = ["title"]) val judul: String = "", // judul utama anime
    @SerializedName("poster", alternate = ["posterUrl", "image", "cover"]) val poster: String = "", // url gambar poster anime
    @SerializedName("skor", alternate = ["score", "rating"]) val skor: String = "0.0", // skor/rating anime
    @SerializedName("tipe", alternate = ["type"]) val tipe: String = "TV", // tipe penayangan (TV, Movie, OVA, dll)
    @SerializedName("status") val status: String = "Unknown", // status penayangan anime
    @SerializedName("totalEpisode", alternate = ["episodes", "total_episodes"]) val totalEpisode: String = "-", // jumlah episode
    @SerializedName("durasi", alternate = ["duration"]) val durasi: String = "-", // durasi per episode
    @SerializedName("tanggalRilis", alternate = ["releaseDate", "year", "release_date"]) val tanggalRilis: String = "-", // tanggal rilis anime
    @SerializedName("studio") val studio: String = "-", // nama studio pembuat anime
    @SerializedName("genre", alternate = ["genres"]) val genre: String = "-", // genre anime
    @SerializedName("sinopsis", alternate = ["synopsis", "description"]) val sinopsis: String = "" // sinopsis/deskripsi ringkas cerita
)

// sustom deserializer untuk menangani berbagai format JSON dari API
class AnimeDeserializer : JsonDeserializer<Anime> {
    // fungsi kustom untuk mengubah JSON element menjadi objek anime
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Anime {
        if (json == null || !json.isJsonObject) return Anime()
        val obj = json.asJsonObject

        val id = getStringOrNull(obj, "id", "mal_id") ?: ""
        val judul = getStringOrNull(obj, "judul", "title", "name", "title_english") ?: ""

        var poster = getStringOrNull(obj, "poster", "posterUrl", "image", "cover")
        if (poster.isNullOrEmpty() && obj.has("images") && obj.get("images").isJsonObject) {
            val images = obj.getAsJsonObject("images")
            val imgObj = when {
                images.has("jpg") && images.get("jpg").isJsonObject -> images.getAsJsonObject("jpg")
                images.has("webp") && images.get("webp").isJsonObject -> images.getAsJsonObject("webp")
                else -> null
            }
            if (imgObj != null) {
                poster = getStringOrNull(imgObj, "large_image_url", "image_url", "small_image_url")
            }
        }

        val skor = getStringOrNull(obj, "skor", "score", "rating") ?: "0.0"
        val tipe = getStringOrNull(obj, "tipe", "type") ?: "TV"
        val status = getStringOrNull(obj, "status") ?: "Unknown"
        val totalEpisode = getStringOrNull(obj, "totalEpisode", "episodes", "total_episodes") ?: "-"
        val durasi = getStringOrNull(obj, "durasi", "duration") ?: "-"

        var tanggalRilis = getStringOrNull(obj, "tanggalRilis", "releaseDate", "year", "release_date")
        if (tanggalRilis.isNullOrEmpty() && obj.has("aired") && obj.get("aired").isJsonObject) {
            val aired = obj.getAsJsonObject("aired")
            tanggalRilis = getStringOrNull(aired, "string")
        }
        if (tanggalRilis.isNullOrEmpty()) {
            tanggalRilis = "-"
        }

        val studio = parseArrayOrString(obj, "studio", "studios")
        val genre = parseArrayOrString(obj, "genre", "genres")
        val sinopsis = getStringOrNull(obj, "sinopsis", "synopsis", "description") ?: ""

        return Anime(
            id = id,
            judul = judul,
            poster = poster ?: "",
            skor = skor,
            tipe = tipe,
            status = status,
            totalEpisode = totalEpisode,
            durasi = durasi,
            tanggalRilis = tanggalRilis,
            studio = studio,
            genre = genre,
            sinopsis = sinopsis
        )
    }

    // helper untuk mengambil nilai string dari JsonObject
    private fun getStringOrNull(obj: JsonObject, vararg keys: String): String? {
        for (key in keys) {
            if (obj.has(key) && !obj.get(key).isJsonNull) {
                val elem = obj.get(key)
                if (elem.isJsonPrimitive) {
                    val str = elem.asString
                    if (str.isNotBlank()) {
                        return str
                    }
                }
            }
        }
        return null
    }

    // helper untuk memproses field yang bisa bernilai String tunggal atau JsonArray (seperti genre/studio)
    private fun parseArrayOrString(obj: JsonObject, vararg keys: String): String {
        for (key in keys) {
            if (obj.has(key) && !obj.get(key).isJsonNull) {
                val elem = obj.get(key)
                if (elem.isJsonPrimitive) {
                    val str = elem.asString
                    if (str.isNotBlank()) return str
                } else if (elem.isJsonArray) {
                    val array = elem.asJsonArray
                    val items = mutableListOf<String>()
                    for (item in array) {
                        if (item.isJsonPrimitive) {
                            items.add(item.asString)
                        } else if (item.isJsonObject) {
                            val itemObj = item.asJsonObject
                            if (itemObj.has("name") && !itemObj.get("name").isJsonNull) {
                                items.add(itemObj.get("name").asString)
                            }
                        }
                    }
                    if (items.isNotEmpty()) {
                        return items.joinToString(", ")
                    }
                }
            }
        }
        return "-"
    }
}

