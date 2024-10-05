package dev.arkbuilders.rate.presentation.quick.glancewidget.action

import android.content.Context
import android.content.Intent
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.updateAppWidgetState
import dev.arkbuilders.rate.presentation.MainActivity
import dev.arkbuilders.rate.presentation.quick.glancewidget.QuickPairsWidgetReceiver

class AddNewPairAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters,
    ) {
        var group: String? = null

        updateAppWidgetState(context, glanceId) { pref ->
            group = pref[QuickPairsWidgetReceiver.currentGroupKey]
        }
        context.startActivity(
            Intent(context, MainActivity::class.java).apply {
                putExtra(ADD_NEW_PAIR, "ADD_NEW_PAIR")
                putExtra(ADD_NEW_PAIR_GROUP_KEY, group)
                setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            },
        )
    }

    companion object {
        const val ADD_NEW_PAIR = "QUICK_PAIRS_WIDGET_NEW_PAIR"
        const val ADD_NEW_PAIR_GROUP_KEY = "ADD_NEW_PAIR_GROUP_KEY"
    }
}
