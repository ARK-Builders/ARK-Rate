package dev.arkbuilders.rate.data.worker

import androidx.work.DelegatingWorkerFactory
import dev.arkbuilders.rate.domain.repo.QuickRepo
import dev.arkbuilders.rate.domain.repo.TimestampRepo
import dev.arkbuilders.rate.domain.usecase.ConvertWithRateUseCase
import dev.arkbuilders.rate.domain.usecase.HandlePairAlertCheckUseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppWorkerFactory @Inject constructor(
    private val handlePairAlertCheckUseCase: HandlePairAlertCheckUseCase,
    private val timestampRepo: TimestampRepo,
    private val quickRepo: QuickRepo,
    private val convertUseCase: ConvertWithRateUseCase,
) : DelegatingWorkerFactory() {
    init {
        addFactory(
            CurrencyMonitorWorkerFactory(
                handlePairAlertCheckUseCase,
                timestampRepo
            )
        )
        addFactory(
            RatesRefreshWorkerFactory(quickRepo, convertUseCase)
        )
    }
}