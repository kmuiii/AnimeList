package com.example.animelist.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.animelist.data.model.Anime
import com.example.animelist.data.repository.AnimeRepository
import com.example.animelist.ui.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// view model untuk mengelola ui state dan data detail anime
class DetailViewModel (val repository: AnimeRepository = AnimeRepository()) : ViewModel () {
    // state internal mutable untuk status UI detail
    val _uiState = MutableStateFlow<UiState<Anime>>(UiState.Loading)
    // state public immutable yang diobservasi oleh composable UI
    val uiState: StateFlow<UiState<Anime>> = _uiState.asStateFlow()

    // mengambil detail anime berdasarkan id
    fun fetchAnimeDetail(id: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            repository.getAnimeDetail(id)
                .onSuccess { anime -> _uiState.value = UiState.Success(anime) }
                .onFailure { throwable -> _uiState.value = UiState.Error(throwable.localizedMessage ?: "Gagal memuat detail anime.") }
        }
    }

    // trigger request detail data ulang ketika error
    fun retry(id: String) {
        fetchAnimeDetail(id)
    }
}