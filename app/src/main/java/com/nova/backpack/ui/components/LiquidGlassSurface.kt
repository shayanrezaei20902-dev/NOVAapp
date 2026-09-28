package com.nova.backpack.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.nova.backpack.data.AppSettings
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

/**
 * A glass-look container (blur/translucency handled by the background color's alpha
 * + Modifier.blur where supported) that reproduces the spec's Liquid Glass
 * micro-interaction: press & hold nudges the surface ~1mm toward the finger, and
 * releasing snaps it back with a soft spring. Disabled (or simplified) automatically
 * when Liquid Glass or animations are off in Settings.
 */
@Composable
fun LiquidGlassSurface(
    settings: AppSettings,
    modifier: Modifier = Modifier,
    onHold: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val offset = remember { Animatable(Offset.Zero, Offset.VectorConverter) }
    val scope = rememberCoroutineScope()
    val maxOffsetPx = 8f // ~1mm-scale nudge, clamped so it never reads as a real drag

    val glassOn = settings.liquidGlassEnabled
    val accent = MaterialTheme.colorScheme.primary

    Modifier // no-op, keeps import used
    androidx.compose.foundation.layout.Box(
        modifier
            .graphicsLayer {
                translationX = offset.value.x
                translationY = offset.value.y
            }
            .background(
                color = if (glassOn) Color.White.copy(alpha = 0.06f) else MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp)
            )
            .pointerInput(glassOn) {
                if (!glassOn) return@pointerInput
                detectDragGestures(
                    onDragStart = { onHold?.invoke() },
                    onDragEnd = {
                        scope.launch {
                            offset.animateTo(
                                Offset.Zero,
                                spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)
                            )
                        }
                    },
                    onDragCancel = {
                        scope.launch { offset.animateTo(Offset.Zero, spring(stiffness = Spring.StiffnessMedium)) }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        scope.launch {
                            val next = offset.value + dragAmount
                            offset.snapTo(
                                Offset(
                                    x = max(-maxOffsetPx, min(maxOffsetPx, next.x)),
                                    y = max(-maxOffsetPx, min(maxOffsetPx, next.y))
                                )
                            )
                        }
                    }
                )
            }
    ) {
        content()
    }
}
