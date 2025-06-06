package dev.arkbuilders.rate.feature.quickwidget.presentation.action

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import dev.arkbuilders.rate.feature.quickwidget.presentation.QuickPairsWidgetReceiver

class NextPageAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters,
    ) {
        QuickPairsWidgetReceiver.updateWidgetNewGroup(
            context = context,
            glanceId = glanceId,
            findNewIndex = { currentIndex, lastIndex ->
                var newIndex = currentIndex?.let { it + 1 } ?: 0
                if (newIndex > lastIndex) {
                    newIndex = 0
                }
                newIndex
            },
        )
    }
}
