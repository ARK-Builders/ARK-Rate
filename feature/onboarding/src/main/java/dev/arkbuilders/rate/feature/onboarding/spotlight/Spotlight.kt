package dev.arkbuilders.rate.feature.onboarding.spotlight

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

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
        exit = fadeOut(tween(300)),
    ) {
        Canvas(
            modifier =
                Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            if (targetRect.contains(offset)) {
                                onTargetClicked()
                            } else {
                                onDismiss()
                            }
                        }
                    },
        ) {
            val spotlightRect =
                when (shape) {
                    SpotlightShape.Circle -> {
                        val size = maxOf(targetRect.width, targetRect.height) + paddingPx * 2
                        val center = targetRect.center
                        Rect(
                            center.x - size / 2,
                            center.y - size / 2,
                            center.x + size / 2,
                            center.y + size / 2,
                        )
                    }

                    SpotlightShape.Rect -> {
                        Rect(
                            targetRect.left - paddingPx,
                            targetRect.top - paddingPx,
                            targetRect.right + paddingPx,
                            targetRect.bottom + paddingPx,
                        )
                    }
                }

            // handle edge to edge
            val adjustedRect = spotlightRect.translate(Offset(0f, -statusBarHeightPx))

            val spotlightPath =
                Path().apply {
                    when (shape) {
                        SpotlightShape.Circle -> addOval(adjustedRect)
                        SpotlightShape.Rect -> addRect(adjustedRect)
                    }
                }

            clipPath(
                path = spotlightPath,
                clipOp = ClipOp.Difference,
            ) {
                drawRect(Color.Black.copy(alpha = 0.8f))
            }
        }
    }
}

sealed class SpotlightShape {
    data object Circle : SpotlightShape()

    data object Rect : SpotlightShape()
}
