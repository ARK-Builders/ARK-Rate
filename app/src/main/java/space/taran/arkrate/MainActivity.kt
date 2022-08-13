package space.taran.arkrate

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import space.taran.arkrate.ui.Create

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Create(
                filePath = getExternalFilesDir("database")?.absolutePath.toString() + "/Currencies.json"
            )
        }
    }
}