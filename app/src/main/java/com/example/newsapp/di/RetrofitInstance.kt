package com.example.newsapp.di

import com.example.newsapp.BuildConfig
import com.example.newsapp.data.dataSource.network.NewsApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: NewsApiService = retrofit.create(NewsApiService::class.java)
}