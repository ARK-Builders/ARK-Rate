package dev.arkbuilders.rate.core.presentation.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.arkbuilders.rate.core.presentation.R
import dev.arkbuilders.rate.core.presentation.theme.ArkColor

@Composable
fun DropDownWithIcon(
    modifier: Modifier,
    onClick: () -> Unit,
    title: String,
    icon: Painter,
) {
    Row(
        modifier =
            modifier
                .height(44.dp)
                .border(
                    1.dp,
                    ArkColor.Border,
                    RoundedCornerShape(8.dp),
                )
                .clip(RoundedCornerShape(8.dp))
                .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.padding(start = 16.dp),
            painter = icon,
            contentDescription = "",
            tint = ArkColor.FGSecondary,
        )
        Text(
            modifier =
                Modifier
                    .padding(start = 8.dp)
                    .weight(1f),
            text = title,
            fontSize = 16.sp,
            color = ArkColor.TextPlaceHolder,
        )
        Icon(
            modifier = Modifier.padding(end = 20.dp),
            painter = painterResource(id = R.drawable.ic_chevron),
            contentDescription = "",
            tint = ArkColor.FGSecondary,
        )
    }
}
