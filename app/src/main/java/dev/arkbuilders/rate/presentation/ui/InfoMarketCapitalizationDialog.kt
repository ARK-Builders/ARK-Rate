package dev.arkbuilders.rate.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import dev.arkbuilders.rate.R
import dev.arkbuilders.rate.presentation.theme.ArkColor

@Composable
fun InfoMarketCapitalizationDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = { onDismiss() }) {
        InfoMarketCapitalizationDialogContent(onDismiss)
    }
}

@Preview
@Composable
private fun InfoMarketCapitalizationDialogContent(
    onDismiss: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(12.dp))
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Icon(
                modifier = Modifier
                    .padding(top = 20.dp, start = 16.dp)
                    .align(Alignment.TopStart),
                painter = painterResource(id = R.drawable.ic_info_bg),
                contentDescription = "",
                tint = Color.Unspecified
            )
            IconButton(
                modifier = Modifier
                    .size(44.dp)
                    .padding(top = 12.dp, end = 12.dp)
                    .align(Alignment.TopEnd),
                onClick = { onDismiss() }
            ) {
                Icon(
                    modifier = Modifier,
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = "",
                    tint = ArkColor.FGQuinary
                )
            }
        }
        Text(
            modifier = Modifier.padding(top = 12.dp, start = 16.dp, end = 16.dp),
            text = "Market Capitalization",
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            color = ArkColor.TextPrimary
        )
        Text(
            modifier = Modifier.padding(
                top = 4.dp,
                start = 16.dp,
                end = 16.dp,
                bottom = 36.dp
            ),
            text = stringResource(
                id = R.string.info_dialog_market_capitalization_description
            ),
            fontSize = 18.sp,
            color = ArkColor.TextTertiary
        )
    }
}

@Composable
fun InfoValueOfCirculatingDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = { onDismiss() }) {
        InfoValueOfCirculatingDialogContent(onDismiss)
    }
}

@Preview
@Composable
private fun InfoValueOfCirculatingDialogContent(
    onDismiss: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(12.dp))
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Icon(
                modifier = Modifier
                    .padding(top = 20.dp, start = 16.dp)
                    .align(Alignment.TopStart),
                painter = painterResource(id = R.drawable.ic_info_bg),
                contentDescription = "",
                tint = Color.Unspecified
            )
            IconButton(
                modifier = Modifier
                    .size(44.dp)
                    .padding(top = 12.dp, end = 12.dp)
                    .align(Alignment.TopEnd),
                onClick = { onDismiss() }
            ) {
                Icon(
                    modifier = Modifier,
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = "",
                    tint = ArkColor.FGQuinary
                )
            }
        }
        Text(
            modifier = Modifier.padding(top = 12.dp, start = 16.dp, end = 16.dp),
            text = "Value of Circulating Currency",
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            color = ArkColor.TextPrimary
        )
        Text(
            modifier = Modifier.padding(
                top = 4.dp,
                start = 16.dp,
                end = 16.dp,
                bottom = 36.dp
            ),
            text = stringResource(
                id = R.string.info_dialog_value_of_circulating_description
            ),
            fontSize = 18.sp,
            color = ArkColor.TextTertiary
        )
    }
}
