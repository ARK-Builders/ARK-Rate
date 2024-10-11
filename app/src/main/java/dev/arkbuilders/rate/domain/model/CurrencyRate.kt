package dev.arkbuilders.rate.domain.model

import java.math.BigDecimal

enum class CurrencyType {
    FIAT,
    CRYPTO,
}

data class CurrencyRate(
    val type: CurrencyType,
    val code: CurrencyCode,
    val rate: BigDecimal,
)
