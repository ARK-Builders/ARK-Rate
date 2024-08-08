package dev.arkbuilders.rate.presentation

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import dev.arkbuilders.rate.presentation.theme.ARKRateTheme

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
}
