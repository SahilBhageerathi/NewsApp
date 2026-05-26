package com.example.newsapp.domain.repo

import com.example.newsapp.domain.model.Bookmark
import com.example.newsapp.domain.model.Article

interface BookmarkRepo {
    suspend fun getBookmarks(): List<Bookmark>
    suspend fun addBookmark(article: Article)
    suspend fun removeBookmark(id: Long)
    suspend fun isBookmarked(articleId: Int): Boolean

    suspend fun getBookmarkIdByArticleId(articleId: Int): Long?
}