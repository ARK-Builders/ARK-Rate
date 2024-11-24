package dev.arkbuilders.rate.core.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.arkbuilders.rate.core.presentation.R
import dev.arkbuilders.rate.core.presentation.theme.ArkColor

class NotifyAddedSnackbarVisuals(
    val title: String,
    val description: String,
) : SnackbarVisuals {
    override val actionLabel = ""
    override val duration = SnackbarDuration.Long
    override val message = ""
    override val withDismissAction = true
}

@Preview(showBackground = true)
@Composable
fun NotifyAddedSnackbarContent(
    visuals: NotifyAddedSnackbarVisuals =
        NotifyAddedSnackbarVisuals(
            "Title",
            "Desc",
        ),
    onDismiss: () -> Unit = {},
) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .background(Color.White, RoundedCornerShape(12.dp))
                .border(1.dp, ArkColor.Border, RoundedCornerShape(12.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) { },
    ) {
        Icon(
            modifier =
                Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 7.dp, start = 7.dp),
            painter = painterResource(id = R.drawable.ic_snackbar_done),
            contentDescription = "",
            tint = Color.Unspecified,
        )
        IconButton(
            modifier =
                Modifier
                    .size(36.dp)
                    .padding(top = 8.dp, end = 8.dp)
                    .align(Alignment.TopEnd),
            onClick = { onDismiss() },
        ) {
            Icon(
                modifier = Modifier,
                painter = painterResource(id = R.drawable.ic_close),
                contentDescription = "",
                tint = ArkColor.FGQuinary,
            )
        }
        Column(
            modifier =
                Modifier.padding(
                    top = 52.dp,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp,
                ),
        ) {
            Text(
                text = visuals.title,
                fontWeight = FontWeight.SemiBold,
                color = ArkColor.TextPrimary,
            )
            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = visuals.description,
                color = ArkColor.TextSecondary,
            )
        }
    }
}
