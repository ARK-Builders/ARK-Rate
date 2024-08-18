package dev.arkbuilders.rate.presentation.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.arkbuilders.rate.presentation.theme.ArkColor

@Composable
fun ListHeader(text: String) {
    Text(
        modifier =
            Modifier.padding(
                start = 16.dp,
                top = 24.dp,
                end = 16.dp,
            ),
        text = text,
        fontWeight = FontWeight.Medium,
        color = ArkColor.TextTertiary,
    )
    AppHorDiv16(modifier = Modifier.padding(top = 12.dp))
}
