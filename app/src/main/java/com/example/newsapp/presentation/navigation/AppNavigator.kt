package com.example.newsapp.presentation.navigation

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class AppNavigator {
    private val _navigation = MutableSharedFlow<NavigationEffect>(extraBufferCapacity = 1)
    val navigation: SharedFlow<NavigationEffect> = _navigation.asSharedFlow()

    fun navigate(effect: NavigationEffect) {
        _navigation.tryEmit(effect)
    }
}