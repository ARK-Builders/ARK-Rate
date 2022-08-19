package com.someone.exchange.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.someone.exchange.network.currencies
import com.someone.exchange.storage.AppDatabase

class AddExchange(
    val appDatabase: AppDatabase,
    val count: SnapshotStateMap<String, String>,
    val visible: SnapshotStateMap<String, Boolean>,
) {
    @Composable
    fun AddExchangeView(
        modifier: Modifier,
        searchResult: SnapshotStateMap<String, String>,
        setNowActivity: () -> Unit
    ) {
        var searchContent by remember { mutableStateOf("") }
        val focusManager = LocalFocusManager.current
        return Column(modifier = modifier) {
            Row {
                OutlinedTextField(
                    value = searchContent,
                    onValueChange = { it1 ->
                        searchContent = it1
                        for (i in searchResult.keys.size - 1 downTo 0) {
                            searchResult.remove(searchResult.keys.toList()[i])
                        }
                        val from = currencies.currencies.filter {
                            return@filter it.key.indexOf(it1.uppercase()) != -1
                        }
                        searchResult.putAll(from)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Search")
                    }
                )
            }
            LazyColumn() {
                items(searchResult.keys.toList().sorted()) {
                    Box(Modifier.fillMaxWidth().height(32.dp).clickable(onClick = {
                        appDatabase.setExchange(it, -1.0)
                        count[it] = "-1.0"
                        visible[it] = true
                        focusManager.clearFocus()
                        setNowActivity()
                    })) {
                        Text(
                            it,
                            modifier = Modifier.align(Alignment.CenterStart),
                        )
                        currencies.currencies[it]?.let { it1 ->
                            Text(
                                it1,
                                modifier = Modifier.align(Alignment.CenterEnd)
                            )
                        }
                    }
                    Divider()
                }
            }
        }
    }
}