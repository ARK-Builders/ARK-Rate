package dev.arkbuilders.rate.data.worker

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.GsonBuilder
import dev.arkbuilders.rate.domain.model.PinnedQuickPair
import dev.arkbuilders.rate.domain.model.QuickPair
import dev.arkbuilders.rate.domain.repo.QuickRepo
import dev.arkbuilders.rate.domain.usecase.ConvertWithRateUseCase
import dev.arkbuilders.rate.presentation.quick.QuickScreenPage
import dev.arkbuilders.rate.presentation.quick.glancewidget.QuickPairsWidget
import dev.arkbuilders.rate.presentation.quick.glancewidget.QuickPairsWidgetReceiver
import kotlinx.coroutines.flow.onEach
import java.time.OffsetDateTime

class RatesRefreshWorker(
    private val context: Context,
    private val quickRepo: QuickRepo,
    private val convertUseCase: ConvertWithRateUseCase,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        quickRepo.allFlow().onEach { quick ->
            val pages = mapPairsToPages(quick)
            val quickDisplayPair = pages.first().pinned
            val quickPairs = GsonBuilder().create().toJson(quickDisplayPair)
            val glanceIds =
                GlanceAppWidgetManager(context).getGlanceIds(QuickPairsWidget::class.java)
            for (glanceId in glanceIds) {
                glanceId.let {
                    updateAppWidgetState(context, PreferencesGlanceStateDefinition, it) { pref ->
                        pref.toMutablePreferences().apply {
                            this[QuickPairsWidgetReceiver.quickDisplayPairs] = quickPairs
                        }
                    }
                    context.sendBroadcast(
                        Intent(context, QuickPairsWidgetReceiver::class.java).apply {
                            action = QuickPairsWidgetReceiver.ratesLatestRefresh
                        }
                    )
                }
            }
            QuickPairsWidget().updateAll(context)
        }
        return Result.success()
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
        const val name = "RatesRefreshWorker"
    }
}