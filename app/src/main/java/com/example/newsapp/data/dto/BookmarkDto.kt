package com.example.newsapp.data.dto

data class BookmarkDto(
    val id: Long,
    val articleId: Int,
    val title: String,
    val author: String?,
    val url: String,
    val imageUrl: String?,
    val newsSite: String?,
    val summary: String?,
    val publishedAt: String?,
    val bookmarkedAt: Long
)