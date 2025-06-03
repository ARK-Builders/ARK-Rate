@file:Suppress("ktlint")

package dev.arkbuilders.rate.core.data.network.api

import dev.arkbuilders.rate.core.data.network.dto.FiatRateResponse
import retrofit2.http.GET

interface FiatAPI {
    @GET("/ARK-Builders/ARK-Rate/refs/heads/exchange-rates/core/data/src/main/assets/fiat-rates.json")
    suspend fun get(): FiatRateResponse
}
