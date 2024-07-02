package dev.arkbuilders.rate.data.repo.currency

import arrow.core.Either
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import dev.arkbuilders.rate.domain.model.TimestampType
import dev.arkbuilders.rate.data.network.NetworkStatus
import dev.arkbuilders.rate.domain.model.CurrencyName
import dev.arkbuilders.rate.domain.model.CurrencyRate
import dev.arkbuilders.rate.domain.model.CurrencyType
import dev.arkbuilders.rate.domain.repo.TimestampRepo
import dev.arkbuilders.rate.utils.withContextAndLock
import timber.log.Timber
import java.util.concurrent.TimeUnit

abstract class CurrencyDataSource(
    private val local: LocalCurrencyDataSource,
    private val networkStatus: NetworkStatus,
    private val timestampRepo: TimestampRepo
) {
    protected abstract val currencyType: CurrencyType
    private fun timestampType() = when (currencyType) {
        CurrencyType.FIAT -> TimestampType.FetchFiat
        CurrencyType.CRYPTO -> TimestampType.FetchCrypto
    }

    private var updatedTS: Long? = null
    private val mutex = Mutex()

    suspend fun getCurrencyRate(): Either<Throwable, List<CurrencyRate>> =
        withContextAndLock(Dispatchers.IO, mutex) {
            val localRates = local.getByType(currencyType)
            var fetchRemoteError: Throwable = IllegalStateException()
            updatedTS ?: let {
                updatedTS = timestampRepo
                    .getTimestamp(timestampType())
                    ?.toInstant()
                    ?.toEpochMilli()
            }

            if (!networkStatus.isOnline() && localRates.isNotEmpty()) {
                return@withContextAndLock Either.Right(localRates)
            }

            if (networkStatus.isOnline() && hasUpdateIntervalPassed()) {
                val result = fetchRemote()
                result.onRight { rates ->
                    launch { timestampRepo.rememberTimestamp(timestampType()) }
                    launch { local.insert(rates, currencyType) }
                    updatedTS = System.currentTimeMillis()
                    return@withContextAndLock Either.Right(rates)
                }.onLeft {
                    Timber.e(it)
                    fetchRemoteError = it
                }
            }

            return@withContextAndLock if (localRates.isNotEmpty()) {
                Either.Right(localRates)
            } else {
                Either.Left(fetchRemoteError)
            }
        }

    protected abstract suspend fun fetchRemote(): Either<Throwable, List<CurrencyRate>>

    abstract suspend fun getCurrencyName(): Either<Throwable, List<CurrencyName>>

    private fun hasUpdateIntervalPassed() = updatedTS == null ||
            updatedTS!! + dayInMillis < System.currentTimeMillis()

    private val dayInMillis = TimeUnit.DAYS.toMillis(1)
}