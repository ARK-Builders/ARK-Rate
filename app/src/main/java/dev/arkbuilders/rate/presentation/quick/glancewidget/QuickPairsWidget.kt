package dev.arkbuilders.rate.presentation.quick.glancewidget

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import dev.arkbuilders.rate.R
import dev.arkbuilders.rate.core.presentation.theme.ArkColor
import dev.arkbuilders.rate.di.DIManager
import dev.arkbuilders.rate.presentation.quick.glancewidget.action.AddNewPairAction
import dev.arkbuilders.rate.presentation.quick.glancewidget.action.NextPageAction
import dev.arkbuilders.rate.presentation.quick.glancewidget.action.OpenAppAction
import dev.arkbuilders.rate.presentation.quick.glancewidget.action.PreviousPageAction

class QuickPairsWidget : GlanceAppWidget() {
    private val getPinnedUseCase = DIManager.component.getSortedPinnedQuickPairsUseCase()

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId,
    ) {
        val pinned = getPinnedUseCase.invoke()
        provideContent {
            val prefs = currentState<Preferences>()
            val group = prefs[QuickPairsWidgetReceiver.currentGroupKey]
            val quickPairsList = pinned.filter { it.pair.group == group }
            val displayGroup = group ?: context.getString(R.string.group_default_name)
            Column(
                modifier =
                    GlanceModifier.fillMaxSize().background(Color.White)
                        .padding(horizontal = 12.dp),
            ) {
                Row(
                    modifier = GlanceModifier.fillMaxWidth().padding(top = 8.dp),
                    verticalAlignment = Alignment.Vertical.CenterVertically,
                ) {
                    Image(
                        modifier =
                            GlanceModifier.size(24.dp).padding(4.dp)
                                .clickable(actionRunCallback<AddNewPairAction>()),
                        provider = ImageProvider(R.drawable.ic_about_logo),
                        contentDescription = null,
                    )
                    Text(
                        modifier = GlanceModifier.defaultWeight(),
                        text = context.getString(R.string.quick_pinned_pairs),
                        style =
                            TextStyle(
                                color = ColorProvider(ArkColor.TextTertiary),
                                fontWeight = FontWeight.Medium,
                            ),
                    )
                    Text(
                        modifier = GlanceModifier.defaultWeight(),
                        text = displayGroup,
                        style =
                            TextStyle(
                                color = ColorProvider(ArkColor.TextTertiary),
                                fontWeight = FontWeight.Medium,
                            ),
                    )
                    Image(
                        modifier =
                            GlanceModifier.size(24.dp).padding(4.dp)
                                .clickable(actionRunCallback<PreviousPageAction>()),
                        provider = ImageProvider(R.drawable.ic_chevron_left),
                        contentDescription = null,
                    )

                    Image(
                        modifier =
                            GlanceModifier.size(24.dp).padding(4.dp)
                                .clickable(actionRunCallback<NextPageAction>()),
                        provider = ImageProvider(R.drawable.ic_chevron_right),
                        contentDescription = null,
                    )
                    Image(
                        modifier =
                            GlanceModifier.size(24.dp).padding(4.dp)
                                .clickable(actionRunCallback<AddNewPairAction>()),
                        provider =
                            ImageProvider(
                                R.drawable.ic_add_circle,
                            ),
                        contentDescription = null,
                    )
                    Text(
                        modifier =
                            GlanceModifier
                                .clickable(actionRunCallback<OpenAppAction>()),
                        text = context.getString(R.string.quick_open_app),
                        style =
                            TextStyle(
                                color =
                                    ColorProvider(
                                        ArkColor.Primary,
                                    ),
                                fontWeight = FontWeight.Medium,
                            ),
                    )
                }
                LazyColumn(modifier = GlanceModifier.fillMaxHeight()) {
                    items(quickPairsList) { quick ->
                        Column {
                            QuickPairItem(
                                quick = quick,
                                context = context,
                            )
                            Spacer(
                                modifier =
                                    GlanceModifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(Color.Gray.copy(alpha = 0.2f))
                                        .padding(vertical = 2.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}
