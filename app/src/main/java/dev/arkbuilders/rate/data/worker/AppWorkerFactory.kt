package dev.arkbuilders.rate.data.worker

import androidx.work.DelegatingWorkerFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppWorkerFactory @Inject constructor() : DelegatingWorkerFactory() {
    init {
        addFactory(
            QuickPairsWidgetRefreshWorkerFactory(),
        )
    }
}
