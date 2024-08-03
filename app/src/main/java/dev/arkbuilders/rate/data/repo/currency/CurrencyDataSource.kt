package dev.arkbuilders.rate.data.repo.currency

import arrow.core.Either
import dev.arkbuilders.rate.domain.model.CurrencyCode
import dev.arkbuilders.rate.domain.model.CurrencyName
import dev.arkbuilders.rate.domain.model.CurrencyRate
import dev.arkbuilders.rate.domain.model.CurrencyType

interface CurrencyDataSource {
    val currencyType: CurrencyType

    suspend fun fetchRemote(): Either<Throwable, List<CurrencyRate>>

    suspend fun getCurrencyName(): Map<CurrencyCode, CurrencyName>
}