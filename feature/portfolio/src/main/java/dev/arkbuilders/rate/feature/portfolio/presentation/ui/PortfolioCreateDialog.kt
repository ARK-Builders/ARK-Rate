package dev.arkbuilders.rate.feature.portfolio.presentation.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import dev.arkbuilders.rate.core.presentation.CoreRDrawable
import dev.arkbuilders.rate.core.presentation.CoreRString
import dev.arkbuilders.rate.core.presentation.theme.ArkColor

@Composable
fun PortfolioCreateDialog(
    onDismiss: () -> Unit,
    onConfirmClick: (String) -> Unit,
) {
    Dialog(onDismissRequest = { onDismiss() }) {
        PortfolioCreateDialogContent(onDismiss, onConfirmClick)
    }
}

@Preview(showBackground = true)
@Composable
private fun PortfolioCreateDialogContent(
    onDismiss: () -> Unit = {},
    onConfirmClick: (String) -> Unit = {},
) {
    var input by remember {
        mutableStateOf("")
    }

    Box(
        modifier =
            Modifier
                .background(Color.White, RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp)),
    ) {
        Icon(
            modifier = Modifier.padding(start = 30.dp, top = 35.dp),
            painter = painterResource(id = CoreRDrawable.ic_group),
            contentDescription = "",
            tint = ArkColor.FGSecondary,
        )
        IconButton(
            modifier =
                Modifier
                    .padding(end = 12.dp, top = 12.dp)
                    .align(Alignment.TopEnd),
            onClick = { onDismiss() },
        ) {
            Icon(
                modifier = Modifier,
                painter = painterResource(id = CoreRDrawable.ic_close),
                contentDescription = "",
                tint = ArkColor.FGQuinary,
            )
        }
        GroupIconCircles(modifier = Modifier.absoluteOffset((-80).dp, (-76).dp))
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 80.dp, start = 16.dp, end = 16.dp),
        ) {
            Text(
                modifier = Modifier.padding(top = 28.dp),
                text = stringResource(CoreRString.portfolio_name_dialog_title),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = ArkColor.TextPrimary,
            )
            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = stringResource(CoreRString.portfolio_name_dialog_desc),
                color = ArkColor.TextTertiary,
            )

            Text(
                modifier = Modifier.padding(top = 20.dp),
                text = stringResource(CoreRString.portfolio_name_dialog_portfolio_name),
                fontWeight = FontWeight.Medium,
                color = ArkColor.TextSecondary,
            )
            OutlinedTextField(
                modifier =
                    Modifier
                        .padding(top = 6.dp)
                        .fillMaxWidth(),
                value = input,
                onValueChange = { input = it },
                textStyle =
                    TextStyle.Default.copy(
                        fontSize = 16.sp,
                        color = ArkColor.TextPrimary,
                    ),
                shape = RoundedCornerShape(8.dp),
                colors =
                    OutlinedTextFieldDefaults
                        .colors(
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            focusedBorderColor = ArkColor.Border,
                            unfocusedBorderColor = ArkColor.Border,
                        ),
                placeholder = {
                    Text(
                        text = stringResource(CoreRString.portfolio_name_dialog_placeholder),
                        fontSize = 16.sp,
                        color = ArkColor.TextPlaceHolder,
                    )
                },
            )
            Button(
                modifier =
                    Modifier
                        .padding(top = 24.dp)
                        .fillMaxWidth(),
                onClick = {
                    onConfirmClick(input)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = ArkColor.Primary),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text(
                    text = stringResource(CoreRString.confirm),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                )
            }
            OutlinedButton(
                modifier =
                    Modifier
                        .padding(top = 12.dp, bottom = 16.dp)
                        .fillMaxWidth(),
                onClick = { onDismiss() },
                border =
                    BorderStroke(
                        width = 1.dp,
                        color = ArkColor.BorderSecondary,
                    ),
                colors = ButtonDefaults.outlinedButtonColors(),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text(
                    text = stringResource(CoreRString.cancel),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ArkColor.FGSecondary,
                )
            }
        }
    }
}

@Composable
private fun GroupIconCircles(modifier: Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Box(
            modifier =
                Modifier
                    .size(48.dp)
                    .border(
                        width = 1.dp,
                        shape = RoundedCornerShape(10.dp),
                        color = ArkColor.BorderSecondary.copy(alpha = 0.95f),
                    ),
        )
        Box(
            modifier =
                Modifier
                    .size(96.dp)
                    .border(
                        width = 1.dp,
                        shape = CircleShape,
                        color = ArkColor.BorderSecondary.copy(alpha = 0.8f),
                    ),
        )
        Box(
            modifier =
                Modifier
                    .size(144.dp)
                    .border(
                        width = 1.dp,
                        shape = CircleShape,
                        color = ArkColor.BorderSecondary.copy(alpha = 0.6f),
                    ),
        )
        Box(
            modifier =
                Modifier
                    .size(192.dp)
                    .border(
                        width = 1.dp,
                        shape = CircleShape,
                        color = ArkColor.BorderSecondary.copy(alpha = 0.3f),
                    ),
        )
        Box(
            modifier =
                Modifier
                    .size(240.dp)
                    .border(
                        width = 1.dp,
                        shape = CircleShape,
                        color = ArkColor.BorderSecondary.copy(alpha = 0.2f),
                    ),
        )
    }
}
