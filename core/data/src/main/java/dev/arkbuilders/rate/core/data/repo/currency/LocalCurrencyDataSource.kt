package dev.arkbuilders.rate.core.data.repo.currency

import dev.arkbuilders.rate.core.db.dao.CurrencyRateDao
import dev.arkbuilders.rate.core.db.entity.RoomCurrencyRate
import dev.arkbuilders.rate.core.domain.model.CurrencyRate
import dev.arkbuilders.rate.core.domain.model.CurrencyType
import javax.inject.Inject

class LocalCurrencyDataSource @Inject constructor(val dao: dev.arkbuilders.rate.core.db.dao.CurrencyRateDao) {
    suspend fun insert(currencyRate: List<CurrencyRate>) =
        dao.insert(currencyRate.map { it.toRoom() })

    suspend fun getByType(currencyType: CurrencyType) =
        dao.getByType(currencyType.name).map { it.toCurrencyRate() }

    suspend fun getAll() = dao.getAll().map { it.toCurrencyRate() }
}

private fun dev.arkbuilders.rate.core.db.entity.RoomCurrencyRate.toCurrencyRate() =
    CurrencyRate(CurrencyType.valueOf(currencyType), code, rate)

private fun CurrencyRate.toRoom() =
    dev.arkbuilders.rate.core.db.entity.RoomCurrencyRate(code, type.name, rate)
