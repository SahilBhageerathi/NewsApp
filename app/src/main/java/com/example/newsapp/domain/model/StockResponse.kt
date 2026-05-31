package com.example.newsapp.domain.model


data class Stock(
    val symbol: String,
    val name: String,
    val exchange: String,
    val industry: String,
    val logoUrl: String,
    val price: Double,
    val change: Double,
    val changePercent: Double,
    val high: Double,
    val low: Double,
    val open: Double,
    val prevClose: Double,
)