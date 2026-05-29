package com.example.newsapp

import android.app.Application
import com.example.newsapp.core.Notification.LocalNotificationService
import com.example.newsapp.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.context.GlobalContext

class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@BaseApplication)
            modules(appModule)
        }

        GlobalContext.get().get<LocalNotificationService>().initialize()
    }
}