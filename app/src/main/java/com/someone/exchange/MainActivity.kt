package com.someone.exchange

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.someone.exchange.ui.theme.ExchangeTheme
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExchangeTheme {
                // A surface container using the 'background' color from the theme
                Scaffold(topBar = {
                    TopAppBar {

                    }
                }) {
                    InputActivity().InputView(
                        modifier = Modifier.padding(it),
                        getExternalFilesDir(null)?.absolutePath + File.separator + "a.json"
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ExchangeTheme {
        // A surface container using the 'background' color from the theme
        Scaffold(topBar = {
            TopAppBar {

            }
        }) {
            InputActivity().InputView(
                modifier = Modifier.padding(it),
                "a.json"
            )
        }
    }
}