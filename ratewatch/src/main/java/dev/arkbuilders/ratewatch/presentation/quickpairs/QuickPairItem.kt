package dev.arkbuilders.ratewatch.presentation.quickpairs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester.Companion.createRefs
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import dev.arkbuilders.ratewatch.R
import dev.arkbuilders.ratewatch.data.CurrUtils
import dev.arkbuilders.ratewatch.domain.model.Amount
import dev.arkbuilders.ratewatch.domain.model.PinnedQuickPair
import dev.arkbuilders.ratewatch.domain.model.QuickPair
import dev.arkbuilders.ratewatch.presentation.WearApp
import java.time.OffsetDateTime

@Composable
fun QuickPairItem(modifier: Modifier = Modifier,
                  quick: PinnedQuickPair,
    ) {
    Row(
        modifier = modifier.padding(vertical = 2.dp).fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier =
            Modifier
                .size(12.dp)
                .padding(top = 7.dp, start = 7.dp),
            contentDescription = "",
            tint = Color.Unspecified,
            painter = painterResource(id = androidx.wear.compose.material.R.drawable.circular_vignette_bottom)
        )
        Column(
            modifier = modifier.padding(start = 8.dp),
        ) {
            Text(
                text = "${quick.pair.from} to ${quick.pair.to.joinToString(
                    separator = ", ",
                ) { it.code }}",
            )
            Text(
                text = "${CurrUtils.prepareToDisplay(quick.pair.amount)} ${quick.pair.from} = ",
            )
            Text(
                text = "${CurrUtils.prepareToDisplay(quick.pair.amount)} ${quick.actualTo.first().code}",
            )
        }
    }
}


@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun QuickPairItemPreview() {
    QuickPairItem(
        quick = PinnedQuickPair(
            pair = QuickPair(
                id = 1,
                from = "BTC",
                amount = 1.2,
                to = listOf(Amount("USD", 12.0)),
                calculatedDate = OffsetDateTime.now(),
                pinnedDate = null,
                group = null
            ),
            actualTo = listOf(Amount("USD", 12.0)),
            refreshDate = OffsetDateTime.now(),
        )
    )
}
