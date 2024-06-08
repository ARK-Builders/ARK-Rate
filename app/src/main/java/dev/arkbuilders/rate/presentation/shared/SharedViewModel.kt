package dev.arkbuilders.rate.presentation.shared

import dev.arkbuilders.rate.domain.model.CurrencyCode
import dev.arkbuilders.rate.presentation.ui.NotifyAddedSnackbarVisuals
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

sealed class AppSharedFlow<T>(val flow: MutableSharedFlow<T>) {
    data object AddPairAlertTarget : AppSharedFlow<CurrencyCode>(MutableSharedFlow())
    data object AddPairAlertBase : AppSharedFlow<CurrencyCode>(MutableSharedFlow())

    data object SetAssetCode :
        AppSharedFlow<Pair<Int, CurrencyCode>>(MutableSharedFlow())
    data object AddAsset : AppSharedFlow<CurrencyCode>(MutableSharedFlow())

    data object SetQuickCode :
        AppSharedFlow<Pair<Int, CurrencyCode>>(MutableSharedFlow())
    data object AddQuickCode :
        AppSharedFlow<CurrencyCode>(MutableSharedFlow())

    data object PickBaseCurrency : AppSharedFlow<CurrencyCode>(MutableSharedFlow())

    object ShowAddedSnackbarQuick {
        val flow: MutableStateFlow<NotifyAddedSnackbarVisuals?> =
            MutableStateFlow(null)
    }

    object ShowAddedSnackbarPortfolio {
        val flow: MutableStateFlow<NotifyAddedSnackbarVisuals?> =
            MutableStateFlow(null)
    }

    object ShowAddedSnackbarPairAlert {
        val flow: MutableStateFlow<NotifyAddedSnackbarVisuals?> =
            MutableStateFlow(null)
    }
}

enum class AppSharedFlowKey {
    AddPairAlertTarget, AddPairAlertBase, SetAssetCode, AddAsset, SetQuickCode, AddQuickCode, PickBaseCurrency
}