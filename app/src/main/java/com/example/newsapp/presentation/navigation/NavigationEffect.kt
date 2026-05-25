package com.example.newsapp.presentation.navigation

sealed class NavigationEffect {
    data class ToDetail(val articleId: Int) : NavigationEffect()
    data object Back : NavigationEffect()
    data object ToContactsScreen : NavigationEffect()
}