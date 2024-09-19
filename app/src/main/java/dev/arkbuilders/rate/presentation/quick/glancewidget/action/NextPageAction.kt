package dev.arkbuilders.rate.presentation.quick.glancewidget.action

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.updateAppWidgetState
import dev.arkbuilders.rate.di.DIManager
import dev.arkbuilders.rate.presentation.quick.glancewidget.QuickPairsWidget
import dev.arkbuilders.rate.presentation.quick.glancewidget.QuickPairsWidgetReceiver.Companion.currentGroupKey

class NextPageAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters,
    ) {
        val quickRepo = DIManager.component.quickRepo()
        val allGroups = quickRepo.getAllGroups()
        updateAppWidgetState(context, glanceId) { prefs ->
            val currentIndex = allGroups.indexOf(prefs[currentGroupKey])
            var newIndex = if (currentIndex == -1) 0 else currentIndex + 1
            if (newIndex > allGroups.lastIndex) {
                newIndex = 0
            }
            val newGroup = allGroups[newIndex]
            newGroup?.let {
                prefs[currentGroupKey] = newGroup
            } ?: let {
                prefs.remove(currentGroupKey)
            }
        }
        QuickPairsWidget().update(context, glanceId)
    }
}
