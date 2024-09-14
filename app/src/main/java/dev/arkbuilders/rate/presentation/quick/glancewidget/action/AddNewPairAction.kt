package dev.arkbuilders.rate.presentation.quick.glancewidget.action

import android.content.Context
import android.content.Intent
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import dev.arkbuilders.rate.presentation.quick.glancewidget.QuickPairsWidgetReceiver

class AddNewPairAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters,
    ) {
        val intent =
            Intent(context, QuickPairsWidgetReceiver::class.java).apply {
                action = ADD_NEW_PAIR
            }
        context.sendBroadcast(intent)
    }

    companion object {
        const val ADD_NEW_PAIR = "QUICK_PAIRS_WIDGET_NEW_PAIR"
    }
}
