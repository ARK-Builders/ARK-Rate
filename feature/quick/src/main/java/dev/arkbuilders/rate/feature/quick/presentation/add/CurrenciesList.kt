package dev.arkbuilders.rate.feature.quick.presentation.add

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.arkbuilders.rate.core.presentation.CoreRString
import dev.arkbuilders.rate.core.presentation.theme.ArkColor
import dev.arkbuilders.rate.core.presentation.utils.ReorderHapticFeedback
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.ReorderableLazyListState

fun LazyListScope.currencies(
    state: AddQuickScreenState,
    reorderableLazyColumnState: ReorderableLazyListState,
    haptic: ReorderHapticFeedback,
    onAmountChanged: (String) -> Unit,
    onCurrencyRemove: (Int) -> Unit,
    onCodeChange: (Int) -> Unit,
    onSwapClick: () -> Unit,
) {
    val from = state.currencies.first()
    val to = state.currencies.drop(1)

    item {
        Text(
            modifier = Modifier.padding(top = 16.dp, start = 52.dp),
            text = stringResource(CoreRString.quick_from),
            fontWeight = FontWeight.Medium,
            color = ArkColor.TextSecondary,
        )
    }
    item(key = from.code) {
        ReorderableItem(state = reorderableLazyColumnState, key = from.code) {
            FromInput(
                code = from.code,
                amount = from.value,
                haptic = haptic,
                scope = this,
                onAmountChanged = onAmountChanged,
                onCodeChange = {
                    val index = state.currencies.indexOfFirst { it.code == from.code }
                    onCodeChange(index)
                },
            )
        }
    }
    item {
        SwapBtn(modifier = Modifier.padding(top = 16.dp), onClick = onSwapClick)
        Text(
            modifier = Modifier.padding(top = 16.dp, start = 52.dp),
            text = stringResource(CoreRString.quick_to),
            fontWeight = FontWeight.Medium,
            color = ArkColor.TextSecondary,
        )
    }
    itemsIndexed(to, key = { _, amount -> amount.code }) { index, item ->
        ReorderableItem(state = reorderableLazyColumnState, key = item.code) {
            ToResult(
                code = item.code,
                amount = item.value,
                scope = this,
                haptic = haptic,
                onCurrencyRemove = {
                    val index = state.currencies.indexOfFirst { it.code == item.code }
                    onCurrencyRemove(index)
                },
                onCodeChange = {
                    val index = state.currencies.indexOfFirst { it.code == item.code }
                    onCodeChange(index)
                },
            )
        }
    }
}
