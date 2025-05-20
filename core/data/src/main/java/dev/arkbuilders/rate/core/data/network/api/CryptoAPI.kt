package dev.arkbuilders.rate.core.data.network.api

import dev.arkbuilders.rate.core.data.network.dto.CryptoRateResponse
import retrofit2.http.GET

@Suppress("ktlint:standard:max-line-length")
interface CryptoAPI {
    @GET("/ARK-Builders/cache-exchange-rates/refs/heads/joshwhittick-patch-1-1/crypto-rates-JW.json")
    suspend fun getCryptoRates(): List<CryptoRateResponse>
}
