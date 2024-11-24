package dev.arkbuilders.rate.core.data.network.api

import dev.arkbuilders.rate.core.data.network.dto.CryptoRateResponse
import retrofit2.http.GET

interface CryptoAPI {
    @GET("/ARK-Builders/ark-exchange-rates/main/crypto-rates.json")
    suspend fun getCryptoRates(): List<CryptoRateResponse>
}
