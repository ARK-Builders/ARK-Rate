package dev.arkbuilders.rate.data.network.dto

data class FiatRateResponse(
    val timestamp: Long,
    val rates: Map<String, Double>
)