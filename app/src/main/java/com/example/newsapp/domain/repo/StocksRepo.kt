package com.example.newsapp.domain.repo

import com.example.newsapp.domain.model.Stock

interface StockRepository {
    suspend fun getStock(symbol: String): Result<Stock>
    suspend fun getWatchlist(symbols: List<String>): Result<List<Stock>>
}