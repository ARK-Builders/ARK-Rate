package dev.arkbuilders.rate.presentation.ui

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.arkbuilders.rate.domain.model.CurrencyName
import dev.arkbuilders.rate.presentation.theme.ArkColor

@Composable
fun CurrencyInfoItem(
    name: CurrencyName,
    onClick: (CurrencyName) -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick(name) }
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CurrIcon(modifier = Modifier.size(40.dp), code = name.code)
            Column(
                modifier = Modifier.padding(start = 12.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = name.code,
                    fontWeight = FontWeight.Medium,
                    color = ArkColor.TextPrimary
                )
                if (name.name.isNotEmpty()) {
                    Text(text = name.name, color = ArkColor.TextTertiary)
                }
            }
        }
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            thickness = 1.dp,
            color = ArkColor.BorderSecondary
        )
    }
}
