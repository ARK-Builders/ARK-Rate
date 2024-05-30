package dev.arkbuilders.rate.data.worker

import androidx.work.DelegatingWorkerFactory
import dev.arkbuilders.rate.data.currency.CurrencyRepoImpl
import dev.arkbuilders.rate.data.db.PairAlertRepoImpl
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppWorkerFactory @Inject constructor(
    private val currencyRepo: CurrencyRepoImpl,
    private val pairAlertRepo: PairAlertRepoImpl
): DelegatingWorkerFactory() {
    init {
        addFactory(CurrencyMonitorWorkerFactory(currencyRepo, pairAlertRepo))
    }
}