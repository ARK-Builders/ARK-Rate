package dev.arkbuilders.rate.data.assets

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import dev.arkbuilders.rate.data.model.CurrencyAmount
import dev.arkbuilders.rate.data.db.AssetsLocalDataSource
import dev.arkbuilders.rate.data.model.CurrencyCode
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AssetsRepo @Inject constructor(
    private val local: AssetsLocalDataSource
) {
    suspend fun allCurrencyAmount(): List<CurrencyAmount> = local.getAll()

    suspend fun findByCode(code: CurrencyCode) = local.getByCode(code)

    fun allCurrencyAmountFlow(): Flow<List<CurrencyAmount>> = local.allFlow()

    suspend fun setCurrencyAmount(amount: CurrencyAmount) =
        withContext(Dispatchers.IO) { local.insert(amount) }

    suspend fun setCurrencyAmountList(list: List<CurrencyAmount>) =
        local.insertList(list)

    suspend fun removeCurrency(code: String) = withContext(Dispatchers.IO) {
        local.delete(code)
    }
}