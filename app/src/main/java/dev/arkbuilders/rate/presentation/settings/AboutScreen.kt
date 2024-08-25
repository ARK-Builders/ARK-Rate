package dev.arkbuilders.rate.presentation.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.arkbuilders.components.about.presentation.ArkAbout
import dev.arkbuilders.rate.BuildConfig
import dev.arkbuilders.rate.R
import dev.arkbuilders.rate.presentation.ui.AppTopBarBack

@Destination
@Composable
fun AboutScreen(navigator: DestinationsNavigator) {
    Scaffold(
        topBar = {
            AppTopBarBack(title = stringResource(R.string.about), navigator)
        },
    ) {
        Box(modifier = Modifier.padding(it)) {
            ArkAbout(
                appName = stringResource(id = R.string.app_name),
                appLogoResId = R.drawable.ic_about_logo,
                versionName = BuildConfig.VERSION_NAME,
                privacyPolicyUrl = stringResource(R.string.privacy_policy_url),
            )
        }
    }
}
