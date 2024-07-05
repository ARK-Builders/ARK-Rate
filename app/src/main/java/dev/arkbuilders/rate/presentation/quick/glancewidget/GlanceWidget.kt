package dev.arkbuilders.rate.presentation.quick.glancewidget

import android.content.Context
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import arrow.core.left
import dev.arkbuilders.rate.R
import dev.arkbuilders.rate.data.CurrUtils
import dev.arkbuilders.rate.domain.model.Amount
import dev.arkbuilders.rate.domain.model.QuickPair
import dev.arkbuilders.rate.presentation.quick.QuickDisplayPair
import dev.arkbuilders.rate.presentation.theme.ArkColor
import dev.arkbuilders.rate.presentation.utils.IconUtils

class GlanceWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {

        provideContent {
            // create your AppWidget here
            val quick = QuickDisplayPair(
                pair = QuickPair(
                    id = 0,
                    from = "BTC",
                    amount = 1.0,
                    to = listOf("USD"),
                    group = null
                ),
                to = listOf(Amount("USD", 12.4))
            )
            GlanceTheme {
                LazyColumn(modifier = GlanceModifier.fillMaxSize().background(Color.White)) {
                    item {
                        Text(
                            modifier = GlanceModifier.padding(start = 16.dp, top = 24.dp),
                            text = "Calculations",
                            style = TextStyle(
                                color = ColorProvider(ArkColor.TextTertiary),
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                    item {
                        Row(modifier = GlanceModifier) {
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
                                        quick.to.first().code
                                    )
                                ),
                                contentDescription = null,
                            )
                        }
                    }

                    item {
                        Column(
                            verticalAlignment = Alignment.Vertical.CenterVertically
                        ) {
                            Text(
                                text = "${quick.pair.from} to ${quick.pair.to.joinToString(", ")}",
                            )
                            Text(
                                text = "${CurrUtils.prepareToDisplay(quick.pair.amount)} ${quick.pair.from} = " +
                                        "${CurrUtils.prepareToDisplay(quick.to.first().value)} ${quick.to.first().code}",
                            )
                        }
                    }

                }
            }
        }
    }
}