package com.example.newsapp.di

import com.example.newsapp.core.Connectivity.ConnectivityObserver
import com.example.newsapp.core.Connectivity.NetworkConnectivityObserver
import com.example.newsapp.data.dataSource.network.RemoteDataSource
import com.example.newsapp.data.repo.BookmarkRepoImpl
import com.example.newsapp.data.repo.ContactsRepoImpl
import com.example.newsapp.data.repo.NewsRepoImpl
import com.example.newsapp.domain.repo.BookmarkRepo
import com.example.newsapp.domain.repo.ContactsRepo
import com.example.newsapp.domain.repo.NewsRepo
import com.example.newsapp.presentation.navigation.AppNavigator
import com.example.newsapp.presentation.viewmodel.BookmarkViewModel
import com.example.newsapp.presentation.viewmodel.ContactsViewModel
import com.example.newsapp.presentation.viewmodel.NewsViewModel
import com.example.newsapp.utils.DefaultDispatcherProvider
import com.example.newsapp.utils.DispatcherProvider
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val appModule = module {

    // Connectivity
    single<ConnectivityObserver> { NetworkConnectivityObserver(androidContext()) }
    // content resolver
    single { androidContext().contentResolver }

    // Navigator
    single { AppNavigator() }

    // Network
    single { RetrofitInstance.api }

    // Data Source
    single { RemoteDataSource(get()) }

    // Repository
    single<NewsRepo> { NewsRepoImpl(get()) }
    single<ContactsRepo> { ContactsRepoImpl(get()) }
    single<BookmarkRepo> { BookmarkRepoImpl(get()) }

    // Dispatcher
    single<DispatcherProvider> { DefaultDispatcherProvider() }

    // ViewModel
    viewModel { NewsViewModel(get(), get(),get(),get()) }

    viewModel { ContactsViewModel(get(), get(), get()) }
    viewModel { BookmarkViewModel(get(), get(), get()) }
}
