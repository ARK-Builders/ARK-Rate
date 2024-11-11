@file:OptIn(ExperimentalMaterial3Api::class)

package dev.arkbuilders.rate.core.presentation.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.arkbuilders.rate.core.domain.CurrUtils
import java.math.BigDecimal

@Composable
fun LargeNumberTooltipBox(
    modifier: Modifier = Modifier,
    number: BigDecimal,
    code: String? = null,
    content: @Composable () -> Unit,
) {
    val suffix = code?.let { " ${CurrUtils.getSymbolOrCode(code)}" } ?: ""
    TooltipBox(
        modifier = modifier,
        positionProvider =
            TooltipDefaults.rememberPlainTooltipPositionProvider(),
        tooltip = { PlainTooltip { Text("${CurrUtils.prepareToDisplay(number)}$suffix") } },
        state = rememberTooltipState(isPersistent = true),
        content = content,
    )
}
