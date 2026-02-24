
package com.deveshsharma.deveshsharma.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deveshsharma.deveshsharma.data.model.NewsArticle
import com.deveshsharma.deveshsharma.data.repository.NewsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class NewsUiState {
    object Loading : NewsUiState()
    data class Success(val news: List<NewsArticle>) : NewsUiState()
    data class Error(val message: String) : NewsUiState()
}

class NewsViewModel : ViewModel() {

    private val repository = NewsRepository()

    private val _uiState = MutableStateFlow<NewsUiState>(NewsUiState.Loading)
    val uiState: StateFlow<NewsUiState> = _uiState

    private var nextPage: String? = null
    private var isLoading = false

    init {
        fetchNews()
    }

    fun fetchNews() {
        viewModelScope.launch {
            if (isLoading || (nextPage == "-1" && nextPage != null)) {
                return@launch
            }
            isLoading = true

            val currentArticles = (_uiState.value as? NewsUiState.Success)?.news ?: emptyList()

            // Show full screen loader only on initial load.
            if (currentArticles.isEmpty()) {
                _uiState.value = NewsUiState.Loading
            }

            try {
                val response = repository.getNews(page = nextPage)
                val allArticles = currentArticles + response.results
                _uiState.value = NewsUiState.Success(allArticles.distinctBy { it.link })
                nextPage = response.nextPage
                if (nextPage == null) {
                    nextPage = "-1" // Sentinel to indicate end of pages
                }
            } catch (e: Exception) {
                if (currentArticles.isEmpty()) {
                    _uiState.value = NewsUiState.Error(e.message ?: "An unknown error occurred")
                } else {
                    // For subsequent load errors, we can just stop loading and keep old articles.
                    _uiState.value = NewsUiState.Success(currentArticles)
                    nextPage = "-1" // Stop further loading attempts
                }
            } finally {
                isLoading = false
            }
        }
    }
}
