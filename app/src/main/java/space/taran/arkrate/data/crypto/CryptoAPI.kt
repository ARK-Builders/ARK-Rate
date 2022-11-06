package space.taran.arkrate.data.crypto

import retrofit2.http.GET

interface CryptoAPI {
    @GET("/api/v3/ticker/price")
    suspend fun getCryptoRates(): List<CryptoRateResponse>
}