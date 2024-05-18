package dev.arkbuilders.rate.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.arkbuilders.rate.R
import dev.arkbuilders.rate.presentation.theme.ArkColor

@Preview(showBackground = true, widthDp = 400)
@Composable
fun GroupSelectPopup(
    groups: List<String> = listOf("Companies", "Funds", "Projects"),
    widthPx: Int = 10,
    onGroupSelect: (String) -> Unit = {},
    onNewGroupClick: () -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    Card(
        modifier = Modifier.width(with(LocalDensity.current) { widthPx.toDp() }),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onNewGroupClick()
                    onDismiss()
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.padding(
                    start = 16.dp,
                    top = 16.dp,
                    bottom = 16.dp
                ),
                painter = painterResource(id = R.drawable.ic_group_add),
                contentDescription = "",
                tint = ArkColor.FGQuinary
            )
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = "New group",
                color = ArkColor.TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
        HorizontalDivider(color = ArkColor.BorderSecondary)
        groups.forEach {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onGroupSelect(it)
                        onDismiss()
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.padding(
                        start = 16.dp,
                        top = 16.dp,
                        bottom = 16.dp
                    ),
                    painter = painterResource(id = R.drawable.ic_group),
                    contentDescription = "",
                    tint = ArkColor.FGQuinary
                )
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = it,
                    color = ArkColor.TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}