package space.taran.arkrate.data.fiat

data class FiatRateResponse(
    val timestamp: Long,
    val rates: Map<String, Double>
)