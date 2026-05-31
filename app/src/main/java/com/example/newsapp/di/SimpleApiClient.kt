//package com.example.newsapp.di
//
//import okhttp3.HttpUrl
//import okhttp3.OkHttpClient
//import okhttp3.Request
//import org.json.JSONArray
//import java.io.IOException
//import java.util.concurrent.TimeUnit
//
//
//
//data class Post(
//    val id: Int,
//    val userId: Int,
//    val title: String,
//    val body: String
//)
//
//object SimpleApiClient{
//
//    private val baseUrl = HttpUrl.Builder()
//        .scheme("https")
//        .host("jsonplaceholder.typicode.com")
//        .build()
//
//    private val client : OkHttpClient by lazy{
//
//        val logging = HttpLoggingInterceptor().apply {
//            level = HttpLoggingInterceptor.Level.BASIC
//        }
//        OkHttpClient.Builder()
//            .connectTimeout(15, TimeUnit.SECONDS)
//            .readTimeout(15, TimeUnit.SECONDS)
//            .writeTimeout(15, TimeUnit.SECONDS)
//            .addInterceptor(logging)
//            .build()
//    }
//
//    fun fetchPosts(): List<Post> {
//        val url = baseUrl.newBuilder()
//            .addPathSegment("posts")
//            .build()
//
//        val request = Request.Builder()
//            .url(url)
//            .get()
//            .build()
//
//        client.newCall(request).execute().use { response ->
//            if (!response.isSuccessful) {
//                throw RuntimeException("Unexpected HTTP ${response.code}: ${response.message}")
//            }
//
//            val body = response.body?.string()
//                ?: throw IOException("Empty response body")
//
//            return parsePostsJson(body)
//        }
//    }
//
//    private fun parsePostsJson(json: String): List<Post> {
//        val array = JSONArray(json)
//        return List(array.length()) { i ->
//            val obj = array.getJSONObject(i)
//            Post(
//                id     = obj.getInt("id"),
//                userId = obj.getInt("userId"),
//                title  = obj.getString("title"),
//                body   = obj.getString("body")
//            )
//        }
//    }
//}
//
