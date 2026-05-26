package com.example.newsapp.data.mappers


import com.example.newsapp.domain.model.Bookmark
import com.example.newsapp.data.dto.BookmarkDto

fun BookmarkDto.toBookmark(): Bookmark {
    return Bookmark(
        id = id,
        articleId = articleId,
        title = title,
        author = author ?: "Unknown",
        url = url,
        imageUrl = imageUrl ?: "",
        newsSite = newsSite ?: "",
        summary = summary ?: "",
        publishedAt = publishedAt ?: "",
        bookmarkedAt = bookmarkedAt
    )
}

fun List<BookmarkDto>.toBookmarkList(): List<Bookmark> {
    return map { it.toBookmark() }
}