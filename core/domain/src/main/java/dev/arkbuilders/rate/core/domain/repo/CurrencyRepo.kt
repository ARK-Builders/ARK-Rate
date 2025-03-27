package dev.arkbuilders.rate.core.domain.repo

import dev.arkbuilders.rate.core.domain.model.CurrencyCode
import dev.arkbuilders.rate.core.domain.model.CurrencyName
import dev.arkbuilders.rate.core.domain.model.CurrencyRate

interface CurrencyRepo {
    suspend fun getCurrencyRate(): List<CurrencyRate>

    suspend fun getCurrencyName(): List<CurrencyName>

    suspend fun nameByCode(code: CurrencyCode): CurrencyName =
        getCurrencyName().find { name -> name.code == code }
            ?: error("Currency code not found in names!")

    suspend fun getCodeToCurrencyRate(): Map<CurrencyCode, CurrencyRate> =
        getCurrencyRate().associateBy { it.code }
}
