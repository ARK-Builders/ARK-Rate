package dev.arkbuilders.rate.core.domain.repo

import dev.arkbuilders.rate.core.domain.model.CurrencyCode
import dev.arkbuilders.rate.core.domain.model.CurrencyInfo
import dev.arkbuilders.rate.core.domain.model.CurrencyRate

interface CurrencyRepo {
    suspend fun getCurrencyRates(): List<CurrencyRate>

    suspend fun getCurrencyInfo(): List<CurrencyInfo>

    suspend fun infoByCode(code: CurrencyCode): CurrencyInfo =
        getCurrencyInfo().find { name -> name.code == code }
            ?: error("Currency code not found in names!")

    suspend fun getCodeToCurrencyRate(): Map<CurrencyCode, CurrencyRate> =
        getCurrencyRates().associateBy { it.code }
}
