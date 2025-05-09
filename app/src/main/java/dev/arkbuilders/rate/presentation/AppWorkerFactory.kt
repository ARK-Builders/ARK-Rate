package dev.arkbuilders.rate.presentation

import androidx.work.DelegatingWorkerFactory
import dev.arkbuilders.rate.feature.quickwidget.worker.QuickPairsWidgetRefreshWorkerFactory

class AppWorkerFactory : DelegatingWorkerFactory() {
    init {
        addFactory(
            QuickPairsWidgetRefreshWorkerFactory(),
        )
    }
}
