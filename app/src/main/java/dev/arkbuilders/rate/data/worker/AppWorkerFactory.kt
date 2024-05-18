package dev.arkbuilders.rate.data.worker

import androidx.work.DelegatingWorkerFactory
import dev.arkbuilders.rate.data.GeneralCurrencyRepo
import dev.arkbuilders.rate.data.db.PairAlertRepo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppWorkerFactory @Inject constructor(
    private val currencyRepo: GeneralCurrencyRepo,
    private val pairAlertRepo: PairAlertRepo
): DelegatingWorkerFactory() {
    init {
        addFactory(CurrencyMonitorWorkerFactory(currencyRepo, pairAlertRepo))
    }
}