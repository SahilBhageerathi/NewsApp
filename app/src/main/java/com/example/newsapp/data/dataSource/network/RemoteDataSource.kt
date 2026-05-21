package com.example.newsapp.data.dataSource.network

import com.example.newsapp.data.dto.ArticleResponseDto


class RemoteDataSource(private val api: NewsApiService) {

    suspend fun getNews(): ApiResponse<ArticleResponseDto> {
        return safeApiCall { api.getArticles() }
    }

}