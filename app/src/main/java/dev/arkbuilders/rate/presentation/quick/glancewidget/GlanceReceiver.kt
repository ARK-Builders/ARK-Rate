package dev.arkbuilders.rate.presentation.quick.glancewidget

import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
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


class GlanceReceiver : GlanceAppWidgetReceiver() {
    private val quickRepo: QuickRepo = DIManager.component.quickRepo()
    private val convertUseCase: ConvertWithRateUseCase = DIManager.component.convertUseCase()
    private val currencyRepo: CurrencyRepo = DIManager.component.generalCurrencyRepo()

    private val coroutineScope = MainScope()

    override val glanceAppWidget: GlanceAppWidget = GlanceWidget()
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        quickRepo.allFlow().onEach { all ->
            val codeToRate = currencyRepo.getCodeToCurrencyRate().getOrNull()!!
            val displayList = all.reversed().map { pair ->
                val toDisplay = pair.to.map { code ->
                    val (amount, _) = convertUseCase(
                        Amount(pair.from, pair.amount),
                        toCode = code,
                        codeToRate
                    )
                    amount
                }
                QuickDisplayPair(pair, toDisplay)
            }
            val pages = displayList.groupBy { it.pair.group }
                .map { (group, pairs) -> QuickScreenPage(group, pairs) }

        }.launchIn(coroutineScope)
    }
}