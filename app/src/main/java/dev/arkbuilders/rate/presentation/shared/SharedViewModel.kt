package dev.arkbuilders.rate.presentation.shared

import dev.arkbuilders.rate.data.model.CurrencyAmount
import dev.arkbuilders.rate.data.model.CurrencyCode
import kotlinx.coroutines.flow.MutableSharedFlow

sealed class AppSharedFlow<T>(val flow: MutableSharedFlow<T>) {
    data object AddPairAlertTarget : AppSharedFlow<CurrencyCode>(MutableSharedFlow())
    data object AddPairAlertBase : AppSharedFlow<CurrencyCode>(MutableSharedFlow())
    data object AddCurrencyAmount :
        AppSharedFlow<Pair<CurrencyCode, CurrencyAmount>>(MutableSharedFlow())
    data object AddQuick : AppSharedFlow<Pair<Int, CurrencyCode>>(MutableSharedFlow())

    companion object {
        fun fromKey(key: AppSharedFlowKey): AppSharedFlow<*> = when (key) {
            AppSharedFlowKey.AddPairAlertTarget -> AddPairAlertTarget
            AppSharedFlowKey.AddPairAlertBase -> AddPairAlertBase
            AppSharedFlowKey.AddCurrencyAmount -> AddCurrencyAmount
            AppSharedFlowKey.AddQuick -> AddQuick
        }
    }

}

enum class AppSharedFlowKey {
    AddPairAlertTarget, AddPairAlertBase, AddCurrencyAmount, AddQuick
}