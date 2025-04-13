package dev.arkbuilders.rate.feature.portfolio.presentation.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.arkbuilders.rate.core.domain.CurrUtils
import dev.arkbuilders.rate.core.presentation.theme.ArkColor
import dev.arkbuilders.rate.core.presentation.ui.CurrIcon
import dev.arkbuilders.rate.core.presentation.ui.LargeNumberText
import dev.arkbuilders.rate.core.presentation.ui.LargeNumberTooltipBox

@Composable
fun CurrencyItem(
    amount: PortfolioDisplayAsset,
    onClick: (PortfolioDisplayAsset) -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(Color.White)
                .clickable {
                    onClick(amount)
                }
                .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CurrIcon(modifier = Modifier.size(40.dp), code = amount.asset.code)
        Column(
            modifier = Modifier.padding(start = 12.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    modifier = Modifier.padding(end = 8.dp),
                    text = amount.asset.code,
                    fontWeight = FontWeight.Medium,
                    color = ArkColor.TextPrimary,
                )
                LargeNumberTooltipBox(
                    modifier = Modifier.weight(1f),
                    number = amount.baseAmount.value,
                    code = amount.baseAmount.code,
                ) {
                    LargeNumberText(
                        number = amount.baseAmount.value,
                        code = amount.baseAmount.code,
                        fontWeight = FontWeight.Medium,
                        color = ArkColor.TextPrimary,
                        fontSize = 14.sp,
                        textAlign = TextAlign.End,
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    modifier = Modifier.padding(end = 8.dp),
                    text = CurrUtils.prepareToDisplay(amount.ratioToBase),
                    color = ArkColor.TextTertiary,
                )
                LargeNumberTooltipBox(
                    modifier = Modifier.weight(1f),
                    number = amount.asset.value,
                    code = amount.asset.code,
                ) {
                    LargeNumberText(
                        number = amount.asset.value,
                        code = amount.asset.code,
                        color = ArkColor.TextTertiary,
                        fontSize = 14.sp,
                        textAlign = TextAlign.End,
                    )
                }
            }
        }
    }
}
