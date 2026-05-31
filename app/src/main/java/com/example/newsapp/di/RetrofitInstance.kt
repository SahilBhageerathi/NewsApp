package com.example.newsapp.di

import com.example.newsapp.BuildConfig
import com.example.newsapp.data.dataSource.network.FinnhubApiService
import com.example.newsapp.data.dataSource.network.NewsApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private val finnhubRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.FINNHUB_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: NewsApiService = retrofit.create(NewsApiService::class.java)

    val finnhubApi: FinnhubApiService = finnhubRetrofit.create(FinnhubApiService::class.java)

}