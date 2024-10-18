package dev.arkbuilders.ratewatch.data.network

import android.content.Context
import android.webkit.WebSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.OkHttpClient
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OkHttpClientBuilder @Inject constructor(
    @ApplicationContext  val context: Context) {
    fun build(): OkHttpClient {
//        val agent = WebSettings.getDefaultUserAgent(context)

        val client =
            OkHttpClient.Builder()
                .addNetworkInterceptor { chain ->
                    chain.proceed(
                        chain.request()
                            .newBuilder()
//                            .header("User-Agent", agent)
                            .build(),
                    )
                }
                .build()

        return client
    }
}
