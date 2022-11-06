package space.taran.arkrate.data.fiat

import retrofit2.http.GET

interface FiatAPI {
    @GET("/ARK-Builders/ark-exchange-rates/main/rates.json")
    suspend fun get(): FiatRateResponse
}