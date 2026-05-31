package com.example.newsapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.newsapp.presentation.StockScreens.DetailScreen
import com.example.newsapp.presentation.StockScreens.WatchlistScreen
import com.example.newsapp.presentation.pages.NewsDetailScreen
import com.example.newsapp.presentation.pages.NewsListScreen
import com.example.newsapp.presentation.viewmodel.NewsViewModel
import com.example.newsapp.presentation.viewmodel.StockViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun AppNav(navigator: AppNavigator = koinInject()) {
    val backStack = remember { mutableStateListOf<Screen>(Screen.StockWatchlist) }
    val viewModel: NewsViewModel = koinViewModel()
    val stockViewModel: StockViewModel = koinViewModel()

    LaunchedEffect(Unit) {
        if (backStack.isEmpty()) {
            backStack.add(Screen.NewsList)
        }

        navigator.navigation.collect { effect ->
            when (effect) {
                is NavigationEffect.ToDetail -> backStack.add(Screen.NewsDetail(effect.articleId))
                is NavigationEffect.Back -> backStack.removeLastOrNull()
            }
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
                    isConnected = state.isConnected
                )
            }

            entry<Screen.NewsDetail> { detail ->
                val viewModel: NewsViewModel = koinViewModel()
                NewsDetailScreen(
                    article = viewModel.getArticleById(detail.articleId),
                    onEvent = viewModel::onEvent
                )
            }

            entry<Screen.StockWatchlist> {
                WatchlistScreen(
                    viewModel = stockViewModel,
                    onStockClick = { symbol -> backStack.add(Screen.StockDetail(symbol)) }
                )
            }

            entry<Screen.StockDetail> { route ->
                DetailScreen(
                    symbol = route.symbol,
                    viewModel = stockViewModel, // ← same instance
                    onBack = { backStack.removeLastOrNull() }
                )
            }
        }
    )
}