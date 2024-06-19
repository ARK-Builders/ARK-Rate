package dev.arkbuilders.rate.data.repo.currency

import dev.arkbuilders.rate.data.db.dao.CurrencyRateDao
import dev.arkbuilders.rate.data.db.entity.RoomCurrencyRate
import dev.arkbuilders.rate.domain.model.CurrencyRate
import dev.arkbuilders.rate.domain.model.CurrencyType
import javax.inject.Inject

class LocalCurrencyDataSource @Inject constructor(val dao: CurrencyRateDao) {
    suspend fun insert(
        currencyRate: List<CurrencyRate>,
        currencyType: CurrencyType
    ) = dao.insert(currencyRate.map { it.toRoom(currencyType) })

    suspend fun getByType(currencyType: CurrencyType) =
        dao.getByType(currencyType.name).map { it.toCurrencyRate() }
}

private fun RoomCurrencyRate.toCurrencyRate() =
    CurrencyRate(CurrencyType.valueOf(currencyType), code, rate)

private fun CurrencyRate.toRoom(currencyType: CurrencyType) =
    RoomCurrencyRate(code, currencyType.name, rate)