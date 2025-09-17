package dev.arkbuilders.rate.feature.search.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.arkbuilders.rate.core.domain.model.CurrencyInfo
import dev.arkbuilders.rate.core.presentation.theme.ArkColor
import dev.arkbuilders.rate.core.presentation.ui.CurrIcon

@Composable
fun SearchCurrencyInfoItem(
    model: CurrencyInfo,
    isProhibited: Boolean,
    onClick: (CurrencyInfo) -> Unit,
) {
    val contentAlpha = if (isProhibited) 0.4f else 1f
    Column {
        Row(
            modifier =
                Modifier
                    .alpha(contentAlpha)
                    .fillMaxWidth()
                    .clickable { onClick(model) }
                    .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CurrIcon(modifier = Modifier.size(40.dp), code = model.code)
            Column(
                modifier = Modifier.padding(start = 12.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = model.code,
                    fontWeight = FontWeight.Medium,
                    color = ArkColor.TextPrimary,
                )
                if (model.name.isNotEmpty()) {
                    Text(text = model.name, color = ArkColor.TextTertiary)
                }
            }
        }
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            thickness = 1.dp,
            color = ArkColor.BorderSecondary,
        )
    }
}
