package com.example.newsapp.domain.model

data class Article(
    val id: Int,
    val title: String,
    val author: String,
    val url: String,
    val imageUrl: String,
    val newsSite: String,
    val summary: String,
    val publishedAt: String
)
