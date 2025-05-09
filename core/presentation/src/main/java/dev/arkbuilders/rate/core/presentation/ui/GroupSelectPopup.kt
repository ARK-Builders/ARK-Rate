package dev.arkbuilders.rate.core.presentation.ui

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.arkbuilders.rate.core.domain.model.Group
import dev.arkbuilders.rate.core.presentation.R
import dev.arkbuilders.rate.core.presentation.theme.ArkColor

@Composable
fun GroupSelectPopup(
    groups: List<Group>,
    newGroupTitle: String = stringResource(R.string.new_group),
    widthPx: Int = 10,
    onGroupSelect: (Group) -> Unit,
    onNewGroupClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    Card(
        modifier = Modifier.width(with(LocalDensity.current) { widthPx.toDp() }),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clickable {
                        onNewGroupClick()
                        onDismiss()
                    },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                modifier =
                    Modifier.padding(
                        start = 16.dp,
                        top = 16.dp,
                        bottom = 16.dp,
                    ),
                painter = painterResource(id = R.drawable.ic_group_add),
                contentDescription = null,
                tint = ArkColor.FGQuinary,
            )
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = newGroupTitle,
                color = ArkColor.TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
            )
        }
        HorizontalDivider(color = ArkColor.BorderSecondary)
        groups.forEach {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clickable {
                            onGroupSelect(it)
                            onDismiss()
                        },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    modifier =
                        Modifier.padding(
                            start = 16.dp,
                            top = 16.dp,
                            bottom = 16.dp,
                        ),
                    painter = painterResource(id = R.drawable.ic_group),
                    contentDescription = null,
                    tint = ArkColor.FGQuinary,
                )
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = it.name,
                    color = ArkColor.TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}
