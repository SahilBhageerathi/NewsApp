package com.example.newsapp.presentation.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.domain.model.Bookmark
import com.example.newsapp.domain.model.Article
import com.example.newsapp.domain.repo.BookmarkRepo
import com.example.newsapp.presentation.navigation.AppNavigator
import com.example.newsapp.presentation.navigation.NavigationEffect
import com.example.newsapp.utils.DispatcherProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


data class BookmarkUiState(
    val bookmarks: List<Bookmark> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)


sealed class BookmarkEvent {
    data object LoadBookmarks : BookmarkEvent()
    data class AddBookmark(val article: Article) : BookmarkEvent()
    data class RemoveBookmarkByArticleId(val articleId: Int) : BookmarkEvent()
    data object BackClicked : BookmarkEvent()
}

class BookmarkViewModel(
    private val repo: BookmarkRepo,
    private val dispatchers: DispatcherProvider,
    private val navigator: AppNavigator
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookmarkUiState())
    val uiState: StateFlow<BookmarkUiState> = _uiState.asStateFlow()

    init {
        loadBookmarks()
    }

    fun onEvent(event: BookmarkEvent) {
        when (event) {
            is BookmarkEvent.LoadBookmarks -> loadBookmarks()
            is BookmarkEvent.AddBookmark -> addBookmark(event.article)
            is BookmarkEvent.RemoveBookmarkByArticleId -> removeByArticleId(event.articleId)
            is BookmarkEvent.BackClicked -> navigator.navigate(NavigationEffect.Back)
        }
    }

    private fun loadBookmarks() {
        viewModelScope.launch(dispatchers.io) {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val bookmarks = repo.getBookmarks()
                withContext(dispatchers.main) {
                    _uiState.update {
                        it.copy(bookmarks = bookmarks, isLoading = false)
                    }
                }
            } catch (e: Exception) {
                withContext(dispatchers.main) {
                    _uiState.update {
                        it.copy(isLoading = false, error = e.message ?: "Failed to load bookmarks")
                    }
                }
            }
        }
    }

    private fun addBookmark(article: Article) {
        viewModelScope.launch(dispatchers.io) {
            try {
                repo.addBookmark(article)
                loadBookmarks()
            } catch (e: Exception) {
                withContext(dispatchers.main) {
                    _uiState.update { it.copy(error = "Failed to bookmark: ${e.message}") }
                }
            }
        }
    }

    private fun removeByArticleId(articleId: Int) {
        viewModelScope.launch(dispatchers.io) {
            try {
                val bookmarkId = repo.getBookmarkIdByArticleId(articleId)
                if (bookmarkId != null) repo.removeBookmark(bookmarkId)
                loadBookmarks()
            } catch (e: Exception) {
                withContext(dispatchers.main) {
                    _uiState.update { it.copy(error = "Failed to remove: ${e.message}") }
                }
            }
        }
    }
}