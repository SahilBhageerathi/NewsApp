package com.example.newsapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.newsapp.presentation.navigation.Screen.*
import com.example.newsapp.presentation.pages.BookmarksScreen
import com.example.newsapp.presentation.pages.ContactsScreen
import com.example.newsapp.presentation.pages.NewsDetailScreen
import com.example.newsapp.presentation.pages.NewsListScreen
import com.example.newsapp.presentation.viewmodel.BookmarkEvent
import com.example.newsapp.presentation.viewmodel.BookmarkViewModel
import com.example.newsapp.presentation.viewmodel.ContactsViewModel
import com.example.newsapp.presentation.viewmodel.NewsViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun AppNav(navigator: AppNavigator = koinInject()) {
    val backStack = remember { mutableStateListOf<Screen>(NewsList) }
    val viewModel: NewsViewModel = koinViewModel()
    val contactsViewModel: ContactsViewModel = koinViewModel()
    val bookmarksViewModel: BookmarkViewModel = koinViewModel()

    LaunchedEffect(Unit) {
        if (backStack.isEmpty()) {
            backStack.add(NewsList)
        }

        navigator.navigation.collect { effect ->
            when (effect) {
                is NavigationEffect.ToDetail -> backStack.add(NewsDetail(effect.articleId))
                is NavigationEffect.ToContactsScreen -> backStack.add(Screen.ContactsScreen)
                is NavigationEffect.TBookmarkScreen -> {backStack.add(BookmarkScreen)}
                is NavigationEffect.Back -> backStack.removeLastOrNull()
            }
        }

    }

    NavDisplay(
        backStack = backStack,
        onBack = {  navigator.navigate(NavigationEffect.Back)},
        entryProvider = entryProvider {

            entry<NewsList> {
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

            entry<NewsDetail> { detail ->
                val viewModel: NewsViewModel = koinViewModel()
                val bookmarkState by bookmarksViewModel.uiState.collectAsStateWithLifecycle()
                val article = viewModel.getArticleById(detail.articleId)
                val isBookmarked = bookmarkState.bookmarks.any { it.articleId == detail.articleId }


                NewsDetailScreen(
                    article = viewModel.getArticleById(detail.articleId),
                    onEvent = viewModel::onEvent,
                    isBookmarked = isBookmarked,
                    onBookmark = { article -> bookmarksViewModel.onEvent(BookmarkEvent.AddBookmark(article)) },
                    onRemoveBookmark = { bookmarksViewModel.onEvent(BookmarkEvent.RemoveBookmarkByArticleId(it.id)) }

                )
            }

            entry<Screen.ContactsScreen> {

                val state by contactsViewModel.uiState.collectAsStateWithLifecycle()
                ContactsScreen(
                    uiState = state,
                    onEvent = contactsViewModel::onEvent
                )
            }


            entry<BookmarkScreen> {
                val state by bookmarksViewModel.uiState.collectAsStateWithLifecycle()
                BookmarksScreen(
                    uiState = state,
                    onEvent = bookmarksViewModel::onEvent
                )
            }
        }
    )
}