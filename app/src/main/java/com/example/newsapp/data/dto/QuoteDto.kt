package com.example.newsapp.data.dto

data class QuoteDto(
    val c: Double,   // current price
    val d: Double,   // change
    val dp: Double,  // percent change
    val h: Double,   // day high
    val l: Double,   // day low
    val o: Double,   // open
    val pc: Double,  // previous close
)