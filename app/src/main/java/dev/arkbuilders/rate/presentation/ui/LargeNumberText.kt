package dev.arkbuilders.rate.presentation.ui

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import dev.arkbuilders.rate.data.CurrUtils
import dev.arkbuilders.rate.data.divideArk
import java.math.BigDecimal

@Composable
fun LargeNumberText(
    modifier: Modifier = Modifier,
    number: BigDecimal,
    code: String? = null,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    softWrap: Boolean = true,
    style: TextStyle = LocalTextStyle.current,
    overflow: TextOverflow = TextOverflow.Clip,
) {
    var text by remember(key1 = number, key2 = code) {
        val suffix = code?.let { " ${CurrUtils.getSymbolOrCode(code)}" } ?: ""
        mutableStateOf("${CurrUtils.prepareToDisplay(number)}$suffix")
    }
    var textReadyToDraw by remember(key1 = number) {
        mutableStateOf(false)
    }
    var currentScale by remember(key1 = number) {
        mutableStateOf(Scale.ONE)
    }
    Text(
        modifier =
            modifier.drawWithContent {
                if (textReadyToDraw) {
                    drawContent()
                }
            },
        text = text,
        color = color,
        textAlign = textAlign,
        fontSize = fontSize,
        fontFamily = fontFamily,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        lineHeight = lineHeight,
        style = style,
        maxLines = 1,
        minLines = 1,
        softWrap = softWrap,
        overflow = if (textReadyToDraw) overflow else TextOverflow.Clip,
        onTextLayout = { result ->
            if (result.hasVisualOverflow) {
                if (currentScale == Scale.QUETTA) {
                    textReadyToDraw = true
                } else {
                    currentScale = currentScale.next()
                    text = shortNumber(number, currentScale, code)
                }
                return@Text
            }
            textReadyToDraw = true
        },
    )
}

private fun shortNumber(
    number: BigDecimal,
    scale: Scale,
    code: String?,
): String {
    val suffix = code?.let { " ${CurrUtils.getSymbolOrCode(code)}" } ?: ""
    val rem = number.divideArk(scale.value)
    return "${CurrUtils.prepareToDisplay(rem)}${scale.symbol}$suffix"
}

private enum class Scale(val symbol: String, val value: BigDecimal) {
    ONE("", BigDecimal(1)),
    KILO("k", BigDecimal(10).pow(3)),
    MEGA("M", BigDecimal(10).pow(6)),
    GIGA("G", BigDecimal(10).pow(9)),
    TERA("T", BigDecimal(10).pow(12)),
    PETA("P", BigDecimal(10).pow(15)),
    EXA("E", BigDecimal(10).pow(18)),
    ZETTA("Z", BigDecimal(10).pow(21)),
    YOTTA("Y", BigDecimal(10).pow(24)),
    RONNA("R", BigDecimal(10).pow(27)),
    QUETTA("Q", BigDecimal(10).pow(30)),
}

private fun Scale.next() =
    when (this) {
        Scale.ONE -> Scale.KILO
        Scale.KILO -> Scale.MEGA
        Scale.MEGA -> Scale.GIGA
        Scale.GIGA -> Scale.TERA
        Scale.TERA -> Scale.PETA
        Scale.PETA -> Scale.EXA
        Scale.EXA -> Scale.ZETTA
        Scale.ZETTA -> Scale.YOTTA
        Scale.YOTTA -> Scale.RONNA
        Scale.RONNA -> Scale.QUETTA
        Scale.QUETTA -> error("QUETTA is last scale, use it")
    }
