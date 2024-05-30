package dev.arkbuilders.rate.domain.repo

import dev.arkbuilders.rate.domain.model.CurrencyAmount
import kotlinx.coroutines.flow.Flow

interface PortfolioRepo {
    suspend fun allCurrencyAmount(): List<CurrencyAmount>

    fun allCurrencyAmountFlow(): Flow<List<CurrencyAmount>>

    suspend fun getById(id: Long): CurrencyAmount?

    suspend fun setCurrencyAmount(amount: CurrencyAmount)

    suspend fun setCurrencyAmountList(list: List<CurrencyAmount>)

    suspend fun removeCurrency(id: Long)
}