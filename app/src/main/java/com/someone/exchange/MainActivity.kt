package com.someone.exchange

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.someone.exchange.ui.Create

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Create(filePath = getExternalFilesDir("database")?.absolutePath.toString() + "/Currencies.json")
        }
    }
}