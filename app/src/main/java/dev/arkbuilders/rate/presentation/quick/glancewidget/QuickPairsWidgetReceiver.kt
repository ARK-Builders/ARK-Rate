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
import dev.arkbuilders.rate.domain.model.Amount
import dev.arkbuilders.rate.domain.repo.CurrencyRepo
import dev.arkbuilders.rate.domain.repo.QuickRepo
import dev.arkbuilders.rate.domain.usecase.ConvertWithRateUseCase
import dev.arkbuilders.rate.presentation.MainActivity
import dev.arkbuilders.rate.presentation.quick.QuickDisplayPair
import dev.arkbuilders.rate.presentation.quick.QuickScreenPage
import dev.arkbuilders.rate.presentation.quick.glancewidget.action.OpenAppAction
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

class QuickPairsWidgetReceiver(
    private val quickRepo: QuickRepo = DIManager.component.quickRepo(),
    private val convertUseCase: ConvertWithRateUseCase = DIManager.component.convertUseCase(),
    private val currencyRepo: CurrencyRepo = DIManager.component.generalCurrencyRepo(),
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
                val intent = Intent(context, MainActivity::class.java).apply {
                    setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
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