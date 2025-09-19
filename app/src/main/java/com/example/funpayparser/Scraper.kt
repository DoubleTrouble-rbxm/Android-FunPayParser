package com.example.funpayparser
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

object Scraper {
    private val client = OkHttpClient.Builder()
        .connectTimeout(3, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .writeTimeout(5, TimeUnit.SECONDS)
        .build()

    fun fetchHtml(url: String): String? {
        return try {
            val request = Request.Builder()
                .url(url)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    println("Ошибка HTTP: ${response.code}")
                    return null
                }

                val body = response.body?.string()
                if (body.isNullOrBlank()) {
                    println("Ответ пустой")
                    null
                } else body
            }
        } catch (e: Exception) {
            println("Ошибка сети: ${e.message}")
            null
        }
    }
}