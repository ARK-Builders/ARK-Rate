package dev.arkbuilders.rate.feature.onboarding.quick

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
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.arkbuilders.rate.core.domain.model.Amount
import dev.arkbuilders.rate.core.domain.model.CurrencyName
import dev.arkbuilders.rate.core.presentation.CoreRString
import dev.arkbuilders.rate.core.presentation.R
import dev.arkbuilders.rate.core.presentation.theme.ArkColor
import dev.arkbuilders.rate.core.presentation.ui.CurrencyInfoItem
import dev.arkbuilders.rate.core.presentation.ui.ListHeader
import dev.arkbuilders.rate.core.presentation.ui.SearchTextField
import java.math.BigDecimal

@Composable
fun OnboardingQuickPairScreen(
    modifier: Modifier,
    pairModifier: Modifier,
    stepIndex: Int,
    currencies: List<CurrencyName>,
) {
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

    val step = OnboardingQuickStep.entries.get(stepIndex)

    val translatePairModifier =
        when (step) {
            OnboardingQuickStep.PairSwipeToRight ->
                Modifier.graphicsLayer { translationX = quickPairTranslatePx }
            OnboardingQuickStep.PairSwipeToLeft ->
                Modifier.graphicsLayer { translationX = -quickPairTranslatePx }
            OnboardingQuickStep.PinnedSwipeToRight ->
                Modifier.graphicsLayer {
                    translationX = quickPairTranslatePx
                }
            else -> Modifier
        }

    Scaffold(modifier = modifier) {
        Column(modifier = Modifier.padding(it)) {
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
            Column(modifier = pairModifier) {
                if (stepIndex <= 4) {
                    ListHeader(text = stringResource(CoreRString.quick_calculations))
                } else {
                    ListHeader(text = stringResource(CoreRString.quick_pinned_calculations))
                }
                Box(propagateMinConstraints = true) {
                    DismissBackground(
                        swipeToDelete =
                            step == OnboardingQuickStep.PairSwipeToLeft,
                        isPinned =
                            step == OnboardingQuickStep.PinnedSwipeToRight,
                    )
                    MockQuickItem(
                        modifier = translatePairModifier,
                        from = Amount("EUR", BigDecimal.valueOf(1.0)),
                        to = listOf(Amount("USD", BigDecimal.valueOf(1.09076))),
                        dateText = "Last refreshed 1 minutes ago",
                    ) { }
                }
            }
            ListHeader(text = stringResource(CoreRString.all_currencies))
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(currencies, key = { it.code }) { name ->
                    CurrencyInfoItem(name) { }
                }
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
