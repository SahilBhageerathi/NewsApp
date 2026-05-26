package com.example.newsapp.presentation.navigation

import kotlinx.serialization.Serializable

//Route based navigation
sealed class Screen {
    @Serializable
    data object NewsList : Screen()

    @Serializable
    data class NewsDetail(val articleId: Int) : Screen()

    @Serializable
    data object ContactsScreen : Screen()

    @Serializable
    data object BookmarkScreen : Screen()

}