package com.horgaring.dateapp.data.api

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.horgaring.dateapp.data.ValidationException
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    private const val BASE_URL = "http://10.0.2.2:8080/api/"

    private val gson = Gson()

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val authInterceptor = Interceptor { chain ->
        val original = chain.request()
        val token = TokenManager.token
        val request = if (token != null) {
            original.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            original
        }
        chain.proceed(request)
    }

    private val errorInterceptor = Interceptor { chain ->
        val response = chain.proceed(chain.request())
        if (response.isSuccessful) return@Interceptor response

        response.use {
            val status = it.code
            val rawErrorBody = it.body?.string()

            val parsedErrors: HashMap<String, String> = if (!rawErrorBody.isNullOrBlank()) {
                try {
                    val bodyMap: Map<String, Any> = gson.fromJson(
                        rawErrorBody,
                        object : TypeToken<Map<String, Any>>() {}.type
                    )
                    val result = HashMap<String, String>()

                    val errorsObj = bodyMap["errors"]
                    if (errorsObj is Map<*, *>) {
                        errorsObj.forEach { (k, v) ->
                            if (k is String && v is String) result[k] = v
                        }
                    }

                    val message = bodyMap["message"]
                    if (message is String && result.isEmpty()) {
                        result["error"] = message
                    }

                    if (result.isEmpty()) result["error"] = "Server error: $status"
                    result
                } catch (_: Exception) {
                    hashMapOf("error" to "Server error: $status")
                }
            } else {
                hashMapOf("error" to "Server error: $status")
            }

            throw ValidationException(parsedErrors, status)
        }
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(errorInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: DateAppApi = retrofit.create(DateAppApi::class.java)
}
