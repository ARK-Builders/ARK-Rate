package dev.arkbuilders.rate.feature.quickwidget.presentation.action

import android.content.Context
import android.content.Intent
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback

class OpenAppAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters,
    ) {
        context.startActivity(
            Intent().apply {
                setClassName(context, "dev.arkbuilders.rate.presentation.MainActivity")
                setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            },
        )
    }
}
