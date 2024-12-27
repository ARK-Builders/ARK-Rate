package dev.arkbuilders.rate.core.presentation.ui

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import dev.arkbuilders.rate.core.presentation.R
import dev.arkbuilders.rate.core.presentation.theme.ArkColor

@Composable
fun InfoDialog(
    title: String,
    desc: String,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Content(title, desc, onDismiss)
    }
}

@Preview(showBackground = true)
@Composable
private fun Content(
    title: String = "Currency Already Selected",
    desc: String =
        "Please choose a different currency to complete the pairing. " +
            "Identical currencies cannot be paired together.",
    onDismiss: () -> Unit = {},
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(12.dp)),
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Icon(
                modifier =
                    Modifier
                        .padding(top = 20.dp, start = 16.dp)
                        .align(Alignment.TopStart),
                painter = painterResource(id = R.drawable.ic_info_bg),
                contentDescription = "",
                tint = Color.Unspecified,
            )
            IconButton(
                modifier =
                    Modifier
                        .size(44.dp)
                        .padding(top = 12.dp, end = 12.dp)
                        .align(Alignment.TopEnd),
                onClick = { onDismiss() },
            ) {
                Icon(
                    modifier = Modifier.size(12.dp),
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = "",
                    tint = ArkColor.FGQuinary,
                )
            }
        }
        Text(
            modifier = Modifier.padding(top = 12.dp, start = 16.dp, end = 16.dp),
            text = title,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            color = ArkColor.TextPrimary,
        )
        Text(
            modifier = Modifier.padding(top = 4.dp, start = 16.dp, end = 16.dp, bottom = 36.dp),
            text = desc,
            fontSize = 18.sp,
            color = ArkColor.TextTertiary,
        )
    }
}
