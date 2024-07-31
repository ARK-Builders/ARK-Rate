package dev.arkbuilders.rate.data.repo.currency

import arrow.core.Either
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import dev.arkbuilders.rate.domain.model.TimestampType
import dev.arkbuilders.rate.data.network.NetworkStatus
import dev.arkbuilders.rate.domain.model.CurrencyCode
import dev.arkbuilders.rate.domain.model.CurrencyName
import dev.arkbuilders.rate.domain.model.CurrencyRate
import dev.arkbuilders.rate.domain.model.CurrencyType
import dev.arkbuilders.rate.domain.repo.TimestampRepo
import dev.arkbuilders.rate.utils.withContextAndLock
import timber.log.Timber
import java.util.concurrent.TimeUnit

interface CurrencyDataSource {
    val currencyType: CurrencyType

    suspend fun fetchRemote(): Either<Throwable, List<CurrencyRate>>

    suspend fun getCurrencyName(): Map<CurrencyCode, CurrencyName>
}