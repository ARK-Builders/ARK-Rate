package dev.arkbuilders.rate.watchapp.presentation.quickpairs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import dev.arkbuilders.rate.core.domain.CurrUtils
import dev.arkbuilders.rate.core.domain.model.Amount
import dev.arkbuilders.rate.core.domain.model.CurrencyCode
import dev.arkbuilders.rate.core.presentation.theme.ArkColor
import dev.arkbuilders.rate.core.presentation.utils.IconUtils
import dev.arkbuilders.rate.feature.quick.domain.model.PinnedQuickPair
import dev.arkbuilders.rate.feature.quick.domain.model.QuickPair
import java.math.BigDecimal
import java.time.OffsetDateTime

@Composable
fun QuickPairItem(
    modifier: Modifier = Modifier,
    quick: PinnedQuickPair,
) {
    var isExpanded by remember {
        mutableStateOf(true)
    }
    Column(
        modifier = modifier
            .padding(12.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = modifier.align(Alignment.Start)) {
            CurrIcon(
                modifier = modifier.size(16.dp),
                code = quick.pair.from
            )
            if (quick.pair.to.size > 1) {
                Box(
                    modifier =
                    modifier
                        .size(16.dp)
                        .background(ArkColor.BGTertiary, CircleShape),
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = "+ ${quick.pair.to.size}",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 8.sp,
                        color = ArkColor.TextTertiary,
                    )
                }
            } else {
                CurrIcon(
                    modifier = modifier.size(16.dp),
                    code = quick.pair.to.first().code
                )
            }


            Text(
                text = "2 mins ago",
                modifier = modifier.fillMaxWidth(),
                textAlign = TextAlign.End
            )
        }
        if (isExpanded) {
            Text(
                modifier = modifier.fillMaxWidth(),
                text = "${CurrUtils.prepareToDisplay(quick.pair.amount)} ${quick.pair.from} = ",
            )
            quick.pair.to.forEach {
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CurrIcon(
                        modifier = Modifier.size(20.dp),
                        code = it.code
                    )
                    Text(
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp),
                        text = "${CurrUtils.prepareToDisplay(it.value)} ${it.code}",
                    )
                }
            }
        } else {
            Text(
                modifier = modifier.fillMaxWidth(),
                text = "${quick.pair.from} to ${
                    quick.pair.to.joinToString(
                        separator = ", ",
                    ) { it.code }
                }",
            )
            Text(
                modifier = modifier.fillMaxWidth(),
                text =
                "${CurrUtils.prepareToDisplay(quick.pair.amount)} ${quick.pair.from} = " +
                    "${CurrUtils.prepareToDisplay(quick.pair.to.first().value)} ${quick.pair.to.first().code}",
            )
        }
    }
}

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


@Preview(device = Devices.WEAR_OS_LARGE_ROUND, showSystemUi = true)
@Composable
fun QuickPairItemPreview() {
    QuickPairItem(
        quick = PinnedQuickPair(
            pair = QuickPair(
                id = 1,
                from = "BTC",
                amount = BigDecimal.valueOf(1.2),
                to = listOf(
                    Amount("USD", BigDecimal.valueOf(12.0)),
                    Amount("EUR", BigDecimal.valueOf(12.0))
                ),
                calculatedDate = OffsetDateTime.now(),
                pinnedDate = null,
                group = null
            ),
            actualTo = listOf(Amount("USD", BigDecimal.valueOf(12.0))),
            refreshDate = OffsetDateTime.now(),
        )
    )
}

