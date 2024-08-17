package dev.arkbuilders.rate.data.repo.currency

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import dev.arkbuilders.rate.domain.model.CurrencyName
import dev.arkbuilders.rate.domain.model.CurrencyRate
import dev.arkbuilders.rate.domain.model.CurrencyType
import dev.arkbuilders.rate.domain.model.TimestampType
import dev.arkbuilders.rate.domain.repo.CurrencyRepo
import dev.arkbuilders.rate.domain.repo.NetworkStatus
import dev.arkbuilders.rate.domain.repo.TimestampRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.time.Duration
import java.time.OffsetDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrencyRepoImpl @Inject constructor(
    private val fiatDataSource: FiatCurrencyDataSource,
    private val cryptoDataSource: CryptoCurrencyDataSource,
    private val localCurrencyDataSource: LocalCurrencyDataSource,
    private val timestampRepo: TimestampRepo,
    private val networkStatus: NetworkStatus
) : CurrencyRepo {

    private val mutex = Mutex()

    override suspend fun getCurrencyRate(): Either<Throwable, List<CurrencyRate>> =
        withContext(Dispatchers.IO) {
            val local = localCurrencyDataSource.getAll()
            if (local.isNotEmpty()) {
                launch(Job()) { updateRates() }
                return@withContext local.right()
            } else {
                updateRates()
                val newLocal = localCurrencyDataSource.getAll()
                return@withContext if (newLocal.isEmpty()) {
                    IllegalStateException("Local rates are empty").left()
                } else {
                    newLocal.right()
                }
            }
        }

    override suspend fun getCurrencyName(): Either<Throwable, List<CurrencyName>> {
        val localRates = localCurrencyDataSource.getAll()
        if (localRates.isEmpty()) {
            return IllegalStateException("Local rates are empty").left()
        }

        val fiatNames = fiatDataSource.getCurrencyName()
        val cryptoNames = cryptoDataSource.getCurrencyName()

        val names = localRates.map { rate ->
            var name = when (rate.type) {
                CurrencyType.FIAT -> fiatNames[rate.code]
                CurrencyType.CRYPTO -> cryptoNames[rate.code]
            }
            if (name == null) {
                name = CurrencyName(rate.code, "")
            }

            name
        }

        return names.sortedBy { it.code }.right()
    }

    private suspend fun updateRates() = mutex.withLock {
        val updatedDate = timestampRepo
            .getTimestamp(TimestampType.FetchRates)

        if ((networkStatus.isOnline() && hasUpdateIntervalPassed(updatedDate)).not()) {
            return
        }

        val crypto = cryptoDataSource.fetchRemote()
        val fiat = fiatDataSource.fetchRemote()
        if (crypto.isLeft() || fiat.isLeft()) {
            return
        }
        localCurrencyDataSource.insert(
            crypto.getOrNull()!! + fiat.getOrNull()!!
        )
        timestampRepo.rememberTimestamp(TimestampType.FetchRates)
    }

    private fun hasUpdateIntervalPassed(updatedDate: OffsetDateTime?) =
        updatedDate == null ||
            Duration.between(updatedDate, OffsetDateTime.now())
            .toMillis() > dayInMillis

    private val dayInMillis = TimeUnit.DAYS.toMillis(1)
}
