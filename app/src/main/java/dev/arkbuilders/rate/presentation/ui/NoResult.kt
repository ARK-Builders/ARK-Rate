package dev.arkbuilders.rate.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.arkbuilders.rate.R
import dev.arkbuilders.rate.presentation.theme.ArkColor

@Composable
fun NoResult() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                modifier = Modifier.size(72.dp),
                painter = painterResource(R.drawable.ic_search_refraction),
                contentDescription = "",
                tint = ArkColor.Secondary
            )
            Text(
                text = stringResource(R.string.no_result),
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = ArkColor.TextPrimary
            )
        }
    }
}
