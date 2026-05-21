package com.example.newsapp.domain.repo

import com.example.newsapp.data.dataSource.network.ApiResponse
import com.example.newsapp.domain.model.Article
import kotlinx.coroutines.flow.Flow

interface NewsRepo {
    fun getNews(): Flow<ApiResponse<List<Article>>>
}