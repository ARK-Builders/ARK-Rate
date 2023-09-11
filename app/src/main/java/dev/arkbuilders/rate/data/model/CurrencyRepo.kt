package dev.arkbuilders.rate.data.model

import android.util.Log
import dev.arkbuilders.rate.data.model.CurrencyCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import dev.arkbuilders.rate.data.db.CurrencyRateLocalDataSource
import dev.arkbuilders.rate.data.db.FetchTimestampDataSource
import dev.arkbuilders.rate.data.network.NetworkStatus
import dev.arkbuilders.rate.utils.withContextAndLock
import java.util.concurrent.TimeUnit

abstract class CurrencyRepo(
    private val local: CurrencyRateLocalDataSource,
    private val networkStatus: NetworkStatus,
    private val fetchTimestampDataSource: FetchTimestampDataSource
) {
    protected abstract val type: CurrencyType
    private var currencyRates: List<CurrencyRate>? = null
    private var updatedTS: Long? = null
    private val mutex = Mutex()

    suspend fun getCurrencyRate(): List<CurrencyRate> =
        withContextAndLock(Dispatchers.IO, mutex) {
            if (!networkStatus.isOnline()) {
                currencyRates = local.getByType(type)
                return@withContextAndLock currencyRates!!
            }

            updatedTS ?: let {
                updatedTS = fetchTimestampDataSource.getTimestamp(type)
            }

            if (
                updatedTS == null ||
                updatedTS!! + dayInMillis < System.currentTimeMillis()
            ) {
                val result = fetchRemoteSafe()
                result.onSuccess {
                    currencyRates = it
                    launch { fetchTimestampDataSource.rememberTimestamp(type) }
                    launch { local.insert(currencyRates!!, type) }
                    updatedTS = System.currentTimeMillis()
                }
            }

            currencyRates ?: let {
                currencyRates = local.getByType(type)
            }
            Log.d("Test", "${currencyRates!!.sortedBy { it.code }}")
            return@withContextAndLock currencyRates!!
        }

    private suspend fun fetchRemoteSafe(): Result<List<CurrencyRate>> {
        return try {
            Result.success(fetchRemote())
        } catch (e: Exception) {
            Log.e("Fetch currency error", "currency type [$type]", e)
            Result.failure(e)
        }
    }

    protected abstract suspend fun fetchRemote(): List<CurrencyRate>

    abstract suspend fun getCurrencyName(): List<CurrencyName>

    abstract suspend fun currencyNameByCode(code: CurrencyCode): CurrencyName

    private val dayInMillis = TimeUnit.DAYS.toMillis(1)
}