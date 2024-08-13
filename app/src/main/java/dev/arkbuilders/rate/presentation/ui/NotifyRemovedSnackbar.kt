package dev.arkbuilders.rate.presentation.ui

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
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.arkbuilders.rate.R
import dev.arkbuilders.rate.presentation.theme.ArkColor

class NotifyRemovedSnackbarVisuals(
    val title: String,
    val description: String,
    val onUndo: () -> Unit
) : SnackbarVisuals {
    override val actionLabel = ""
    override val duration = SnackbarDuration.Long
    override val message = ""
    override val withDismissAction = true
}

@Preview(showBackground = true)
@Composable
fun NotifyRemovedSnackbarContent(
    visuals: NotifyRemovedSnackbarVisuals = NotifyRemovedSnackbarVisuals(
        title = "Title",
        description = "Desc",
        onUndo = {}
    ),
    onDismiss: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(Color.White, RoundedCornerShape(12.dp))
            .border(1.dp, ArkColor.Border, RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { }
    ) {
        Icon(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 7.dp, start = 7.dp),
            painter = painterResource(id = R.drawable.ic_info_red),
            contentDescription = "",
            tint = Color.Unspecified
        )
        IconButton(
            modifier = Modifier
                .size(36.dp)
                .padding(top = 8.dp, end = 8.dp)
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
        Column {
            Text(
                modifier = Modifier.padding(top = 52.dp, start = 16.dp, end = 16.dp),
                text = visuals.title,
                fontWeight = FontWeight.SemiBold,
                color = ArkColor.TextPrimary
            )
            Text(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp),
                text = visuals.description,
                color = ArkColor.TextSecondary
            )
            Text(
                modifier = Modifier
                    .padding(
                        start = 12.dp,
                        top = 8.dp,
                        bottom = 12.dp
                    )
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(
                            color = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        visuals.onUndo()
                        onDismiss()
                    }
                    .padding(4.dp),
                text = stringResource(R.string.undo),
                fontWeight = FontWeight.SemiBold,
                color = ArkColor.TextBrandSecondary
            )
        }
    }
}