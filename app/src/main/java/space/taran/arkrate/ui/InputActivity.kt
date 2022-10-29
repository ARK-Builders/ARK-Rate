package space.taran.arkrate.ui

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import space.taran.arkrate.storage.AppDatabase

class InputActivity(
    val appDatabase: AppDatabase,
    val count: SnapshotStateMap<String, String>,
    val visible: SnapshotStateMap<String, Boolean>
) {
    @Composable
    fun InputView(modifier: Modifier) {
        return LazyColumn(modifier = modifier) {
            items(count.size) { i ->
                AnimatedVisibility(
                    visible[count.keys.toList()[i]]!!,
                    exit = slideOutHorizontally()
                ) {
                    Row(modifier.fillMaxWidth()) {
                        var textValue by remember { mutableStateOf(if ((count[count.keys.toList()[i]]?: "-1.0").toDouble() == -1.0
                        ) "" else count[count.keys.toList()[i]].toString()) }
                        OutlinedTextField(
                            value = textValue,
                            onValueChange = {
                                textValue=it
                                var oneDot = true
                                val result = it.filter {
                                    if (it == ".".toCharArray()[0] && oneDot) {
                                        oneDot = false
                                        Log.d("inputview", "InputView: $it")
                                        return@filter true
                                    }
                                    return@filter it.isDigit()
                                }
                                appDatabase.setExchange(
                                    count.keys.toList()[i],
                                    if (result.isEmpty()) {
                                        -1.0
                                    } else {
                                        result.toDouble()
                                    }
                                )
                                count[count.keys.toList()[i]] =
                                    (result.ifEmpty { (-1.0).toString() })
                            },
                            trailingIcon = {
                                Icon(Icons.Default.Clear,
                                    contentDescription = "clear text",
                                    modifier = Modifier
                                        .clickable {
                                            appDatabase.setExchange(count.keys.toList()[i], -1.0)
                                            count[count.keys.toList()[i]] = "-1.0"
                                            visible[count.keys.toList()[i]] = true
                                            textValue=""
                                        }
                                )
                            },
                            label = { Text(count.keys.toList()[i]) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.height(60.dp).fillMaxWidth(0.9f),
                            maxLines = 1
                        )
                        IconButton(onClick = {
                            appDatabase.remove(count.keys.toList()[i])
                            visible[count.keys.toList()[i]] = false
                        }, modifier = Modifier.padding(8.dp)) {
                            Icon(Icons.Filled.Delete, "Delete")
                        }
                    }
                }
            }

            //if don't have any exchange show this text
            if (count.size == 0) {
                item {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Text(
                            modifier = Modifier.align(Alignment.Center),
                            text = "Click the Add button at the top right to add a new exchange rate."
                        )
                    }
                }
            }
        }
    }
}