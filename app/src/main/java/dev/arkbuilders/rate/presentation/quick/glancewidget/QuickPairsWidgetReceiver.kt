package dev.arkbuilders.rate.presentation.quick.glancewidget

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
import dev.arkbuilders.rate.domain.model.Amount
import dev.arkbuilders.rate.domain.repo.CurrencyRepo
import dev.arkbuilders.rate.domain.repo.QuickRepo
import dev.arkbuilders.rate.domain.usecase.ConvertWithRateUseCase
import dev.arkbuilders.rate.presentation.quick.QuickDisplayPair
import dev.arkbuilders.rate.presentation.quick.QuickScreenPage
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


class QuickPairsWidgetReceiver : GlanceAppWidgetReceiver() {
    private val quickRepo: QuickRepo = DIManager.component.quickRepo()
    private val convertUseCase: ConvertWithRateUseCase = DIManager.component.convertUseCase()
    private val currencyRepo: CurrencyRepo = DIManager.component.generalCurrencyRepo()

    private val coroutineScope = MainScope()

    override val glanceAppWidget: GlanceAppWidget = QuickPairsWidget()
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        quickRepo.allFlow().onEach { all ->
            val codeToRate = currencyRepo.getCodeToCurrencyRate().getOrNull()!!
            val displayList = all.reversed().map { pair ->
                val toDisplay = pair.to.map { code ->
                    val (amount, _) = convertUseCase(
                        from = Amount(pair.from, pair.amount),
                        toCode = code,
                        _rates = codeToRate
                    )
                    amount
                }
                QuickDisplayPair(pair, toDisplay)
            }
            val pages = displayList.groupBy { it.pair.group }
                .map { (group, pairs) -> QuickScreenPage(group, pairs) }
            val quickDisplayPair = pages.first().pairs
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

    companion object {
        val quickDisplayPairs = stringPreferencesKey("quick_pair_display")
    }
}