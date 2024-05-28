package dev.arkbuilders.rate.presentation.shared

import dev.arkbuilders.rate.data.model.CurrencyAmount
import dev.arkbuilders.rate.data.model.CurrencyCode
import dev.arkbuilders.rate.presentation.ui.NotifyAddedSnackbarVisuals
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

sealed class AppSharedFlow<T>(val flow: MutableSharedFlow<T>) {
    data object AddPairAlertTarget : AppSharedFlow<CurrencyCode>(MutableSharedFlow())
    data object AddPairAlertBase : AppSharedFlow<CurrencyCode>(MutableSharedFlow())
    data object AddCurrencyAmount :
        AppSharedFlow<Pair<Int, CurrencyCode>>(MutableSharedFlow())
    data object AddQuick : AppSharedFlow<Pair<Int, CurrencyCode>>(MutableSharedFlow())
    data object PickBaseCurrency: AppSharedFlow<CurrencyCode>(MutableSharedFlow())

    object ShowAddedSnackbarQuick {
        val flow: MutableStateFlow<NotifyAddedSnackbarVisuals?> = MutableStateFlow(null)
    }
    object ShowAddedSnackbarPortfolio {
        val flow: MutableStateFlow<NotifyAddedSnackbarVisuals?> = MutableStateFlow(null)
    }
    object ShowAddedSnackbarPairAlert {
        val flow: MutableStateFlow<NotifyAddedSnackbarVisuals?> = MutableStateFlow(null)
    }

    companion object {
        fun fromKey(key: AppSharedFlowKey): AppSharedFlow<*> = when (key) {
            AppSharedFlowKey.AddPairAlertTarget -> AddPairAlertTarget
            AppSharedFlowKey.AddPairAlertBase -> AddPairAlertBase
            AppSharedFlowKey.AddCurrencyAmount -> AddCurrencyAmount
            AppSharedFlowKey.AddQuick -> AddQuick
            AppSharedFlowKey.PickBaseCurrency -> PickBaseCurrency
        }
    }

}

enum class AppSharedFlowKey {
    AddPairAlertTarget, AddPairAlertBase, AddCurrencyAmount, AddQuick, PickBaseCurrency
}