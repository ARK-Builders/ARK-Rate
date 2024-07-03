package dev.arkbuilders.rate.presentation.settings

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.arkbuilders.rate.R
import dev.arkbuilders.rate.presentation.theme.ArkColor
import dev.arkbuilders.rate.presentation.utils.openEmail

@Composable
fun QRCryptoDialog(
    visible: Boolean,
    title: String,
    wallet: String,
    fileName: String,
    @DrawableRes qrBitmap: Int,
    onDismiss: () -> Unit
) {
    if (visible.not())
        return

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Content(
            title = title,
            wallet = wallet,
            fileName = fileName,
            qrBitmap = qrBitmap,
            onDismiss = onDismiss
        )
    }
}

@SuppressLint("ResourceType")
@Composable
private fun Content(
    title: String,
    wallet: String,
    fileName: String,
    @DrawableRes qrBitmap: Int,
    onDismiss: () -> Unit
) {
    val ctx = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("image/jpg")) { uri ->
            uri ?: return@rememberLauncherForActivityResult
            val input = ctx.resources.openRawResource(qrBitmap)
            ctx.contentResolver.openOutputStream(uri).use { output ->
                output?.let {
                    input.copyTo(output)
                }
            }
            input.close()
        }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(Color.White, RoundedCornerShape(12.dp))
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row {
                Text(
                    modifier = Modifier.padding(top = 20.dp),
                    text = title,
                    fontWeight = FontWeight.SemiBold,
                    color = ArkColor.TextPrimary,
                    fontSize = 18.sp
                )
            }
            val emailText = buildAnnotatedString {
                append("Send us the transaction id by email ")
                pushStringAnnotation(
                    tag = "email",
                    annotation = "support@ark-builders.dev"
                )
                withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
                    append("support@ark-builders.dev")
                }
                pop()
                append(" to receive premium support. We’ll make our best to help you with any issue you encounter while using our Software.")
            }
            ClickableText(
                modifier = Modifier.padding(top = 4.dp),
                text = emailText,
                style = TextStyle.Default.copy(color = ArkColor.TextTertiary)
            ) { offset ->
                emailText
                    .getStringAnnotations(tag = "email", offset, offset)
                    .firstOrNull()
                    ?.let {
                        ctx.openEmail("support@ark-builders.dev")
                    }
            }
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                painter = painterResource(qrBitmap),
                contentDescription = "",
                contentScale = ContentScale.FillWidth
            )
            Row(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .fillMaxWidth()
                    .height(45.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, ArkColor.Border, RoundedCornerShape(8.dp)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    text = wallet,
                    fontSize = 16.sp,
                    maxLines = 1,
                    color = ArkColor.TextPlaceHolder,
                    overflow = TextOverflow.Ellipsis
                )
                VerticalDivider(
                    modifier = Modifier.height(45.dp),
                    color = ArkColor.Border
                )
                Row(
                    modifier = Modifier
                        .clickable {
                            clipboardManager.setText(AnnotatedString(wallet))
                            Toast
                                .makeText(ctx, "Wallet copied!", Toast.LENGTH_SHORT)
                                .show()
                        }
                        .padding(
                            horizontal = 16.dp,
                            vertical = 12.dp
                        )

                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_copy),
                        contentDescription = "",
                        tint = ArkColor.TextSecondary
                    )
                    Text(
                        modifier = Modifier.padding(start = 6.dp),
                        text = "Copy",
                        color = ArkColor.TextSecondary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            OutlinedButton(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 16.dp)
                    .fillMaxWidth(),
                onClick = { launcher.launch(fileName) },
                border = BorderStroke(
                    width = 1.dp,
                    color = ArkColor.BorderSecondary
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_download),
                    contentDescription = "",
                    tint = ArkColor.TextSecondary
                )
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = "Download QR Image",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = ArkColor.TextSecondary
                )
            }
        }
        IconButton(
            modifier = Modifier
                .padding(end = 12.dp, top = 12.dp)
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
}