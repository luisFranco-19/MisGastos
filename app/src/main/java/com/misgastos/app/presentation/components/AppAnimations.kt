package com.misgastos.app.presentation.components

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput

/**
 * Centralized animation constants following Emil Kowalski's design philosophy.
 *
 * Key principles:
 * - Use ease-out for entries (starts fast, feels responsive)
 * - UI animations stay under 300ms
 * - Custom easing curves with more "punch" than defaults
 * - Never animate from scale(0) — start from 0.95f+
 * - Stagger delays: 30-50ms between items
 */
object AppEasing {
    /** Strong ease-out for UI interactions — starts fast, decelerates smoothly */
    val EaseOut = CubicBezierEasing(0.23f, 1f, 0.32f, 1f)

    /** Strong ease-in-out for on-screen movement */
    val EaseInOut = CubicBezierEasing(0.77f, 0f, 0.175f, 1f)

    /** Standard ease for hover/color transitions */
    val Ease = CubicBezierEasing(0.25f, 0.1f, 0.25f, 1f)
}

object AppDuration {
    /** Button press feedback */
    const val PRESS = 120
    /** Tooltips, small popovers, chip selection */
    const val QUICK = 150
    /** Dropdowns, selects, step transitions */
    const val TRANSITION = 200
    /** Modals, drawers, confirmation screens */
    const val MODAL = 250
    /** Chart animations, progress bars */
    const val CHART = 600
    /** Stagger delay between list items */
    const val STAGGER_DELAY = 40
}

/**
 * Modifier that adds a press-scale feedback effect to any composable.
 * On press: scales to 0.97f over 120ms with EaseOut.
 * On release: returns to 1f.
 *
 * Applies Emil Kowalski's principle: "Buttons must feel responsive."
 * Every pressable element should have subtle scale feedback.
 */
fun Modifier.pressScale(
    targetScale: Float = 0.97f,
    onClick: () -> Unit = {}
): Modifier = composed {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) targetScale else 1f,
        animationSpec = tween(
            durationMillis = AppDuration.PRESS,
            easing = AppEasing.EaseOut
        ),
        label = "press_scale"
    )

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .pointerInput(onClick) {
            detectTapGestures(
                onPress = {
                    isPressed = true
                    try {
                        awaitRelease()
                    } finally {
                        isPressed = false
                    }
                    onClick()
                }
            )
        }
}
