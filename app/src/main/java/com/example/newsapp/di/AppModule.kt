package com.example.newsapp.di

import com.example.newsapp.core.Connectivity.ConnectivityObserver
import com.example.newsapp.core.Connectivity.NetworkConnectivityObserver
import com.example.newsapp.data.dataSource.network.FinnhubWebSocket
import com.example.newsapp.data.dataSource.network.RemoteDataSource
import com.example.newsapp.data.repo.NewsRepoImpl
import com.example.newsapp.data.repo.StockRepositoryImpl
import com.example.newsapp.domain.repo.NewsRepo
import com.example.newsapp.domain.repo.StockRepository
import com.example.newsapp.presentation.navigation.AppNavigator
import com.example.newsapp.presentation.viewmodel.NewsViewModel
import com.example.newsapp.presentation.viewmodel.StockViewModel
import com.example.newsapp.utils.DefaultDispatcherProvider
import com.example.newsapp.utils.DispatcherProvider
import com.google.gson.Gson
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val appModule = module {

    single { OkHttpClient.Builder().build() }
    single { Gson() }

    // add websocket
    single { FinnhubWebSocket(get(), get()) }

    // Connectivity
    single<ConnectivityObserver> { NetworkConnectivityObserver(androidContext()) }

    // Navigator
    single { AppNavigator() }

    // Network
    single { RetrofitInstance.api }
    single { RetrofitInstance.finnhubApi }

    // Data Source
    single { RemoteDataSource(get()) }

    // Repository
    single<NewsRepo> { NewsRepoImpl(get()) }
    single<StockRepository> { StockRepositoryImpl(get()) }

    // Dispatcher
    single<DispatcherProvider> { DefaultDispatcherProvider() }

    // ViewModel
    viewModel { NewsViewModel(get(), get(),get(),get()) }
    viewModel { StockViewModel(get(),get()) }
}
