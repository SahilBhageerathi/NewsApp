package com.example.newsapp.data.dto

data class WsMessageDto(
    val type: String,        // "trade" or "ping"
    val data: List<WsTradeDto>?,
)

data class WsTradeDto(
    val s: String,   // symbol e.g. "AAPL"
    val p: Double,   // last trade price
    val v: Double,   // volume
    val t: Long,     // timestamp milliseconds
)