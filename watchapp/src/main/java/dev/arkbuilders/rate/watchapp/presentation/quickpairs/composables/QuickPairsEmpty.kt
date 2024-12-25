package dev.arkbuilders.rate.watchapp.presentation.quickpairs.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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

@Composable
fun QuickPairsEmpty(
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            painter = painterResource(id = CoreRDrawable.ic_empty_quick),
            contentDescription = "",
            tint = Color.Unspecified,
        )
        Text(
            modifier = modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth(),
            text = "Empty Here, But Full of Possibilities!",
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
        )
        Text(
            modifier = modifier.fillMaxWidth()
                .padding(horizontal = 8.dp),
            text = "Calculate currency from Rate App",
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
        )

    }
}

@Preview(device = Devices.WEAR_OS_LARGE_ROUND, showSystemUi = true)
@Composable
fun QuickPairEmptyPreview() {
    QuickPairsEmpty()
}
