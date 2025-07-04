package dev.arkbuilders.rate.core.data.repo.currency

import arrow.core.Either
import dev.arkbuilders.rate.core.domain.model.CurrencyRate
import dev.arkbuilders.rate.core.domain.model.CurrencyType

interface CurrencyDataSource {
    val currencyType: CurrencyType

    suspend fun fetchRemote(): Either<Throwable, List<CurrencyRate>>
}
