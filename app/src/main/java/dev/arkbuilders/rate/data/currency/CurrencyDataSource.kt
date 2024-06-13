package dev.arkbuilders.rate.data.currency

import arrow.core.Either
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import dev.arkbuilders.rate.data.db.CurrencyRateLocalDataSource
import dev.arkbuilders.rate.data.db.TimestampRepo
import dev.arkbuilders.rate.data.db.TimestampType
import dev.arkbuilders.rate.data.network.NetworkStatus
import dev.arkbuilders.rate.domain.model.CurrencyName
import dev.arkbuilders.rate.domain.model.CurrencyRate
import dev.arkbuilders.rate.domain.model.CurrencyType
import dev.arkbuilders.rate.utils.withContextAndLock
import timber.log.Timber
import java.util.concurrent.TimeUnit

abstract class CurrencyDataSource(
    private val local: CurrencyRateLocalDataSource,
    private val networkStatus: NetworkStatus,
    private val timestampRepo: TimestampRepo
) {
    protected abstract val currencyType: CurrencyType
    private fun timestampType() = when (currencyType) {
        CurrencyType.FIAT -> TimestampType.FetchFiat
        CurrencyType.CRYPTO -> TimestampType.FetchCrypto
    }

    private var currencyRates: List<CurrencyRate>? = null
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
                currencyRates = localRates
                return@withContextAndLock Either.Right(currencyRates!!)
            }

            if (networkStatus.isOnline() && hasUpdateIntervalPassed()) {
                val result = fetchRemote()
                result.onRight {
                    currencyRates = it
                    launch { timestampRepo.rememberTimestamp(timestampType()) }
                    launch { local.insert(currencyRates!!, currencyType) }
                    updatedTS = System.currentTimeMillis()
                    return@withContextAndLock Either.Right(currencyRates!!)
                }.onLeft {
                    Timber.e(it)
                    fetchRemoteError = it
                }
            }

            currencyRates ?: let {
                if (localRates.isNotEmpty()) {
                    currencyRates = localRates
                    return@withContextAndLock Either.Right(currencyRates!!)
                } else {
                    return@withContextAndLock Either.Left(fetchRemoteError)
                }
            }

            return@withContextAndLock Either.Right(currencyRates!!)
        }

    protected abstract suspend fun fetchRemote(): Either<Throwable, List<CurrencyRate>>

    abstract suspend fun getCurrencyName(): Either<Throwable, List<CurrencyName>>

    private fun hasUpdateIntervalPassed() = updatedTS == null ||
            updatedTS!! + dayInMillis < System.currentTimeMillis()

    private val dayInMillis = TimeUnit.DAYS.toMillis(1)
}