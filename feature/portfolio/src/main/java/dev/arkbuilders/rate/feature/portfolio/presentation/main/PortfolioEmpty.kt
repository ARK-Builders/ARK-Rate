package dev.arkbuilders.rate.feature.portfolio.presentation.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ramcosta.composedestinations.generated.portfolio.destinations.AddAssetScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.arkbuilders.rate.core.presentation.CoreRDrawable
import dev.arkbuilders.rate.core.presentation.CoreRString
import dev.arkbuilders.rate.core.presentation.theme.ArkColor
import dev.arkbuilders.rate.core.presentation.ui.AppButton

@Composable
fun PortfolioEmpty(navigator: DestinationsNavigator) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                painter = painterResource(id = CoreRDrawable.ic_empty_portfolio),
                contentDescription = null,
                tint = Color.Unspecified,
            )
            Text(
                modifier = Modifier.padding(top = 16.dp),
                text = stringResource(CoreRString.portfolio_empty_title),
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                color = ArkColor.TextPrimary,
            )
            Text(
                modifier = Modifier.padding(top = 6.dp, start = 24.dp, end = 24.dp),
                text = stringResource(CoreRString.portfolio_empty_desc),
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = ArkColor.TextTertiary,
                textAlign = TextAlign.Center,
            )
            AppButton(
                modifier = Modifier.padding(top = 24.dp),
                onClick = {
                    navigator.navigate(AddAssetScreenDestination())
                },
            ) {
                Icon(
                    painter = painterResource(id = CoreRDrawable.ic_add),
                    contentDescription = null,
                )
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = stringResource(CoreRString.portfolio_empty_new_assets),
                )
            }
        }
    }
}
