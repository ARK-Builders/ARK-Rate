package dev.arkbuilders.rate.presentation

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import dev.arkbuilders.rate.core.presentation.theme.ARKRateTheme
import dev.arkbuilders.rate.presentation.quick.glancewidget.QuickPairsWidgetReceiver

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.WHITE
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            ARKRateTheme {
                MainScreen()
            }
        }
    }

    override fun onStop() {
        sendBroadcast(
            Intent(this, QuickPairsWidgetReceiver::class.java).apply {
                action = QuickPairsWidgetReceiver.PINNED_PAIRS_REFRESH
            },
        )
        super.onStop()
    }
}
