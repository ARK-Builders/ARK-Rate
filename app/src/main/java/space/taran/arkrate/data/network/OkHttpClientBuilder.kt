package dev.arkbuilders.rate.data.network

import android.content.Context
import android.webkit.WebSettings
import okhttp3.CipherSuite
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.TlsVersion
import okhttp3.logging.HttpLoggingInterceptor
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.Collections
import javax.inject.Inject
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

@Singleton
class OkHttpClientBuilder @Inject constructor(val context: Context) {
    fun build(): OkHttpClient {
        val agent = WebSettings.getDefaultUserAgent(context)

        val client = OkHttpClient.Builder()
            .addNetworkInterceptor { chain ->
                chain.proceed(
                    chain.request()
                        .newBuilder()
                        .header("User-Agent", agent)
                        .build()
                )
            }
            .build()

        return client
    }
}