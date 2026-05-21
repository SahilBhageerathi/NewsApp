package com.example.newsapp.presentation.viewmodel

import com.example.newsapp.domain.model.Article

data class NewsUiState(
    val articles: List<Article> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val isConnected: Boolean = true
)