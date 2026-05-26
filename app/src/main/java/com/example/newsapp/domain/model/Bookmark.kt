package com.example.newsapp.domain.model

data class Bookmark(
    val id: Long = 0,
    val articleId: Int,
    val title: String,
    val author: String,
    val url: String,
    val imageUrl: String,
    val newsSite: String,
    val summary: String,
    val publishedAt: String,
    val bookmarkedAt: Long = System.currentTimeMillis() / 1000
)