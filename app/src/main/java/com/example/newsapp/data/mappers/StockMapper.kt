package com.example.newsapp.data.mappers

import com.example.newsapp.data.dto.ProfileDto
import com.example.newsapp.data.dto.QuoteDto
import com.example.newsapp.domain.model.Stock

fun QuoteDto.toStock(symbol: String, profile: ProfileDto) = Stock(
    symbol        = symbol,
    name          = profile.name,
    exchange      = profile.exchange,
    industry      = profile.finnhubIndustry,
    logoUrl       = profile.logo,
    price         = c,
    change        = d,
    changePercent = dp,
    high          = h,
    low           = l,
    open          = o,
    prevClose     = pc,
)