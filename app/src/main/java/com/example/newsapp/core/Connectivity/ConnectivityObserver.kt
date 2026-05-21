package com.example.newsapp.core.Connectivity

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {
    fun getCurrentStatus(): Boolean
    val isConnected: Flow<Boolean>
}