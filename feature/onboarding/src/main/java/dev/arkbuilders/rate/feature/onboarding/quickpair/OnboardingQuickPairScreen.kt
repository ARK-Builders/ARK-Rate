package dev.arkbuilders.rate.feature.onboarding.quickpair

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.ExternalModuleGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.arkbuilders.rate.core.domain.model.Amount
import dev.arkbuilders.rate.core.presentation.CoreRString
import dev.arkbuilders.rate.core.presentation.R
import dev.arkbuilders.rate.core.presentation.theme.ArkColor
import dev.arkbuilders.rate.core.presentation.ui.CurrencyInfoItem
import dev.arkbuilders.rate.core.presentation.ui.ListHeader
import dev.arkbuilders.rate.core.presentation.ui.SearchTextField
import dev.arkbuilders.rate.core.presentation.utils.DateFormatUtils
import dev.arkbuilders.rate.feature.onboarding.di.OnboardingComponentHolder
import dev.arkbuilders.rate.feature.onboarding.quick.MockBottomNavigation
import dev.arkbuilders.rate.feature.onboarding.quick.MockQuickItem
import dev.arkbuilders.rate.feature.onboarding.spotlight.Spotlight
import dev.arkbuilders.rate.feature.onboarding.spotlight.SpotlightShape
import dev.arkbuilders.rate.feature.onboarding.spotlight.SpotlightTooltip
import dev.arkbuilders.rate.feature.onboarding.spotlight.TooltipPosition
import dev.arkbuilders.rate.feature.quick.di.QuickComponentHolder
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
@Destination<ExternalModuleGraph>
fun OnboardingQuickPairScreen(navigator: DestinationsNavigator) {
    val ctx = LocalContext.current
    val quickComponent =
        remember {
            QuickComponentHolder.provide(ctx)
        }
    val onboardingComponent =
        remember {
            OnboardingComponentHolder.provide(ctx)
        }
    val viewModel: OnboardingQuickPairViewModel =
        viewModel(
            factory =
                onboardingComponent.onboardingQuickPairViewModelFactory()
                    .create(quickComponent.quickRepo()),
        )

    var quickPairRect by remember { (mutableStateOf<Rect?>(null)) }

    val quickPairTranslatePx by rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = with(LocalDensity.current) { 120.dp.toPx() },
        animationSpec =
            infiniteRepeatable(
                animation =
                    tween(
                        durationMillis = 1800,
                        delayMillis = 500,
                        easing = LinearEasing,
                    ),
            ),
    )

    val state by viewModel.collectAsState()

    viewModel.collectSideEffect { effect ->
        when (effect) {
            OnboardingQuickPairEffect.Finish -> navigator.popBackStack()
        }
    }

    val step = OnboardingQuickPairStep.entries.get(state.stepIndex)

    val translatePairModifier =
        when (step) {
            OnboardingQuickPairStep.PairSwipeToRight ->
                Modifier.graphicsLayer { translationX = quickPairTranslatePx }

            OnboardingQuickPairStep.PairSwipeToLeft ->
                Modifier.graphicsLayer { translationX = -quickPairTranslatePx }

            OnboardingQuickPairStep.PinnedSwipeToRight ->
                Modifier.graphicsLayer {
                    translationX = quickPairTranslatePx
                }

            else -> Modifier
        }

    if (state.initialized.not())
        return

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                contentColor = Color.White,
                containerColor = ArkColor.Secondary,
                shape = CircleShape,
                onClick = {},
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(CoreRString.add))
            }
        },
        bottomBar = {
            MockBottomNavigation(
                portfolioModifier = Modifier,
                pairAlertModifier = Modifier,
            )
        },
    ) {
        Column(Modifier.padding(it)) {
            SearchTextField(
                modifier =
                    Modifier.padding(
                        top = 16.dp,
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp,
                    ),
                text = "",
            ) {}
            Column(
                modifier =
                    Modifier.onGloballyPositioned { coordinates ->
                        quickPairRect = coordinates.boundsInRoot()
                    },
            ) {
                if (state.stepIndex <= 1) {
                    ListHeader(text = stringResource(CoreRString.quick_calculations))
                } else {
                    ListHeader(text = stringResource(CoreRString.quick_pinned_calculations))
                }
                Box(propagateMinConstraints = true) {
                    DismissBackground(
                        swipeToDelete =
                            step == OnboardingQuickPairStep.PairSwipeToLeft,
                        isPinned =
                            step == OnboardingQuickPairStep.PinnedSwipeToRight,
                    )
                    MockQuickItem(
                        modifier = translatePairModifier,
                        from = Amount(state.pair.from, state.pair.amount),
                        to = state.pair.to,
                        dateText =
                            stringResource(
                                CoreRString.quick_calculated_on,
                                DateFormatUtils.calculatedOn(state.pair.calculatedDate),
                            ),
                    ) { }
                }
            }
            ListHeader(text = stringResource(CoreRString.all_currencies))
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(state.currencies, key = { it.code }) { name ->
                    CurrencyInfoItem(name) { }
                }
            }
        }
    }

    quickPairRect?.let {
        Spotlight(
            targetRect = it,
            onTargetClicked = {},
            onDismiss = {},
            shape = SpotlightShape.Rect,
            padding = 0.dp,
        )
    }

    when (step) {
        OnboardingQuickPairStep.PairSwipeToRight -> {
            quickPairRect?.let {
                SpotlightTooltip(
                    targetRect = it,
                    titleText = stringResource(CoreRString.onboarding_quick_pair_1_title),
                    descText = stringResource(CoreRString.onboarding_quick_pair_1_desc),
                    buttonText = stringResource(CoreRString.next),
                    position = TooltipPosition.Below,
                    targetPadding = 24.dp,
                    onClick = { viewModel.onNext() },
                    onSkip = { viewModel.onSkip() },
                )
            }
        }

        OnboardingQuickPairStep.PairSwipeToLeft -> {
            quickPairRect?.let {
                SpotlightTooltip(
                    targetRect = it,
                    titleText = stringResource(CoreRString.onboarding_quick_pair_2_title),
                    descText = stringResource(CoreRString.onboarding_quick_pair_2_desc),
                    buttonText = stringResource(CoreRString.next),
                    position = TooltipPosition.Below,
                    targetPadding = 24.dp,
                    onClick = { viewModel.onNext() },
                    onSkip = { viewModel.onSkip() },
                )
            }
        }

        OnboardingQuickPairStep.PinnedSwipeToRight -> {
            quickPairRect?.let {
                SpotlightTooltip(
                    targetRect = it,
                    titleText = stringResource(CoreRString.onboarding_quick_pair_3_title),
                    descText = stringResource(CoreRString.onboarding_quick_pair_3_desc),
                    buttonText = stringResource(CoreRString.next),
                    position = TooltipPosition.Below,
                    targetPadding = 24.dp,
                    onClick = { viewModel.onNext() },
                    onSkip = { viewModel.onSkip() },
                )
            }
        }

        OnboardingQuickPairStep.PairMenu -> {
            quickPairRect?.let {
                SpotlightTooltip(
                    targetRect = it,
                    titleText = stringResource(CoreRString.onboarding_quick_pair_4_title),
                    descText = stringResource(CoreRString.onboarding_quick_pair_4_desc),
                    buttonText = stringResource(CoreRString.finish),
                    position = TooltipPosition.Below,
                    targetPadding = 24.dp,
                    onClick = { viewModel.onNext() },
                )
            }
        }
    }
}

