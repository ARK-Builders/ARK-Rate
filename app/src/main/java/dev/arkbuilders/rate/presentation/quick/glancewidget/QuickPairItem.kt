package dev.arkbuilders.rate.presentation.quick.glancewidget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import dev.arkbuilders.rate.data.CurrUtils
import dev.arkbuilders.rate.domain.model.PinnedQuickPair
import dev.arkbuilders.rate.presentation.theme.ArkColor
import dev.arkbuilders.rate.presentation.utils.IconUtils

@Composable
fun QuickPairItem(
    quick: PinnedQuickPair,
    context: Context
) {
    Row(modifier = GlanceModifier.padding(vertical = 2.dp), verticalAlignment = Alignment.Vertical.CenterVertically) {
        Image(
            modifier = GlanceModifier.size(24.dp),
            provider = ImageProvider(
                IconUtils.iconForCurrCode(
                    context,
                    quick.pair.from
                )
            ),
            contentDescription = null
        )
        Image(
            modifier = GlanceModifier.size(16.dp).padding(start = (-8).dp),
            provider = ImageProvider(
                IconUtils.iconForCurrCode(
                    context,
                    quick.actualTo.first().code
                )
            ),
            contentDescription = null,
        )
        Column(
            verticalAlignment = Alignment.Vertical.CenterVertically
        ) {
            Text(
                text = "${quick.pair.from} to ${quick.pair.to.first().code}",
                style = TextStyle(
                    color = ColorProvider(ArkColor.TextPrimary),
                    fontWeight = FontWeight.Medium
                )
            )
            Text(
                text = "${CurrUtils.prepareToDisplay(quick.pair.amount)} ${quick.pair.from} = " +
                        "${CurrUtils.prepareToDisplay(quick.actualTo.first().value)} ${quick.actualTo.first().code}",
                style = TextStyle(
                    color = ColorProvider(ArkColor.TextTertiary),
                )
            )
        }
    }
}