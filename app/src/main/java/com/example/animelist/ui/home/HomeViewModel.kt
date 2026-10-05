package com.example.animelist.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.animelist.data.model.Anime
import com.example.animelist.data.repository.AnimeRepository
import com.example.animelist.ui.common.UiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// view model untuk ui state home screen
class HomeViewModel(val repository: AnimeRepository = AnimeRepository()) : ViewModel() {

    val _uiState = MutableStateFlow<UiState<List<Anime>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Anime>>> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private var searchJob: Job? = null

    init {
        fetchAnimeList()
    }

    // fungsi untuk mengambil daftar anime dari repository
    fun fetchAnimeList(search: String? = null) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _uiState.value = UiState.Loading

            val querySearch = if (search.isNullOrBlank()) null else search.trim()

            repository.getAnimeList(querySearch)
                .onSuccess { list ->
                    if (list.isEmpty()) {
                        _uiState.value = UiState.Error("Tidak ada anime yang ditemukan.")
                    } else {
                        _uiState.value = UiState.Success(list)
                    }
                }
                .onFailure { throwable ->
                    _uiState.value = UiState.Error(
                        throwable.localizedMessage ?: "Gagal terhubung ke server. Periksa koneksi internet Anda."
                    )
                }
        }
    }

    // update query search anime dengan debouncing
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            if (query.isNotBlank()) {
                delay(400) // Debounce 400ms saat mengetik
            }
            val querySearch = if (query.isBlank()) null else query.trim()

            _uiState.value = UiState.Loading

            repository.getAnimeList(querySearch)
                .onSuccess { list ->
                    if (list.isEmpty()) {
                        _uiState.value = UiState.Error("Tidak ada anime yang ditemukan.")
                    } else {
                        _uiState.value = UiState.Success(list)
                    }
                }
                .onFailure { throwable ->
                    _uiState.value = UiState.Error(
                        throwable.localizedMessage ?: "Gagal terhubung ke server. Periksa koneksi internet Anda."
                    )
                }
        }
    }

    // trigger retry ketika eror
    fun retry() {
        fetchAnimeList(search = _searchQuery.value)
    }
}

