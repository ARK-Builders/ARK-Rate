package dev.arkbuilders.rate.domain.repo

import dev.arkbuilders.rate.domain.model.CurrencyCode
import dev.arkbuilders.rate.domain.model.CurrencyName
import dev.arkbuilders.rate.domain.model.CurrencyRate

interface CurrencyRepo {
    suspend fun currencyNameByCode(code: CurrencyCode): CurrencyName

    suspend fun rateByCode(code: CurrencyCode): CurrencyRate

    suspend fun getCodeToCurrencyRate(): Map<CurrencyCode, CurrencyRate>

    suspend fun getCurrencyRate(): List<CurrencyRate>

    suspend fun getCurrencyName(): List<CurrencyName>
}