@Composable
private fun BoxScope.DismissBackground(
    swipeToDelete: Boolean,
    isPinned: Boolean,
) {
    val color =
        if (swipeToDelete) {
            ArkColor.UtilityError500
        } else {
            if (isPinned)
                ArkColor.FGQuarterary
            else
                ArkColor.Secondary
        }

    Row(
        modifier =
            Modifier
                .matchParentSize()
                .background(color),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        if (swipeToDelete.not()) {
            if (isPinned.not()) {
                Row {
                    Icon(
                        modifier = Modifier.padding(start = 17.dp),
                        painter = painterResource(id = R.drawable.ic_pin),
                        contentDescription = null,
                        tint = Color.White,
                    )
                    Text(
                        modifier = Modifier.padding(start = 4.dp),
                        text = stringResource(R.string.pin),
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                    )
                }
            } else {
                Text(
                    modifier = Modifier.padding(start = 17.dp),
                    text = stringResource(R.string.unpin),
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                )
            }
        }
        Spacer(modifier = Modifier)
        if (swipeToDelete) {
            Row {
                Text(
                    modifier = Modifier.padding(end = 4.dp),
                    text = stringResource(R.string.delete),
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                )
                Icon(
                    modifier = Modifier.padding(end = 17.dp),
                    painter = painterResource(id = R.drawable.ic_delete),
                    contentDescription = null,
                    tint = Color.White,
                )
            }
        }
    }
}
