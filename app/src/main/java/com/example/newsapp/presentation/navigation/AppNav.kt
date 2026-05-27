package com.example.newsapp.presentation.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.newsapp.presentation.navigation.Screen.*
import com.example.newsapp.presentation.pages.NewsDetailScreen
import com.example.newsapp.presentation.pages.NewsListScreen
import com.example.newsapp.presentation.viewmodel.NewsViewModel
import com.example.newsapp.presentation.viewmodel.NotificationViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun AppNav(navigator: AppNavigator = koinInject()) {
    val backStack = remember { mutableStateListOf<Screen>(Screen.NewsList) }
    val viewModel: NewsViewModel = koinViewModel()
    val notificationViewModel: NotificationViewModel = koinViewModel()

    LaunchedEffect(Unit) {
        if (backStack.isEmpty()) {
            backStack.add(Screen.NewsList)
        }

        navigator.navigation.collect { effect ->
            when (effect) {
                is NavigationEffect.ToDetail -> backStack.add(NewsDetail(effect.articleId))
                is NavigationEffect.Back -> backStack.removeLastOrNull()
                is NavigationEffect.OpenDetailPageFormNotification -> {
                    Log.d("NOTIF", "Article ID from navigator: ${effect.articleId.toInt()}");
                    backStack.add(NewsDetail(effect.articleId.toInt()))
                }
            }
//            navigator.clearLastEffect()
        }

    }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {

            entry<Screen.NewsList> {
                val state by viewModel.uiState.collectAsStateWithLifecycle()


                NewsListScreen(
                    articles = state.articles,
                    isLoading = state.isLoading,
                    error = state.error,
                    searchQuery = state.searchQuery,
                    onEvent = viewModel::onEvent,
                    isConnected = state.isConnected,
                    onNotificationClick = { articleId, title ->
                        notificationViewModel.showWelcomeNotification(articleId, title)
                    }
                )
            }

            entry<Screen.NewsDetail> { detail ->

                Log.d("NOTIF", "Detail entry composing with id: ${detail.articleId}")
                val state by viewModel.uiState.collectAsStateWithLifecycle()
                val article = state.articles.find { it.id == detail.articleId }
                Log.d("NOTIF", "Article in detail entry: ${article?.title}")
                NewsDetailScreen(
                    article = article,
                    onEvent = viewModel::onEvent
                )
            }
        }
    )
}