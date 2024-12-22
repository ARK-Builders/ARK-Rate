package dev.arkbuilders.rate.watchapp.presentation.quickpairs.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import dev.arkbuilders.rate.core.presentation.CoreRDrawable
import dev.arkbuilders.rate.core.presentation.theme.ArkColor

@Composable
fun QuickPairsEmpty(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = CoreRDrawable.ic_empty_quick),
            contentDescription = "",
            tint = Color.Unspecified,
        )
        Text(
            modifier = Modifier.padding(top = 16.dp),
            text = "Empty Here, But Full of Possibilities!",
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
        )
        Text(
            modifier = Modifier.padding(top = 6.dp, start = 24.dp, end = 24.dp),
            text = "Calculate currency from Rate App",
            fontSize = 14.sp,
            lineHeight = 20.sp,
            color = ArkColor.TextTertiary,
            textAlign = TextAlign.Center,
        )

    }
}

@Preview(device = Devices.WEAR_OS_LARGE_ROUND, showSystemUi = true)
@Composable
fun QuickPairItemPreview() {
    QuickPairsEmpty()
}
