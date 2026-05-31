package com.example.newsapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.data.dataSource.network.FinnhubWebSocket
import com.example.newsapp.domain.model.Stock
import com.example.newsapp.domain.repo.StockRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

// presentation/viewmodel/StockViewModel.kt

class StockViewModel(
    private val repository: StockRepository,
    private val webSocket: FinnhubWebSocket,  // ← new
) : ViewModel() {

    private val _stocks = MutableStateFlow<StockUiState>(StockUiState.Loading)
    val stocks: StateFlow<StockUiState> = _stocks.asStateFlow()

    init {
        getWatchlist()
    }

    fun getWatchlist() {
        viewModelScope.launch {
            _stocks.value = StockUiState.Loading
            repository.getWatchlist(DEFAULT_SYMBOLS)
                .onSuccess { stocks ->
                    _stocks.value = StockUiState.Success(stocks)
                    startLiveFeed(stocks.map { it.symbol }) // ← start ws after load
                }
                .onFailure {
                    _stocks.value = StockUiState.Error(it.message ?: "Something went wrong")
                }
        }
    }

    private fun startLiveFeed(symbols: List<String>) {
        viewModelScope.launch {
            webSocket.observePrices(symbols)
                .catch { e ->
                    Log.e("FINNHUB_WS", "Feed error: ${e.message}")
                }
                .collect { trade ->
                    // when a new price arrives, update just that stock in the list
                    val current = _stocks.value
                    if (current is StockUiState.Success) {
                        val updated = current.stocks.map { stock ->
                            if (stock.symbol == trade.s) {
                                val change = trade.p - stock.prevClose
                                val changePct = (change / stock.prevClose) * 100
                                stock.copy(
                                    price         = trade.p,
                                    change        = change,
                                    changePercent = changePct,
                                    high          = maxOf(stock.high, trade.p),
                                    low           = minOf(stock.low, trade.p),
                                )
                            } else stock
                        }
                        _stocks.value = StockUiState.Success(updated)
                    }
                }
        }
    }
}

// Sealed class to represent all possible UI states
sealed class StockUiState {
    object Loading : StockUiState()
    data class Success(val stocks: List<Stock>) : StockUiState()
    data class Error(val message: String) : StockUiState()
}

// Your default watchlist symbols
val DEFAULT_SYMBOLS = listOf(
    "AAPL", "MSFT", "NVDA", "GOOGL", "AMZN", "TSLA", "META", "JPM"
)