package dev.arkbuilders.rate.core.presentation

import dev.arkbuilders.rate.core.presentation.ui.NotifyAddedSnackbarVisuals
import kotlinx.coroutines.flow.MutableSharedFlow

sealed class AppSharedFlow<T>(val flow: MutableSharedFlow<T>) {
    data object ShowAddedSnackbarQuick :
        AppSharedFlow<NotifyAddedSnackbarVisuals>(MutableSharedFlow())

    data object ShowAddedSnackbarPortfolio :
        AppSharedFlow<NotifyAddedSnackbarVisuals>(MutableSharedFlow())

    data object ShowAddedSnackbarPairAlert :
        AppSharedFlow<NotifyAddedSnackbarVisuals>(MutableSharedFlow())
}
