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
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import dev.arkbuilders.rate.core.domain.CurrUtils
import dev.arkbuilders.rate.core.domain.model.PinnedQuickPair
import dev.arkbuilders.rate.presentation.theme.ArkColor
import dev.arkbuilders.rate.presentation.utils.IconUtils

@Composable
fun QuickPairItem(
    quick: PinnedQuickPair,
    context: Context,
) {
    Row(
        modifier = GlanceModifier.padding(vertical = 2.dp),
    ) {
        Image(
            modifier = GlanceModifier.size(24.dp),
            provider =
                ImageProvider(
                    IconUtils.iconForCurrCode(
                        context,
                        quick.pair.from,
                    ),
                ),
            contentDescription = null,
        )
        Column(
            modifier = GlanceModifier.padding(start = 8.dp),
            verticalAlignment = Alignment.Vertical.CenterVertically,
        ) {
            Text(
                text = "${quick.pair.from} to ${quick.pair.to.joinToString(
                    separator = ", ",
                ) { it.code }}",
                style =
                    TextStyle(
                        color = ColorProvider(ArkColor.TextPrimary),
                        fontWeight = FontWeight.Medium,
                    ),
            )

            Text(
                text = "${CurrUtils.prepareToDisplay(quick.pair.amount)} ${quick.pair.from} = ",
                style =
                    TextStyle(
                        color = ColorProvider(ArkColor.TextTertiary),
                    ),
            )
            for (toAmount in quick.actualTo) {
                Row(modifier = GlanceModifier.fillMaxWidth()) {
                    Image(
                        modifier = GlanceModifier.size(16.dp),
                        provider =
                            ImageProvider(
                                IconUtils.iconForCurrCode(
                                    context,
                                    toAmount.code,
                                ),
                            ),
                        contentDescription = null,
                    )
                    Spacer(modifier = GlanceModifier.width(4.dp))
                    Text(
                        text = "${CurrUtils.prepareToDisplay(toAmount.value)} ${toAmount.code}",
                        style =
                            TextStyle(
                                color = ColorProvider(ArkColor.TextTertiary),
                            ),
                    )
                }
            }
        }
    }
}
