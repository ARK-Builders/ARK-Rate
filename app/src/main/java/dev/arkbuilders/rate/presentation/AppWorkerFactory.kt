package dev.arkbuilders.rate.presentation

import androidx.work.DelegatingWorkerFactory
import dev.arkbuilders.rate.core.domain.repo.TimestampRepo
import dev.arkbuilders.rate.feature.pairalert.data.worker.CurrencyMonitorWorkerFactory
import dev.arkbuilders.rate.feature.pairalert.domain.usecase.HandlePairAlertCheckUseCase
import dev.arkbuilders.rate.feature.quickwidget.worker.QuickPairsWidgetRefreshWorkerFactory

class AppWorkerFactory(
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
