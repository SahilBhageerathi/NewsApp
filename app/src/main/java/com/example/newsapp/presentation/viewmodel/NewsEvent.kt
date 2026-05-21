package com.example.newsapp.presentation.viewmodel

sealed class NewsEvent {
    data object Refresh : NewsEvent()
    data class Search(val query: String) : NewsEvent()
    data object ClearSearch : NewsEvent()

    data class ArticleClicked(val articleId: Int) : NewsEvent()
    data object BackClicked : NewsEvent()
}