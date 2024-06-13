package dev.arkbuilders.rate.presentation.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.arkbuilders.rate.BuildConfig
import dev.arkbuilders.rate.R
import dev.arkbuilders.rate.presentation.theme.ArkColor
import dev.arkbuilders.rate.presentation.ui.AppHorDiv
import dev.arkbuilders.rate.presentation.ui.AppTopBarBack
import dev.arkbuilders.rate.presentation.utils.openLink

@Destination
@Composable
fun AboutScreen(navigator: DestinationsNavigator) {
    val ctx = LocalContext.current
    var btcDialogVisible by remember { mutableStateOf(false) }
    var ethDialogVisible by remember { mutableStateOf(false) }

    QRCryptoDialog(
        visible = btcDialogVisible,
        title = "Donate using Bitcoin",
        wallet = "bc1qx8n9r4uwpgrhgnamt2uew53lmrxd8tuevp7lv5",
        fileName = "ArkQrBtc.jpg",
        qrBitmap = R.drawable.qr_btc
    ) {
        btcDialogVisible = false
    }

    QRCryptoDialog(
        visible = ethDialogVisible,
        title = "Donate using Ethereum",
        wallet = "0x9765C5aC38175BFbd2dC7a840b63e50762B80a1b",
        fileName = "ArkQrEth.jpg",
        qrBitmap = R.drawable.qr_eth
    ) {
        ethDialogVisible = false
    }

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
    ) {
        AppTopBarBack(title = "About", navigator)
        AppHorDiv()
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                modifier = Modifier.padding(top = 32.dp),
                painter = painterResource(id = R.drawable.ic_about_logo),
                contentDescription = "",
                tint = Color.Unspecified
            )
            Text(
                modifier = Modifier.padding(top = 20.dp),
                text = stringResource(R.string.app_name),
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = ArkColor.TextPrimary
            )
            Text(
                modifier = Modifier.padding(top = 12.dp),
                text = "Version ${BuildConfig.VERSION_NAME}",
                color = ArkColor.TextTertiary
            )
            Text(
                modifier = Modifier.padding(top = 12.dp),
                text = "ARK Builders · Copyright ©2024",
                color = ArkColor.TextTertiary
            )
            Row(
                modifier = Modifier.padding(top = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SocialLink(
                    painterResource(R.drawable.ic_about_site),
                    text = "Website"
                ) {
                    ctx.openLink("https://www.ark-builders.dev/")
                }
                SocialLink(
                    painterResource(R.drawable.ic_about_telegram),
                    text = "Telegram"
                ) {
                    ctx.openLink("https://t.me/ark_builders")
                }
                SocialLink(
                    painterResource(R.drawable.ic_about_discord),
                    text = "Discord"
                ) {

                }
            }
            OutlinedButton(
                modifier = Modifier
                    .padding(top = 32.dp)
                    .fillMaxWidth(),
                onClick = { ctx.openLink(ctx.getString(R.string.privacy_policy_url)) },
                border = BorderStroke(
                    width = 1.dp,
                    color = ArkColor.BorderSecondary
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.privacy_policy),
                    fontWeight = FontWeight.SemiBold,
                    color = ArkColor.FGSecondary
                )
                Icon(
                    modifier = Modifier.padding(start = 6.dp),
                    painter = painterResource(R.drawable.ic_external),
                    contentDescription = "",
                    tint = ArkColor.FGSecondary
                )
            }
            AppHorDiv(modifier = Modifier.padding(top = 20.dp))
            Column {
                Text(
                    modifier = Modifier.padding(top = 20.dp),
                    text = "Support us",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ArkColor.TextPrimary
                )
                Text(
                    modifier = Modifier.padding(top = 4.dp),
                    text = "We greatly appreciate every bit of support!",
                    color = ArkColor.TextTertiary
                )
                Row(modifier = Modifier.padding(top = 12.dp)) {
                    DonateBtn(
                        modifier = Modifier,
                        icon = painterResource(dev.arkbuilders.rate.cryptoicons.R.drawable.btc),
                        text = "Donate using BTC",
                    ) {
                        btcDialogVisible = true
                    }
                    DonateBtn(
                        modifier = Modifier.padding(start = 12.dp),
                        icon = painterResource(dev.arkbuilders.rate.cryptoicons.R.drawable.eth),
                        text = "Donate using ETH"
                    ) {
                        ethDialogVisible = true
                    }
                }
                Row(modifier = Modifier.padding(top = 12.dp)) {
                    DonateBtn(
                        modifier = Modifier,
                        icon = painterResource(R.drawable.ic_about_patreon),
                        text = "Donate on Patreon"
                    ) {
                        ctx.openLink("https://www.patreon.com/ARKBuilders")
                    }
                    DonateBtn(
                        modifier = Modifier.padding(start = 12.dp),
                        icon = painterResource(R.drawable.ic_about_coffee),
                        text = "Buy as a coffee"
                    ) {
                        ctx.openLink("https://buymeacoffee.com/arkbuilders")
                    }
                }
                AppHorDiv(modifier = Modifier.padding(top = 20.dp))
                Row(modifier = Modifier.padding(top = 12.dp, bottom = 50.dp)) {
                    OutlinedButton(
                        modifier = Modifier,
                        onClick = { ctx.openLink("https://www.ark-builders.dev/contribute/?tab=goodFirstIssue") },
                        border = BorderStroke(
                            width = 1.dp,
                            color = ArkColor.BorderSecondary
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            modifier = Modifier.padding(8.dp),
                            text = "Discover issues to work on",
                            color = ArkColor.TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    OutlinedButton(
                        modifier = Modifier.padding(start = 12.dp),
                        onClick = { },
                        border = BorderStroke(
                            width = 1.dp,
                            color = ArkColor.BorderSecondary
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(0.dp),
                        enabled = false
                    ) {
                        Text(
                            modifier = Modifier.padding(8.dp),
                            text = "See open bounties",
                            color = ArkColor.TextPlaceHolder,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SocialLink(painter: Painter, text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(start = 8.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            painter = painter,
            contentDescription = text,
            tint = Color.Unspecified
        )
        Text(
            modifier = Modifier.padding(start = 4.dp),
            text = text,
            color = ArkColor.TextTertiary
        )
    }
}

@Composable
private fun DonateBtn(
    modifier: Modifier,
    icon: Painter,
    text: String,
    onClick: () -> Unit
) {
    OutlinedButton(
        modifier = modifier,
        onClick = onClick,
        border = BorderStroke(
            width = 1.dp,
            color = ArkColor.BorderSecondary
        ),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Icon(
            modifier = Modifier
                .padding(start = 8.dp, top = 8.dp, bottom = 8.dp)
                .size(20.dp),
            painter = icon,
            contentDescription = "",
            tint = Color.Unspecified
        )
        Text(
            modifier = Modifier.padding(start = 8.dp, end = 8.dp),
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = ArkColor.FGSecondary
        )
    }
}