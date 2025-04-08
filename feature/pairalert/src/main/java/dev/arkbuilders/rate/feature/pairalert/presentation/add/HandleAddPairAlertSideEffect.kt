package dev.arkbuilders.rate.feature.pairalert.presentation.add

import com.ramcosta.composedestinations.generated.search.destinations.SearchCurrencyScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator

suspend fun handleAddPairAlertSideEffect(
    effect: AddPairAlertScreenEffect,
    navigator: DestinationsNavigator,
    resultNavigator: ResultBackNavigator<Long>,
) {
    when (effect) {
        is AddPairAlertScreenEffect.NavigateBackWithResult ->
            resultNavigator.navigateBack(effect.newPairId)

        is AddPairAlertScreenEffect.NavigateSearchBase ->
            navigator.navigate(
                SearchCurrencyScreenDestination(
                    navKey = SearchNavResultType.BASE.name,
                    prohibitedCodes = effect.prohibitedCodes.toTypedArray(),
                ),
            )

        is AddPairAlertScreenEffect.NavigateSearchTarget ->
            navigator.navigate(
                SearchCurrencyScreenDestination(
                    navKey = SearchNavResultType.TARGET.name,
                    prohibitedCodes = effect.prohibitedCodes.toTypedArray(),
                ),
            )
    }
}
