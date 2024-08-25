package dev.arkbuilders.rate.domain.model

enum class CurrencyType {
    FIAT,
    CRYPTO,
}

data class CurrencyRate(
    val type: CurrencyType,
    val code: CurrencyCode,
    val rate: Double,
)
