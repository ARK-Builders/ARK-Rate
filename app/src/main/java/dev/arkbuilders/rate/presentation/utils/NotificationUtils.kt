package dev.arkbuilders.rate.presentation.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import dev.arkbuilders.rate.R
import dev.arkbuilders.rate.domain.model.PairAlert
import dev.arkbuilders.rate.presentation.MainActivity

object NotificationUtils {
    fun showPairAlert(
        pairAlert: PairAlert,
        curRatio: Double,
        ctx: Context
    ) {
        val pair = pairAlert
        val aboveOrBelow = if (pair.above()) {
            ctx.getString(R.string.above)
        } else {
            ctx.getString(R.string.below)
        }

        val title = ctx.getString(R.string.alert_notification_title)
        val builder = NotificationCompat.Builder(ctx, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle(title)
            .setContentText(
                ctx.getString(
                    R.string.alert_notification_desc,
                    pair.targetCode,
                    aboveOrBelow,
                    pair.targetPrice.toString(),
                    pair.baseCode
                )
            )
            .setContentIntent(appIntent(ctx))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        createNotificationChannel(ctx)

        with(NotificationManagerCompat.from(ctx)) {
            if (ActivityCompat.checkSelfPermission(
                    ctx,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                notify(pairAlert.id.toInt(), builder.build())
            }
        }
    }

    private fun appIntent(ctx: Context): PendingIntent {
        val intent = Intent(ctx, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        return PendingIntent.getActivity(
            ctx,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun createNotificationChannel(ctx: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = ctx.getString(R.string.app_name)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance)

            val manager = ContextCompat.getSystemService(
                ctx,
                android.app.NotificationManager::class.java
            )
            channel.enableVibration(true)
            manager?.createNotificationChannel(channel)
        }
    }

    private const val CHANNEL_ID = "arkRate"
}
