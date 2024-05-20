package dev.arkbuilders.rate.presentation.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.arkbuilders.rate.presentation.theme.ArkColor

@Composable
fun AppHorDiv16(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier.padding(horizontal = 16.dp),
        thickness = 1.dp,
        color = ArkColor.BorderSecondary
    )
}

@Composable
fun AppHorDiv(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier,
        thickness = 1.dp,
        color = ArkColor.BorderSecondary
    )
}