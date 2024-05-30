package dev.arkbuilders.rate.data.currency.fiat

data class FiatRateResponse(
    val timestamp: Long,
    val rates: Map<String, Double>
)