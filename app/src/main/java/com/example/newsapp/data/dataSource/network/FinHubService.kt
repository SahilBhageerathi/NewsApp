package com.example.newsapp.data.dataSource.network

import com.example.newsapp.BuildConfig
import com.example.newsapp.data.dto.ProfileDto
import com.example.newsapp.data.dto.QuoteDto

import retrofit2.http.GET
import retrofit2.http.Query


interface FinnhubApiService {

    // Current price, change, high, low, open, prev close
    // GET https://finnhub.io/api/v1/quote?symbol=AAPL&token=xxx
    @GET("quote")
    suspend fun getQuote(
        @Query("symbol") symbol: String,
        @Query("token")  token: String = BuildConfig.FINNHUB_API_KEY,
    ): QuoteDto

    // Company name, exchange, industry, logo
    // GET https://finnhub.io/api/v1/stock/profile2?symbol=AAPL&token=xxx
    @GET("stock/profile2")
    suspend fun getProfile(
        @Query("symbol") symbol: String,
        @Query("token")  token: String = BuildConfig.FINNHUB_API_KEY,
    ): ProfileDto
}