package com.example.newsapp.data.repo

import com.example.newsapp.data.dataSource.network.ApiResponse
import com.example.newsapp.data.dataSource.network.RemoteDataSource
import com.example.newsapp.data.mappers.toArticleList
import com.example.newsapp.domain.model.Article
import com.example.newsapp.domain.repo.NewsRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class NewsRepoImpl(
    private val remote: RemoteDataSource,
) : NewsRepo {
    override fun getNews(): Flow<ApiResponse<List<Article>>> = flow {
        when (val response = remote.getNews()) {
            is ApiResponse.Success -> emit(ApiResponse.Success(response.data.results.toArticleList()))
            is ApiResponse.Failure -> emit(ApiResponse.Failure(response.error, response.code))
        }
    }

}