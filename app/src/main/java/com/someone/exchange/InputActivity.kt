package com.someone.exchange

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import java.io.File

@JsonClass(generateAdapter = true)
data class Total(var exchange: List<Exchange>)
data class Exchange(val name: String, var number: Double)

class InputActivity {
    @Composable
    fun InputView(modifier: Modifier, filePath: String) {
        val total = getTotal(filePath)
        val count = mutableMapOf<String, Double>()
        total.map {
            count[it.key] = remember {
                it.value
            }
        }
        return Box(modifier = modifier) {
            LazyColumn {
                items(total.keys.toList().size) {
                    Box {
                        OutlinedTextField(
                            value = count[total.keys.toList()[it]].toString(),
                            onValueChange = { a: String ->
                                try {
                                    a.toDouble()
                                } catch (e: Exception) {
                                }
                                count[total.keys.toList()[it]] = a.toDouble()
                            },
                            label = { Text(total.keys.toList()[it]) }
                        )
                    }
                }
            }
        }
    }

    private fun getTotal(filePath: String): MutableMap<String, Double> {
        val origin = File(filePath).readText()
        val moshi = Moshi.Builder().build()
        val jsonAdapter = moshi.adapter(Total::class.java)

        val json = jsonAdapter.fromJson(origin)

        val result = mutableMapOf<String, Double>()

        json?.exchange?.map {
            result[it.name] = it.number
        }

        return result
    }
}