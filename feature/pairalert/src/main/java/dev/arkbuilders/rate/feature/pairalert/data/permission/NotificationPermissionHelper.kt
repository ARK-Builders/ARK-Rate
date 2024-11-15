package dev.arkbuilders.rate.feature.pairalert.data.permission

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import dev.arkbuilders.rate.feature.pairalert.di.PairAlertScope
import javax.inject.Inject
import javax.inject.Singleton

@PairAlertScope
class NotificationPermissionHelper @Inject constructor(
    private val ctx: Context,
) {
    fun isGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(ctx, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
}
