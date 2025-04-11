package dev.arkbuilders.rate.feature.pairalert.presentation.add

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.arkbuilders.rate.core.presentation.theme.ArkColor

@Composable
fun SegmentBtnRow(
    modifier: Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier =
            modifier
                .background(ArkColor.BGSecondaryAlt)
                .border(1.dp, ArkColor.BorderSecondary, RoundedCornerShape(12.dp)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        content()
    }
}

@Composable
fun SegmentBtn(
    modifier: Modifier,
    title: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (enabled) 1.dp else 0.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = if (enabled) Color.White else ArkColor.BGSecondaryAlt,
            ),
        onClick = {
            onClick()
        },
    ) {
        Text(
            modifier =
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 10.dp),
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (enabled) ArkColor.TextBrandSecondary else ArkColor.TextTertiary,
        )
    }
}
