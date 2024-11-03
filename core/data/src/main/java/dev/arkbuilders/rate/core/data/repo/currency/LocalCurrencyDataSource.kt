package dev.arkbuilders.rate.core.data.repo.currency

import dev.arkbuilders.rate.core.data.db.dao.CurrencyRateDao
import dev.arkbuilders.rate.core.data.db.entity.RoomCurrencyRate
import dev.arkbuilders.rate.core.domain.model.CurrencyRate
import dev.arkbuilders.rate.core.domain.model.CurrencyType
import javax.inject.Inject

class LocalCurrencyDataSource @Inject constructor(val dao: CurrencyRateDao) {
    suspend fun insert(currencyRate: List<CurrencyRate>) =
        dao.insert(currencyRate.map { it.toRoom() })

    suspend fun getByType(currencyType: CurrencyType) =
        dao.getByType(currencyType.name).map { it.toCurrencyRate() }

    suspend fun getAll() = dao.getAll().map { it.toCurrencyRate() }
}

private fun RoomCurrencyRate.toCurrencyRate() =
    CurrencyRate(CurrencyType.valueOf(currencyType), code, rate)

private fun CurrencyRate.toRoom() = RoomCurrencyRate(code, type.name, rate)
