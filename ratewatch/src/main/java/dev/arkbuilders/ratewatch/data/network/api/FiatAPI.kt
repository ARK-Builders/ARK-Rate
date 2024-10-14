package dev.arkbuilders.ratewatch.data.network.api

import dev.arkbuilders.ratewatch.data.network.dto.FiatRateResponse
import retrofit2.http.GET

interface FiatAPI {
    @GET("/ARK-Builders/ark-exchange-rates/main/fiat-rates.json")
    suspend fun get(): FiatRateResponse
}
