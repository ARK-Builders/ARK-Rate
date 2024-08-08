package dev.arkbuilders.rate.data.network.dto

data class CryptoRatesResponse(
    val symbol: String,
    val current_price: Double
)
