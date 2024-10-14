package dev.arkbuilders.ratewatch.data.repo.currency

import arrow.core.Either
import dev.arkbuilders.ratewatch.domain.model.CurrencyCode
import dev.arkbuilders.ratewatch.domain.model.CurrencyName
import dev.arkbuilders.ratewatch.domain.model.CurrencyRate
import dev.arkbuilders.ratewatch.domain.model.CurrencyType

interface CurrencyDataSource {
    val currencyType: CurrencyType

    suspend fun fetchRemote(): Either<Throwable, List<CurrencyRate>>

    suspend fun getCurrencyName(): Map<CurrencyCode, CurrencyName>
}
