package dev.arkbuilders.rate.core.domain.repo

import dev.arkbuilders.rate.core.domain.model.CurrencyCode
import dev.arkbuilders.rate.core.domain.model.CurrencyName
import dev.arkbuilders.rate.core.domain.model.CurrencyRate

interface CurrencyRepo {
    suspend fun getCurrencyRates(): List<CurrencyRate>

    suspend fun getCurrencyNames(): List<CurrencyName>

    suspend fun nameByCode(code: CurrencyCode): CurrencyName =
        getCurrencyNames().find { name -> name.code == code }
            ?: error("Currency code not found in names!")

    suspend fun getCodeToCurrencyRate(): Map<CurrencyCode, CurrencyRate> =
        getCurrencyRates().associateBy { it.code }
}
