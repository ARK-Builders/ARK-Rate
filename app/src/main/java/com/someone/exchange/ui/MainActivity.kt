package com.someone.exchange.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.someone.exchange.network.currencies
import com.someone.exchange.storage.AppDatabase
import com.someone.exchange.ui.theme.ExchangeTheme
import kotlinx.coroutines.launch

@Composable
fun Create(filePath: String) {
    val scope = rememberCoroutineScope()
    val appDatabase = AppDatabase(filePath)
    val scaffoldState = rememberScaffoldState()
    val count = remember { mutableStateMapOf<String, String>() }
    val visibilities = remember { mutableStateMapOf<String, Boolean>() }
    appDatabase.getAllExchange().forEach {
        count[it.name] = it.number.toString()
        visibilities[it.name] = true
    }
    var nowActivity by remember {
        mutableStateOf("Input")
    }
    return ExchangeTheme {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                TopAppBar(
                    title = { Text(text = "Input") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { scaffoldState.drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = null)
                        }
                    },
                    actions = {
                        IconButton(onClick = {
//                            count["a"] = 0.05.toString()
//                            visibilities["a"] = true
                            nowActivity = "Add"
                        }) {
                            Icon(Icons.Filled.Add, contentDescription = "add transformation")
                        }
                    }
                )
            },
            content = {
                when (nowActivity) {
                    "Input" -> {
                        InputActivity(
                            appDatabase, count, visibilities
                        ).InputView(
                            modifier = Modifier.fillMaxSize()
                                .padding(start = 16.dp, top = 16.dp, end = 16.dp),
                        )
                    }
                    "Output" -> {}
                    "Add" -> {
                        val a =
                            remember<SnapshotStateMap<String, String>> { mutableStateMapOf() }
                        a.putAll(
                            currencies.currencies
                        )
                        AddExchange(appDatabase, count, visibilities).AddExchangeView(
                            Modifier.fillMaxSize().padding(16.dp, 16.dp, 16.dp),
                            a
                        )
                    }
                }
            },
            drawerContent = {
                Column(modifier = Modifier.fillMaxSize()) {
                    Text(text = "not yet")
                }
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(text = {
                    Icon(Icons.Filled.ArrowForward, "go to output")
                }, onClick = {
                    when (nowActivity) {
                        "Add" -> {
                            nowActivity = "Input"
                        }
                    }
                })
            }
        )
    }

}