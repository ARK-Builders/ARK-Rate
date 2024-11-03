package dev.arkbuilders.rate.presentation.ui

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import dev.arkbuilders.rate.core.domain.model.CurrencyCode
import dev.arkbuilders.rate.presentation.utils.IconUtils

@Composable
fun CurrIcon(
    modifier: Modifier = Modifier,
    code: CurrencyCode,
) {
    val ctx = LocalContext.current
    Icon(
        modifier = modifier,
        painter = painterResource(id = IconUtils.iconForCurrCode(ctx, code)),
        contentDescription = code,
        tint = Color.Unspecified,
    )
}
