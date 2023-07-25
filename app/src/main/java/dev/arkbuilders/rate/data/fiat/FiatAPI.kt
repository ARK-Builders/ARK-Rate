package dev.arkbuilders.rate.data.fiat

import retrofit2.http.GET

interface FiatAPI {
    @GET("/ARK-Builders/ark-exchange-rates/main/fiat-rates.json")
    suspend fun get(): FiatRateResponse
}