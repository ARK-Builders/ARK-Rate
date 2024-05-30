package dev.arkbuilders.rate.data.currency.crypto

import retrofit2.http.GET

interface CryptoAPI {
    @GET("/ARK-Builders/ark-exchange-rates/main/crypto-rates.json")
    suspend fun getCryptoRates(): List<CryptoRateResponse>
}