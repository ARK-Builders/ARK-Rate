package com.someone.exchange.ui

import android.os.Build.VERSION.SDK_INT
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.someone.exchange.network.getAllRatesWithCache
import com.someone.exchange.network.rates
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.concurrent.thread

class OutputActivity {
    @Composable
    fun outputActivity(
        modifier: Modifier,
        count: SnapshotStateMap<String, String>,
        filePath: String,
    ) {
        var loading by remember { mutableStateOf(true) }
        var rates: rates?
        val total: SnapshotStateMap<String, Double> = remember { mutableStateMapOf() }
        val exchange: SnapshotStateMap<String, Double> = remember { mutableStateMapOf() }
        thread {
            rates = getAllRatesWithCache(filePath)
            calculateTotal(count, rates!!).forEach {
                total[it.key] = it.value
            }
            calculateExchange(count, rates!!).forEach {
                exchange[it.key] = it.value
            }
            loading = false
        }
        return Box(modifier = modifier) {
            AnimatedVisibility(
                loading,
                modifier = Modifier.align(Alignment.Center),
                exit = fadeOut()
            ) {
                Column {
                    val imgLoader = ImageLoader.Builder(LocalContext.current)
                        .components {
                            if (SDK_INT >= 28) {
                                add(ImageDecoderDecoder.Factory())
                            } else {
                                add(GifDecoder.Factory())
                            }
                        }
                        .build()
                    val mPainter = rememberAsyncImagePainter(
                        com.someone.exchange.R.drawable.loading,
                        imgLoader
                    )
                    Image(painter = mPainter, "Loading...", modifier = Modifier.size(64.dp))
                    Text("Loading...")
                }
            }
            AnimatedVisibility(!loading) {
                LazyColumn {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth(), elevation = 8.dp
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text("TOTAL", fontSize = 24.sp)
                                Divider()
                                total.forEach {
                                    Box(modifier = Modifier.fillMaxWidth().height(32.dp)) {
                                        val format = DecimalFormat("0.###")
                                        format.roundingMode = RoundingMode.FLOOR
                                        Text(
                                            it.key,
                                            modifier = Modifier.align(Alignment.CenterStart)
                                        )
                                        Text(
                                            format.format(it.value),
                                            modifier = Modifier.align(Alignment.CenterEnd)
                                        )
                                        Divider()
                                    }
                                }
                            }
                        }
                    }
                    item {
                        Spacer(Modifier.fillMaxWidth().height(16.dp))
                    }
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = 8.dp
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                var visible by remember { mutableStateOf(true) }
                                Box(modifier = Modifier.clickable(onClick = {
                                    visible = !visible
                                }).fillMaxWidth()) {
                                    Text("EXCHANGE", fontSize = 24.sp)
                                }
                                Divider()
                                AnimatedVisibility(visible) {
                                    Column {
                                        exchange.forEach {
                                            Box(modifier = Modifier.fillMaxWidth().height(32.dp)) {
                                                val format = DecimalFormat("0.###")
                                                format.roundingMode = RoundingMode.FLOOR
                                                Text(
                                                    it.key,
                                                    modifier = Modifier.align(Alignment.CenterStart)
                                                )
                                                Text(
                                                    format.format(it.value),
                                                    modifier = Modifier.align(Alignment.CenterEnd)
                                                )
                                                Divider()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    item {
                        Spacer(Modifier.fillMaxWidth().height(64.dp))
                    }
                }
            }
        }
    }

    private fun calculateTotal(
        count: SnapshotStateMap<String, String>,
        rates: rates
    ): Map<String, Double> {
        val result = mutableMapOf<String, Double>()
        var USD = 0.0

        count.forEach {
            USD += UtilsBigDecimal.mul(
                if ((if (it.value.isEmpty()) 0.0 else it.value.toDouble()) == -1.0) 0.0 else it.value.toDouble(),
                UtilsBigDecimal.div(1.0, rates.rates[it.key]!!)
            )
        }

        count.forEach {
            result[it.key] = UtilsBigDecimal.mul(USD, rates.rates[it.key]!!)
        }

        return result
    }

    private fun calculateExchange(
        count: SnapshotStateMap<String, String>,
        rates: rates
    ): Map<String, Double> {

        val result = mutableMapOf<String, Double>()
        var USD = 0.0

        count.forEach {
            USD += UtilsBigDecimal.mul(
                if (it.value.isEmpty()) 0.0 else it.value.toDouble(),
                UtilsBigDecimal.div(1.0, rates.rates[it.key]!!)
            )
        }

        count.forEach { i ->
            val iToUsd = UtilsBigDecimal.div(1.0, rates.rates[i.key]!!)
            count.forEach { j ->
                if (j.key != i.key) {
                    result["${i.key}/${j.key}"] =
                        UtilsBigDecimal.mul(iToUsd, rates.rates[j.key]!!)
                }
            }
        }

        return result
    }
}

private object UtilsBigDecimal {

    // 需要精确至小数点后几位
    const val DECIMAL_POINT_NUMBER: Int = 8

    // 加法运算
    @JvmStatic
    fun mul(d1: Double, d2: Double): Double =
        BigDecimal(d1).multiply(BigDecimal(d2))
            .setScale(DECIMAL_POINT_NUMBER, BigDecimal.ROUND_DOWN)
            .toDouble()

    // 除法运算
    @JvmStatic
    fun div(d1: Double, d2: Double): Double =
        BigDecimal(d1).divide(BigDecimal(d2), DECIMAL_POINT_NUMBER, BigDecimal.ROUND_DOWN)
            .toDouble()

}