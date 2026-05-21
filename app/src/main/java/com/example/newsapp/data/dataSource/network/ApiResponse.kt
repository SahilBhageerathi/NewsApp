package com.example.newsapp.data.dataSource.network

import retrofit2.HttpException
import java.io.IOException

sealed class ApiResponse<T> {
    data class Success<T>(val data: T) : ApiResponse<T>()
    data class Failure<T>(val error: String, val code: Int? = null) : ApiResponse<T>()
}

suspend fun <T> safeApiCall(apiCall: suspend () -> T): ApiResponse<T> {
    return try {
        ApiResponse.Success(apiCall())
    } catch (e: HttpException) {
        ApiResponse.Failure(
            error = e.message ?: "Something went wrong",
            code = e.code()
        )
    } catch (e: IOException) {
        ApiResponse.Failure(error = "No internet connection")
    } catch (e: Exception) {
        ApiResponse.Failure(error = e.message ?: "Unknown error")
    }
}