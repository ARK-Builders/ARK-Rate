package space.taran.arkrate.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import space.taran.arkrate.R
import space.taran.arkrate.network.currencies
import space.taran.arkrate.storage.AppDatabase
import space.taran.arkrate.ui.theme.ExchangeTheme
import kotlinx.coroutines.launch
import kotlin.concurrent.thread

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun Create(filePath: String) {
    val scope = rememberCoroutineScope()
    val appDatabase = AppDatabase(filePath)
    val scaffoldState = rememberScaffoldState()
    val count = remember { mutableStateMapOf<String, String>() }
    val visibilities = remember { mutableStateMapOf<String, Boolean>() }
    val viewVisibility = remember {
        mutableStateMapOf<Views, Boolean>(
            Views.Input to true,
            Views.Output to false,
            Views.Add to false
        )
    }
    appDatabase.getAllExchange().forEach {
        count[it.name] = it.number.toString()
        visibilities[it.name] = true
    }
    var nowActivity by remember {
        mutableStateOf(Views.Input)
    }
    return ExchangeTheme {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                TopAppBar(
                    title = { Text(text = Views.Input.name) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { scaffoldState.drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = null)
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            viewVisibility[nowActivity] = false
                            viewVisibility[Views.Add] = true
                            nowActivity = Views.Add

                        }) {
                            Icon(Icons.Filled.Add, contentDescription = "add transformation")
                        }
                    }
                )
            },
            content = {

                AnimatedVisibility(viewVisibility[Views.Input]!!) {
                    InputActivity(
                        appDatabase, count, visibilities
                    ).InputView(
                        modifier = Modifier.fillMaxSize()
                            .padding(start = 16.dp, top = 16.dp, end = 16.dp),
                    )
                }
                AnimatedVisibility(viewVisibility[Views.Output]!!) {
                    OutputActivity().activity(
                        modifier = Modifier.fillMaxSize()
                            .padding(start = 16.dp, top = 16.dp, end = 16.dp), count, filePath
                    )
                }
                AnimatedVisibility(viewVisibility[Views.Add]!!) {
                    Box(Modifier.fillMaxSize()) {
                        var loading by remember { mutableStateOf(true) }
                        val a = remember<SnapshotStateMap<String, String>> { mutableStateMapOf() }
                        thread {
                            a.putAll(
                                currencies.get(filePath)
                            )
                            loading = false
                        }
                        AnimatedVisibility(
                            loading,
                            modifier = Modifier.align(Alignment.Center),
                            exit = fadeOut()
                        ) {
                            Column {
                                val imgLoader = ImageLoader.Builder(LocalContext.current)
                                    .components {
                                        if (Build.VERSION.SDK_INT >= 28) {
                                            add(ImageDecoderDecoder.Factory())
                                        } else {
                                            add(GifDecoder.Factory())
                                        }
                                    }
                                    .build()
                                val mPainter = rememberAsyncImagePainter(
                                    R.drawable.loading,
                                    imgLoader
                                )
                                Image(
                                    painter = mPainter,
                                    "Loading...",
                                    modifier = Modifier.size(64.dp)
                                )
                                Text("Loading...")
                            }
                        }
                        AnimatedVisibility(!loading) {
                            AddExchange(appDatabase, count, visibilities).AddExchangeView(
                                Modifier.fillMaxSize().padding(16.dp, 16.dp, 16.dp),
                                a,
                                filePath
                            ) {
                                viewVisibility[Views.Input] = true
                                viewVisibility[nowActivity] = false
                                nowActivity = Views.Input
                            }
                        }
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
                    when (nowActivity) {
                        Views.Input -> Icon(Icons.Filled.ArrowForward, "go to output")
                        Views.Output -> Icon(Icons.Filled.ArrowBack, "back to Input")
                        Views.Add -> Icon(Icons.Filled.ArrowBack, "back to Input")
                    }
                }, onClick = {
                    visibilities.forEach { (s, b) ->
                        if (!b) {
                            count.remove(s)
                        }
                    }
                    when (nowActivity) {
                        Views.Add -> {
                            viewVisibility[Views.Input] = true
                            viewVisibility[nowActivity] = false
                            nowActivity = Views.Input
                        }
                        Views.Input -> {
                            viewVisibility[Views.Output] = true
                            viewVisibility[nowActivity] = false
                            nowActivity = Views.Output
                        }
                        Views.Output -> {
                            viewVisibility[Views.Input] = true
                            viewVisibility[nowActivity] = false
                            nowActivity = Views.Input
                        }
                    }
                })
            }
        )
    }
}

enum class Views {
    Input, Output, Add
}