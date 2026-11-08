package com.misgastos.app.presentation.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.misgastos.app.MisGastosApplication
import com.misgastos.app.presentation.components.AppDuration
import com.misgastos.app.presentation.components.AppEasing
import com.misgastos.app.presentation.components.AppOriginalLogo
import com.misgastos.app.presentation.theme.Spacing
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    onSplashFinished: (isInitialCurrencySet: Boolean) -> Unit
) {
    val context = LocalContext.current
    val preferencesManager = remember {
        (context.applicationContext as MisGastosApplication).container.preferencesManager
    }

    val contentAlpha = remember { Animatable(0f) }
    val contentScale = remember { Animatable(0.96f) }
    val contentOffset = remember { Animatable(10f) }

    LaunchedEffect(Unit) {
        val isConfigured = preferencesManager.initialCurrencySet.first()

        launch {
            contentAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(AppDuration.MODAL, easing = AppEasing.EaseOut)
            )
        }
        launch {
            contentScale.animateTo(
                targetValue = 1f,
                animationSpec = tween(AppDuration.MODAL, easing = AppEasing.EaseOut)
            )
        }
        launch {
            contentOffset.animateTo(
                targetValue = 0f,
                animationSpec = tween(AppDuration.MODAL, easing = AppEasing.EaseOut)
            )
        }

        delay(620)

        contentAlpha.animateTo(
            targetValue = 0f,
            animationSpec = tween(AppDuration.TRANSITION, easing = AppEasing.EaseOut)
        )

        onSplashFinished(isConfigured)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.graphicsLayer {
                alpha = contentAlpha.value
                scaleX = contentScale.value
                scaleY = contentScale.value
                translationY = contentOffset.value
            }
        ) {
            // 1. Logo oficial de la aplicación (Elemento Principal: 128dp, completo y sin recortes)
            AppOriginalLogo(size = 128.dp)

            // Espaciado controlado entre Logo y Título (16dp - 20dp)
            Spacer(modifier = Modifier.height(Spacing.lg + 2.dp))

            // 2. Nombre de la aplicación "Mis Gastos" en una sola línea, sin recortes ni wrap
            Text(
                text = "Mis Gastos",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.2).sp
                ),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                maxLines = 1,
                softWrap = false,
                modifier = Modifier.wrapContentWidth()
            )

            // Espaciado controlado entre Título y Subtítulo (4dp - 8dp)
            Spacer(modifier = Modifier.height(Spacing.xs + 2.dp))

            Text(
                text = "Tu dinero, en orden.",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.15.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 1,
                softWrap = false,
                modifier = Modifier.wrapContentWidth()
            )
        }
    }
}
