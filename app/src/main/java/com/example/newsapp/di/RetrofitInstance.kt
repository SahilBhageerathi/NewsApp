package com.example.newsapp.di

import com.example.newsapp.BuildConfig
import com.example.newsapp.data.dataSource.network.NewsApiService
import okhttp3.ConnectionPool
import okhttp3.Dispatcher
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

object RetrofitInstance {

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(HttpClientFactory.create())
        .build()

    val api: NewsApiService = retrofit.create(NewsApiService::class.java)
}


object HttpClientFactory {

    fun create(): OkHttpClient {
        return OkHttpClient.Builder()

            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .callTimeout(45, TimeUnit.SECONDS)

            .connectionPool(
                ConnectionPool(
                    maxIdleConnections = 20,
                    keepAliveDuration = 5,
                    timeUnit = TimeUnit.MINUTES
                )
            )

            .dispatcher(
                Dispatcher().apply {
                    maxRequests = 200
                    maxRequestsPerHost = 40
                }
            )

            .retryOnConnectionFailure(true)
            .addInterceptor(RetryInterceptor(maxRetries = 3))
            .followRedirects(true)
            .followSslRedirects(true)


            .apply {
                if (BuildConfig.DEBUG) {
                    addInterceptor(
                        HttpLoggingInterceptor().apply {
                            level = HttpLoggingInterceptor.Level.BODY
                        }
                    )
                }
            }

            .build()
    }
}



class RetryInterceptor(
    private val maxRetries: Int = 3, // attempt + 3 retries
    private val initialDelayMs: Long = 500, // wait time before first re try
    private val maxDelayMs: Long = 8_000 //no single wait should ever exceed this value
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var lastException: IOException? = null
        var currentDelay = initialDelayMs

        repeat(maxRetries + 1) { attempt ->
            try {
                val response = chain.proceed(request)

                // retry on 429 (rate-limited) or 5xx (server error)
                if (response.isSuccessful || attempt == maxRetries) return response
                val code = response.code
                if (code !in 429..429 && code !in 500..599) return response

                response.close()
            } catch (e: IOException) {
                lastException = e
                if (attempt == maxRetries) throw e
            }

            // we use jitter here for adding some extra delay so that there is some gap between multiple user requests
            // lets us say 1000 users are requesting at 12:00:000 and everyone failed at same time due to server load
            // if we dont use jitter again everyone will try with same delay instead we use jitter so there is little gap between the calls like milli seconds

            val jitter = (0..(currentDelay / 4).coerceAtLeast(1)).random()  // coerceAtLeast — "never go below this"
            Thread.sleep(currentDelay + jitter)
            currentDelay = (currentDelay * 2).coerceAtMost(maxDelayMs)  // coerceAtLeast — "never go above this"
        }

        throw lastException ?: IOException("Retry exhausted")
    }
}