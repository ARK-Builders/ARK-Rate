package dev.arkbuilders.rate.data.network

import android.content.Context
import android.webkit.WebSettings
import okhttp3.OkHttpClient
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OkHttpClientBuilder @Inject constructor(val context: Context) {
    fun build(): OkHttpClient {
        val agent = WebSettings.getDefaultUserAgent(context)

        val client = OkHttpClient.Builder().addNetworkInterceptor { chain ->
                chain.proceed(chain.request().newBuilder().header("User-Agent", agent).build())
            }.build()

        return client
    }
}