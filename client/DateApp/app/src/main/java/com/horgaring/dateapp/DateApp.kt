package com.horgaring.dateapp

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.horgaring.dateapp.data.api.TokenManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient

class DateApp : Application(), ImageLoaderFactory {

    companion object {
        lateinit var instance: DateApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    override fun newImageLoader(): ImageLoader {
        val authClient = OkHttpClient.Builder()
            .addInterceptor(Interceptor { chain ->
                val token = TokenManager.token
                val request = if (token != null) {
                    chain.request().newBuilder()
                        .header("Authorization", "Bearer $token")
                        .build()
                } else {
                    chain.request()
                }
                chain.proceed(request)
            })
            .build()

        return ImageLoader.Builder(this)
            .okHttpClient(authClient)
            .crossfade(true)
            .build()
    }
}
