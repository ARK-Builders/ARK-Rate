package dev.arkbuilders.rate.core.presentation.ui

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.arkbuilders.rate.core.presentation.theme.ArkColor

@Composable
fun ArkLargeTextField(
    modifier: Modifier,
    value: String,
    onValueChange: (String) -> Unit,
) {
    BasicTextField(
        modifier =
            modifier
                .width(IntrinsicSize.Min)
                .defaultMinSize(minWidth = 10.dp),
        value = value,
        onValueChange = onValueChange,
        textStyle =
            LocalTextStyle.current.copy(
                fontSize = 36.sp,
                color = ArkColor.TextPrimary,
                fontWeight = FontWeight.SemiBold,
            ),
        keyboardOptions =
            KeyboardOptions.Default
                .copy(keyboardType = KeyboardType.Number),
        singleLine = true,
    )
}
