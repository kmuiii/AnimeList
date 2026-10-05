package com.example.animelist.ui.common

import androidx.compose.ui.autofill.FillableData

// sealed interface untuk ui state
sealed interface UiState<out T> {

    // ketika data sedang dimuat dari API
    data object Loading : UiState<Nothing>

    // ketika data berhasil dimuat
    // @param data objek/list data betipe T (generic type)
    data class Success<out T>(val data: T) : UiState<T>

    // ketika terjadi kesalahan saat pengambilan data
    // @param message pesan error untuk ditampilkan ke user
    data class Error(val message: String) : UiState<Nothing>
}