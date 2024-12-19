package dev.arkbuilders.rate.core.presentation

import dev.arkbuilders.rate.core.domain.model.CurrencyCode
import dev.arkbuilders.rate.core.presentation.ui.NotifyAddedSnackbarVisuals
import kotlinx.coroutines.flow.MutableSharedFlow

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

    data object ShowAddedSnackbarQuick :
        AppSharedFlow<NotifyAddedSnackbarVisuals>(MutableSharedFlow())

    data object ShowAddedSnackbarPortfolio :
        AppSharedFlow<NotifyAddedSnackbarVisuals>(MutableSharedFlow())

    data object ShowAddedSnackbarPairAlert :
        AppSharedFlow<NotifyAddedSnackbarVisuals>(MutableSharedFlow())

    data object SelectGroupQuick : AppSharedFlow<String?>(MutableSharedFlow())

    data object SelectGroupPortfolio : AppSharedFlow<String?>(MutableSharedFlow())

    data object SelectGroupPairAlert : AppSharedFlow<String?>(MutableSharedFlow())
}

enum class AppSharedFlowKey {
    AddPairAlertTarget,
    AddPairAlertBase,
    SetAssetCode,
    AddAsset,
    SetQuickCode,
    AddQuickCode,
    PickBaseCurrency,
}
