package space.taran.arkrate.data.network

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
        val logInterceptor = HttpLoggingInterceptor()
        logInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val manager = object : X509TrustManager {
            override fun checkClientTrusted(
                chain: Array<out X509Certificate>?,
                authType: String?
            ) {
            }

            override fun checkServerTrusted(
                chain: Array<out X509Certificate>?,
                authType: String?
            ) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        }
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, arrayOf(manager), SecureRandom())

        val client = OkHttpClient.Builder()
            .addNetworkInterceptor { chain ->
                chain.proceed(
                    chain.request()
                        .newBuilder()
                        .header("User-Agent", agent)
                        .build()
                )
            }
            .addInterceptor(logInterceptor)
            .sslSocketFactory(sslContext.socketFactory, manager)
            .hostnameVerifier { _, _ -> true }
            .build()

        return client
    }
}