package dev.arkbuilders.rate.presentation.quick.glancewidget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import timber.log.Timber

class QuickPairsWidgetReceiver : GlanceAppWidgetReceiver() {
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
                updateAll(context)
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        updateAll(context)
    }

    private fun updateAll(context: Context) {
        coroutineScope.launch {
            GlanceAppWidgetManager(context)
                .getGlanceIds(QuickPairsWidget::class.java)
                .forEach { glanceId ->
                    glanceAppWidget.update(context, glanceId)
                }
        }
    }

    companion object {
        val currentGroupKey = stringPreferencesKey("currentGroupKey")
        const val PINNED_PAIRS_REFRESH = "PINNED_PAIRS_REFRESH"
    }
}
