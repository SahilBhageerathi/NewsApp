package com.example.newsapp.data.dataSource.network

import com.example.newsapp.data.dto.ArticleResponseDto
import retrofit2.http.GET

interface NewsApiService {
    @GET("articles")
    suspend fun getArticles(): ArticleResponseDto
}