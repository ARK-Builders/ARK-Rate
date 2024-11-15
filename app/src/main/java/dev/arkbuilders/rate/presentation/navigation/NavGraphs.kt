package dev.arkbuilders.rate.presentation.navigation

import com.ramcosta.composedestinations.spec.DestinationSpec
import com.ramcosta.composedestinations.spec.NavGraphSpec
import dev.arkbuilders.rate.feature.pairalert.presentation.destinations.AddPairAlertScreenDestination
import dev.arkbuilders.rate.feature.pairalert.presentation.destinations.PairAlertConditionScreenDestination
import dev.arkbuilders.rate.feature.portfolio.presentation.destinations.AddAssetScreenDestination
import dev.arkbuilders.rate.feature.portfolio.presentation.destinations.EditAssetScreenDestination
import dev.arkbuilders.rate.feature.portfolio.presentation.destinations.PortfolioScreenDestination
import dev.arkbuilders.rate.feature.quick.presentation.destinations.AddQuickScreenDestination
import dev.arkbuilders.rate.feature.quick.presentation.destinations.QuickScreenDestination
import dev.arkbuilders.rate.feature.search.presentation.destinations.SearchCurrencyScreenDestination
import dev.arkbuilders.rate.feature.settings.presentation.destinations.AboutScreenDestination
import dev.arkbuilders.rate.feature.settings.presentation.destinations.SettingsScreenDestination

// https://github.com/raamcosta/compose-destinations/issues/410
object NavGraphs {
    val root =
        object : NavGraphSpec {
            override val route = "root"

            override val destinationsByRoute =
                listOf<DestinationSpec<*>>(
                    QuickScreenDestination,
                    AddQuickScreenDestination,
                    PortfolioScreenDestination,
                    AddAssetScreenDestination,
                    EditAssetScreenDestination,
                    PairAlertConditionScreenDestination,
                    AddPairAlertScreenDestination,
                    SearchCurrencyScreenDestination,
                    SettingsScreenDestination,
                    AboutScreenDestination,
                ).associateBy { it.route }

            override val startRoute = QuickScreenDestination
        }
}
