package com.example.newsapp.data.dto

import com.google.gson.annotations.SerializedName

data class ArticleResponseDto(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<ArticleDto>
)

data class ArticleDto(
    val id: Int,
    val title: String,
    val authors: List<AuthorDto>,
    val url: String,
    @SerializedName("image_url")
    val imageUrl: String,
    @SerializedName("news_site")
    val newsSite: String,
    val summary: String,
    @SerializedName("published_at")
    val publishedAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    val featured: Boolean,
    val launches: List<LaunchDto>,
    val events: List<EventDto>
)

data class AuthorDto(
    val name: String,
    val socials: String?
)

data class LaunchDto(
    val id: String,
    val provider: String?
)

data class EventDto(
    val id: Int,
    val provider: String?
)