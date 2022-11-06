package space.taran.arkrate.di.module

import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import space.taran.arkrate.data.crypto.CryptoAPI
import space.taran.arkrate.data.fiat.FiatAPI
import javax.inject.Named
import javax.inject.Singleton

@Module
class ApiModule {
    @Singleton
    @Provides
    fun cryptoAPI(): CryptoAPI {
        val httpClient = OkHttpClient.Builder().build()
        val gson = GsonBuilder().create()

        return Retrofit.Builder()
            .baseUrl("https://api.binance.com")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(httpClient)
            .build()
            .create(CryptoAPI::class.java)
    }

    @Singleton
    @Provides
    fun fiatAPI(): FiatAPI {
        val httpClient = OkHttpClient.Builder().build()
        val gson = GsonBuilder().create()

        return Retrofit.Builder()
            .baseUrl("https://raw.githubusercontent.com")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(httpClient)
            .build()
            .create(FiatAPI::class.java)
    }
}