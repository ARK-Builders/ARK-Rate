package dev.arkbuilders.rate.presentation.quick.glancewidget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.google.gson.GsonBuilder
import dev.arkbuilders.rate.di.DIManager
import dev.arkbuilders.rate.domain.model.PinnedQuickPair
import dev.arkbuilders.rate.domain.model.QuickPair
import dev.arkbuilders.rate.domain.repo.QuickRepo
import dev.arkbuilders.rate.domain.usecase.ConvertWithRateUseCase
import dev.arkbuilders.rate.presentation.MainActivity
import dev.arkbuilders.rate.presentation.quick.QuickScreenPage
import dev.arkbuilders.rate.presentation.quick.glancewidget.action.AddNewPairAction
import dev.arkbuilders.rate.presentation.quick.glancewidget.action.NextPageAction
import dev.arkbuilders.rate.presentation.quick.glancewidget.action.OpenAppAction
import dev.arkbuilders.rate.presentation.quick.glancewidget.action.PreviousPageAction
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.OffsetDateTime

class QuickPairsWidgetReceiver(
    private val quickRepo: QuickRepo = DIManager.component.quickRepo(),
    private val convertUseCase: ConvertWithRateUseCase = DIManager.component.convertUseCase(),
) : GlanceAppWidgetReceiver() {
    private val coroutineScope = MainScope()

    override val glanceAppWidget: GlanceAppWidget = QuickPairsWidget()

    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        super.onReceive(context, intent)
        val action = intent.action
        Timber.d(action)
        when (action) {
            AppWidgetManager.ACTION_APPWIDGET_ENABLED, PINNED_PAIRS_REFRESH ->
                retrievePinnedPairsAt(context, 0)

            OpenAppAction.OPEN_APP -> {
                context.startActivity(
                    Intent(context, MainActivity::class.java).apply {
                        setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    },
                )
            }

            AddNewPairAction.ADD_NEW_PAIR -> {
                context.startActivity(
                    Intent(context, MainActivity::class.java).apply {
                        putExtra(AddNewPairAction.ADD_NEW_PAIR, "ADD_NEW_PAIR")
                        setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    },
                )
            }

            NextPageAction.NEXT_PAGE -> {
                handleNextSelected(context)
            }

            PreviousPageAction.PREVIOUS_PAGE -> {
                handlePreviousSelected(context)
            }
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        retrievePinnedPairsAt(context, 0)
    }

    private fun retrievePinnedPairsAt(
        context: Context,
        pageIndex: Int,
    ) {
        quickRepo.allFlow().onEach { quick ->
            val pages = mapPairsToPages(quick)
            val ungroupedPinnedPairs = pages[pageIndex].pinned
            val quickPairs = GsonBuilder().create().toJson(ungroupedPinnedPairs)
            updateWidgetData(
                context = context,
                quickPairs = quickPairs,
                pageIndex = pageIndex,
                pageName = pages[pageIndex].group,
            )
            glanceAppWidget.updateAll(context)
        }.launchIn(coroutineScope)
    }

    private suspend fun mapPairsToPages(pairs: List<QuickPair>): List<QuickScreenPage> {
        val pages =
            pairs
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

    private suspend fun mapPairToPinned(pair: QuickPair): PinnedQuickPair {
        val actualTo =
            pair.to.map { to ->
                val (amount, _) = convertUseCase.invoke(pair.from, pair.amount, to.code)
                amount
            }
        return PinnedQuickPair(pair, actualTo, OffsetDateTime.now())
    }

    private suspend fun updateWidgetData(
        quickPairs: String,
        context: Context,
        pageIndex: Int,
        pageName: String?,
    ) {
        val glanceIds =
            GlanceAppWidgetManager(context).getGlanceIds(QuickPairsWidget::class.java)
        for (glanceId in glanceIds) {
            glanceId.let { id ->
                updateAppWidgetState(context, PreferencesGlanceStateDefinition, id) { pref ->
                    pref.toMutablePreferences().apply {
                        this[quickDisplayPairs] = quickPairs
                        this[currentPage] = pageIndex
                        this[currentPageName] = pageName ?: "Not grouped"
                    }
                }
            }
        }
    }

    private fun handleNextSelected(context: Context) {
        coroutineScope.launch {
            val pages = mapPairsToPages(quickRepo.getAll())
            val currentPage = getCurrentPage(context = context)
            if (currentPage >= pages.lastIndex) {
                retrievePinnedPairsAt(context = context, pageIndex = 0)
            } else {
                retrievePinnedPairsAt(context = context, pageIndex = currentPage + 1)
            }
        }
    }

    private fun handlePreviousSelected(context: Context) {
        coroutineScope.launch {
            val pages = mapPairsToPages(quickRepo.getAll())
            val currentPage = getCurrentPage(context = context)
            if (currentPage == 0) {
                retrievePinnedPairsAt(context = context, pageIndex = pages.lastIndex)
            } else {
                retrievePinnedPairsAt(context = context, pageIndex = currentPage - 1)
            }
        }
    }

    private suspend fun getCurrentPage(context: Context): Int {
        val glanceIds =
            GlanceAppWidgetManager(context).getGlanceIds(QuickPairsWidget::class.java)
        for (glanceId in glanceIds) {
            val currentPage =
                getAppWidgetState(
                    context = context,
                    definition = PreferencesGlanceStateDefinition,
                    glanceId = glanceId,
                ).toPreferences()[currentPage]
            return currentPage ?: 0
        }
        return 0
    }

    companion object {
        val quickDisplayPairs = stringPreferencesKey("quick_pair_display")
        val currentPage = intPreferencesKey("currentPage")
        val currentPageName = stringPreferencesKey("currentPageName")
        const val PINNED_PAIRS_REFRESH = "PINNED_PAIRS_REFRESH"
    }
}
