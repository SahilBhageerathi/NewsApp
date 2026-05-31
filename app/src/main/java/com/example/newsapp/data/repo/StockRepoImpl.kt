package com.example.newsapp.data.repo

import android.util.Log
import com.example.newsapp.BuildConfig
import com.example.newsapp.data.dataSource.network.FinnhubApiService
import com.example.newsapp.domain.model.Stock
import com.example.newsapp.domain.repo.StockRepository
import com.example.newsapp.data.mappers.toStock
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.awaitAll



class StockRepositoryImpl(
    private val api: FinnhubApiService,
) : StockRepository {

    init {
        Log.d("FINNHUB", "API Key: ${BuildConfig.FINNHUB_API_KEY}")
    }

    // Fetch a single stock — quote + profile in parallel
    override suspend fun getStock(symbol: String): Result<Stock> =
        runCatching<Stock> {
            coroutineScope {
                val quote   = this.async { api.getQuote(symbol) }
                val profile = this.async { api.getProfile(symbol) }
                quote.await().toStock(symbol, profile.await())
            }
        }

    // Fetch all watchlist stocks in parallel
    override suspend fun getWatchlist(symbols: List<String>): Result<List<Stock>> =
        runCatching {
            coroutineScope {
                symbols
                    .map { sym -> async { getStock(sym).getOrThrow() } }
                    .awaitAll()
            }
        }
}