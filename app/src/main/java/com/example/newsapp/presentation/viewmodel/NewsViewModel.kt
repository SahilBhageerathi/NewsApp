package com.example.newsapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.core.Connectivity.ConnectivityObserver
import com.example.newsapp.data.dataSource.network.ApiResponse
import com.example.newsapp.domain.model.Article
import com.example.newsapp.domain.repo.NewsRepo
import com.example.newsapp.presentation.navigation.AppNavigator
import com.example.newsapp.presentation.navigation.NavigationEffect
import com.example.newsapp.presentation.navigation.NavigationEffect.*
import com.example.newsapp.utils.DispatcherProvider
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewsViewModel(
    private val repo: NewsRepo,
    private val dispatchers: DispatcherProvider,
    private val connectivityObserver: ConnectivityObserver,
    private val navigator: AppNavigator
) : ViewModel() {
    private val _uiState = MutableStateFlow(NewsUiState())
    val uiState: StateFlow<NewsUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<NewsEvent>()
    val uiEvent: SharedFlow<NewsEvent> = _uiEvent.asSharedFlow()


    init {
        observeConnectivity()
        getNews()
    }

    fun onEvent(event: NewsEvent) {
        when (event) {
            is NewsEvent.Refresh -> getNews()
            is NewsEvent.Search -> {
                _uiState.update { it.copy(searchQuery = event.query) }
            }

            is NewsEvent.ClearSearch -> {
                _uiState.update { it.copy(searchQuery = "") }
            }

            is NewsEvent.ArticleClicked -> {
                navigator.navigate(ToDetail(event.articleId))
//                  navigator.navigate(NavigationEffect.ToContactsScreen)
            }

            is NewsEvent.BackClicked -> {
                navigator.navigate(Back)
            }

            NewsEvent.GoToBookmarkedPage -> {
                navigator.navigate(TBookmarkScreen)
            }
        }
    }


    private fun getNews() {
        viewModelScope.launch(dispatchers.io) {
            _uiState.update { it.copy(isLoading = true, error = null) }

            repo.getNews().collect { response ->
                withContext(dispatchers.main) {
                    _uiState.update {
                        when (response) {
                            is ApiResponse.Success -> it.copy(
                                articles = response.data,
                                isLoading = false,
                                error = null
                            )

                            is ApiResponse.Failure -> it.copy(
                                isLoading = false,
                                error = response.error
                            )
                        }
                    }
                }
            }
        }
    }

    private fun observeConnectivity() {
        viewModelScope.launch(dispatchers.io) {
            connectivityObserver.isConnected.collect { connected ->
                Log.d("Connectivity123", "ViewModel received: $connected")
                _uiState.update { it.copy(isConnected = connected) }
                if (connected && _uiState.value.articles.isEmpty()) {
                    getNews()
                }
            }
        }
    }


    fun getArticleById(id: Int?): Article? {
        return _uiState.value.articles.find { it.id == id }
    }


}