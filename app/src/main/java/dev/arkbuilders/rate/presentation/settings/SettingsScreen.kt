package dev.arkbuilders.rate.presentation.settings

import android.content.Intent
import android.net.Uri
import android.text.format.DateUtils
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.arkbuilders.rate.BuildConfig
import dev.arkbuilders.rate.R
import dev.arkbuilders.rate.di.DIManager
import dev.arkbuilders.rate.domain.repo.PreferenceKey
import dev.arkbuilders.rate.presentation.destinations.AboutScreenDestination
import dev.arkbuilders.rate.presentation.theme.ArkColor
import dev.arkbuilders.rate.presentation.ui.AppHorDiv
import dev.arkbuilders.rate.presentation.ui.AppHorDiv16
import dev.arkbuilders.rate.presentation.ui.AppTopBarBack
import dev.arkbuilders.rate.presentation.utils.DateFormatUtils
import org.orbitmvi.orbit.compose.collectAsState
import java.time.Duration
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale


@Destination
@Composable
fun SettingsScreen(navigator: DestinationsNavigator) {
    val vm: SettingsViewModel =
        viewModel(factory = DIManager.component.settingsVMFactory())

    val state by vm.collectAsState()

    Scaffold {
        Box(modifier = Modifier.padding(it)) {
            Content(
                state = state,
                navigator = navigator,
                onCrashReportsToggle = vm::onCrashReportToggle,
                onAnalyticsToggle = vm::onAnalyticsToggle
            )
        }
    }
}

@Composable
private fun Content(
    state: SettingsScreenState,
    navigator: DestinationsNavigator,
    onCrashReportsToggle: (Boolean) -> Unit,
    onAnalyticsToggle: (Boolean) -> Unit
) {
    val ctx = LocalContext.current
    Column(
        modifier = Modifier
            .padding(vertical = 32.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = stringResource(R.string.settings_quick_portfolio_alerts),
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            color = ArkColor.TextPrimary
        )
        val now = OffsetDateTime.now()

        fun formatTime(date: OffsetDateTime): String {
            val elapsed = DateFormatUtils.latestCheckElapsedTime(ctx, now, date)
            val time = DateFormatUtils.latestCheckTime(date)
            return ctx.getString(R.string.settings_elapsed_ago, elapsed) + time
        }


        val fiatDesc = state.latestFiatRefresh?.let {
            formatTime(it)
        } ?: stringResource(R.string.n_a)
        val cryptoDesc = state.latestCryptoRefresh?.let {
            formatTime(it)
        } ?: stringResource(R.string.n_a)
        val pairAlertDesc = state.latestPairAlertCheck?.let {
            formatTime(it)
        } ?: stringResource(R.string.n_a)
        LatestRefresh(
            title = stringResource(R.string.settings_latest_fiat_rates_refresh),
            description = fiatDesc
        )
        LatestRefresh(
            title = stringResource(R.string.settings_latest_crypto_rates_refresh),
            description = cryptoDesc
        )
        LatestRefresh(
            title = stringResource(R.string.settings_latest_alerts_check),
            description = pairAlertDesc
        )
        AppHorDiv16(modifier = Modifier.padding(top = 20.dp))
        if (state.showCrashReports) {
            Row(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.crash_reports),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = ArkColor.TextPrimary
                )
                Switch(
                    checked = state.crashReportsEnabled,
                    onCheckedChange = { onCrashReportsToggle(it) }
                )
            }
            AppHorDiv16(modifier = Modifier.padding(top = 20.dp))
        }
        Row(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.collect_analytics),
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = ArkColor.TextPrimary
            )
            Switch(
                checked = state.analyticsEnabled,
                onCheckedChange = { onAnalyticsToggle(it) }
            )
        }
        AppHorDiv16(modifier = Modifier.padding(top = 20.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { navigator.navigate(AboutScreenDestination) }
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_info),
                contentDescription = "",
                tint = ArkColor.TextTertiary
            )
            Text(
                modifier = Modifier.padding(start = 6.dp),
                text = stringResource(R.string.about),
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = ArkColor.TextTertiary
            )
        }
        AppHorDiv16(modifier = Modifier)
    }
}

@Composable
private fun LatestRefresh(title: String, description: String) {
    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp)) {
        Text(
            modifier = Modifier,
            text = title,
            fontWeight = FontWeight.Medium,
            color = ArkColor.TextSecondary
        )
        Text(
            modifier = Modifier,
            text = description,
            color = ArkColor.TextTertiary
        )
    }
}

private fun mapElapsedTime(now: OffsetDateTime, date: OffsetDateTime): String? {
    var dur = Duration.between(date, now)
    val days = dur.toDays()
    if (days > 0)
        return null

    val hours = dur.toHours()
    if (hours > 0)
        return "$hours hours"
    dur = dur.minusHours(hours)

    val minutes = dur.toMinutes()
    if (minutes > 0)
        return "$minutes minutes"
    dur = dur.minusMinutes(hours)

    val seconds = dur.seconds
    return "$seconds seconds"
}
