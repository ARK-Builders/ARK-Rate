package dev.arkbuilders.rate.presentation.quick.glancewidget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.google.gson.GsonBuilder
import dev.arkbuilders.rate.di.DIManager
import dev.arkbuilders.rate.domain.model.PinnedQuickPair
import dev.arkbuilders.rate.domain.model.QuickPair
import dev.arkbuilders.rate.domain.repo.QuickRepo
import dev.arkbuilders.rate.domain.usecase.ConvertWithRateUseCase
import dev.arkbuilders.rate.presentation.MainActivity
import dev.arkbuilders.rate.presentation.quick.QuickScreenPage
import dev.arkbuilders.rate.presentation.quick.glancewidget.action.OpenAppAction
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import java.time.OffsetDateTime

class QuickPairsWidgetReceiver(
    private val quickRepo: QuickRepo = DIManager.component.quickRepo(),
    private val convertUseCase: ConvertWithRateUseCase = DIManager.component.convertUseCase(),
) : GlanceAppWidgetReceiver() {

    private val coroutineScope = MainScope()

    override val glanceAppWidget: GlanceAppWidget = QuickPairsWidget()
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        val action = intent.action
        Timber.d(action)
        when (action) {
            AppWidgetManager.ACTION_APPWIDGET_ENABLED ->
                getQuickPairs(context)
            OpenAppAction.OPEN_APP -> {
                context.startActivity(Intent(context, MainActivity::class.java).apply {
                    setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                })
            }
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        getQuickPairs(context)
    }

    private fun getQuickPairs(context: Context) {
        Timber.d("Get quick pairs for widget")
        quickRepo.allFlow().onEach { quick ->
            val pages = mapPairsToPages(quick)
            val quickDisplayPair = pages.first().pinned
            val quickPairs = GsonBuilder().create().toJson(quickDisplayPair)
            val glanceId =
                GlanceAppWidgetManager(context).getGlanceIds(QuickPairsWidget::class.java)
                    .firstOrNull()
            glanceId?.let {
                updateAppWidgetState(context, PreferencesGlanceStateDefinition, it) { pref ->
                    pref.toMutablePreferences().apply {
                        this[quickDisplayPairs] = quickPairs
                    }
                }
                glanceAppWidget.update(context, it)
            }
        }.launchIn(coroutineScope)
    }

    private suspend fun mapPairsToPages(pairs: List<QuickPair>): List<QuickScreenPage> {
        val pages = pairs
            .reversed()
            .groupBy { it.group }
            .map { (group, pairs) ->
                val pinnedQuickPairs = pairs.filter { it.isPinned() }
                val pinnedMappedQuickPairs = pinnedQuickPairs.map { mapPairToPinned(it) }
                val sortedPinned =
                    pinnedMappedQuickPairs.sortedByDescending { it.pair.pinnedDate }
                QuickScreenPage(group, sortedPinned, listOf())
            }
        return pages
    }

    private suspend fun mapPairToPinned(
        pair: QuickPair,
    ): PinnedQuickPair {
        val actualTo = pair.to.map { to ->
            val (amount, _) = convertUseCase.invoke(pair.from, pair.amount, to.code)
            amount
        }
        return PinnedQuickPair(pair, actualTo, OffsetDateTime.now())
    }

    companion object {
        val quickDisplayPairs = stringPreferencesKey("quick_pair_display")
    }
}