package dev.arkbuilders.rate.data.worker

import androidx.work.DelegatingWorkerFactory
import dev.arkbuilders.rate.data.currency.CurrencyRepoImpl
import dev.arkbuilders.rate.data.db.PairAlertRepoImpl
import dev.arkbuilders.rate.domain.usecase.HandlePairAlertCheckUseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppWorkerFactory @Inject constructor(
    private val handlePairAlertCheckUseCase: HandlePairAlertCheckUseCase
) : DelegatingWorkerFactory() {
    init {
        addFactory(CurrencyMonitorWorkerFactory(handlePairAlertCheckUseCase))
    }
}