package dev.arkbuilders.rate.feature.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.arkbuilders.rate.core.presentation.R
import dev.arkbuilders.rate.core.presentation.theme.ArkColor
import dev.arkbuilders.rate.core.presentation.ui.AppButton
import kotlin.math.roundToInt

@Composable
fun Spotlight(
    targetRect: Rect,
    onTargetClicked: () -> Unit,
    onDismiss: () -> Unit,
    shape: SpotlightShape = SpotlightShape.Rect,
    padding: Dp = 0.dp,
) {
    val density = LocalDensity.current
    val statusBarHeightPx = WindowInsets.systemBars.getTop(density).toFloat()
    val paddingPx = with(density) { padding.toPx() }

    var showSpotlight by remember { mutableStateOf(true) }

    AnimatedVisibility(
        visible = showSpotlight,
        exit = fadeOut(tween(300))
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        if (targetRect.contains(offset)) {
                            onTargetClicked()
                        } else {
                            showSpotlight = false
                            onDismiss()
                        }
                    }
                }
        ) {

            val spotlightRect = when (shape) {
                SpotlightShape.Circle -> {
                    val size = maxOf(targetRect.width, targetRect.height) + paddingPx * 2
                    val center = targetRect.center
                    Rect(
                        center.x - size / 2,
                        center.y - size / 2,
                        center.x + size / 2,
                        center.y + size / 2
                    )
                }

                SpotlightShape.Rect -> {
                    Rect(
                        targetRect.left - paddingPx,
                        targetRect.top - paddingPx,
                        targetRect.right + paddingPx,
                        targetRect.bottom + paddingPx
                    )
                }
            }

            // handle edge to edge
            val adjustedRect = spotlightRect.translate(Offset(0f, -statusBarHeightPx))

            val spotlightPath = Path().apply {
                when (shape) {
                    SpotlightShape.Circle -> addOval(adjustedRect)
                    SpotlightShape.Rect -> addRect(adjustedRect)
                }
            }

            clipPath(
                path = spotlightPath,
                clipOp = ClipOp.Difference
            ) {
                drawRect(Color.Black.copy(alpha = 0.8f))
            }
        }
    }
}

sealed class SpotlightShape {
    object Circle : SpotlightShape()
    object Rect : SpotlightShape()
}

enum class TooltipPosition { Above, Below }

@Composable
fun SpotlightTooltip(
    targetRect: Rect,
    text: String,
    buttonText: String,
    onClick: () -> Unit,
    position: TooltipPosition,
    targetPadding: Dp = 12.dp,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val statusBarHeightPx = WindowInsets.systemBars.getTop(density).toFloat()
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val screenHeightPx = with(density) { screenHeight.toPx() }
    val paddingPx = with(density) { targetPadding.toPx() }


    var tooltipHeightPx by remember { mutableStateOf(0f) }

    val tooltipOffsetY = run {
        val aboveY = targetRect.top - tooltipHeightPx - paddingPx - statusBarHeightPx
        val belowY = targetRect.bottom + paddingPx - statusBarHeightPx

        when (position) {
            TooltipPosition.Above -> if (aboveY > 0) aboveY else belowY
            TooltipPosition.Below -> if (belowY + tooltipHeightPx < screenHeightPx) belowY else aboveY
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .absoluteOffset { IntOffset(0, tooltipOffsetY.roundToInt()) }
            .padding(horizontal = 24.dp)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            modifier = Modifier.onGloballyPositioned {
                tooltipHeightPx = it.size.height.toFloat()
            }
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = text, style = MaterialTheme.typography.bodyMedium)
                Button(
                    onClick = onClick,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(buttonText)
                }
            }
        }
    }
}

@Composable
fun DialogTmp() {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp)
    ) {
        Spacer(Modifier.height(20.dp))
        Text(
            text = "",
            fontWeight = FontWeight.W600,
            fontSize = 16.sp,
            color = ArkColor.TextPrimary
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "",
            color = ArkColor.TextTertiary
        )
        Spacer(Modifier.height(24.dp))
        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = {  },
            border =
            BorderStroke(
                width = 1.dp,
                color = ArkColor.BorderSecondary,
            ),
            colors = ButtonDefaults.outlinedButtonColors(),
            shape = RoundedCornerShape(8.dp),
        ) {
            Text(
                text = "",
                fontWeight = FontWeight.W600,
                fontSize = 16.sp,
                color = ArkColor.TextSecondary,
            )
        }
        Spacer(Modifier.height(16.dp))
    }
}
