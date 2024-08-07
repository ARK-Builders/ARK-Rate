package dev.arkbuilders.rate.data.network.api

import dev.arkbuilders.rate.data.network.dto.CryptoRatesResponse
import retrofit2.http.GET

interface CryptoAPI {
    @GET("/ARK-Builders/ark-exchange-rates/main/crypto-rates.json")
    suspend fun getCryptoRates(): List<CryptoRatesResponse>
}
