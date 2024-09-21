package dev.arkbuilders.rate.data.worker

import androidx.work.DelegatingWorkerFactory
import dev.arkbuilders.rate.domain.repo.TimestampRepo
import dev.arkbuilders.rate.domain.usecase.HandlePairAlertCheckUseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppWorkerFactory @Inject constructor(
    private val handlePairAlertCheckUseCase: HandlePairAlertCheckUseCase,
    private val timestampRepo: TimestampRepo,
) : DelegatingWorkerFactory() {
    init {
        addFactory(
            CurrencyMonitorWorkerFactory(
                handlePairAlertCheckUseCase,
                timestampRepo,
            ),
        )
        addFactory(
            QuickPairsWidgetRefreshWorkerFactory(),
        )
    }
}
