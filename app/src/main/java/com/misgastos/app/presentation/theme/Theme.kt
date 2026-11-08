package com.misgastos.app.presentation.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Teal700,
    onPrimary = Color.White,
    primaryContainer = Teal50,
    onPrimaryContainer = Color(0xFF002019),
    secondary = Amber700,
    onSecondary = Color.White,
    secondaryContainer = Amber50,
    onSecondaryContainer = Color(0xFF261A00),
    tertiary = Teal400,
    onTertiary = Color.White,
    tertiaryContainer = Teal50,
    onTertiaryContainer = Color(0xFF002019),
    error = ErrorRed,
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = Gray50,
    onBackground = Gray900,
    surface = Color.White,
    onSurface = Gray900,
    surfaceVariant = Color(0xFFF0F0F0),
    onSurfaceVariant = Gray600,
    outline = Gray400,
    outlineVariant = Color(0xFFE8E8E8),
    inverseSurface = Gray800,
    inverseOnSurface = Gray100,
    inversePrimary = Teal200
)

// Modo Oscuro Puro: fondo negro profundo (#121214), superficies oscuras y alto contraste
private val DarkColorScheme = darkColorScheme(
    primary = Teal300,
    onPrimary = Color(0xFF003731),
    primaryContainer = Color(0xFF004D44),
    onPrimaryContainer = Teal100,
    secondary = Amber400,
    onSecondary = Color(0xFF3F2E00),
    secondaryContainer = Color(0xFF5A4300),
    onSecondaryContainer = Amber200,
    tertiary = Teal200,
    onTertiary = Color(0xFF003731),
    tertiaryContainer = Teal600,
    onTertiaryContainer = Teal100,
    error = ErrorRedLight,
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = DarkBackground,
    onBackground = Color(0xFFE6E6E6),
    surface = DarkSurface,
    onSurface = Color(0xFFE6E6E6),
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Gray400,
    outline = Gray600,
    outlineVariant = Color(0xFF3A3A3C),
    inverseSurface = Gray200,
    inverseOnSurface = Gray800,
    inversePrimary = Teal700
)

// Modo Sistema en Oscuro: tonalidades Slate/Cyan oscuro (#10161A, #182026), superficies suaves diferenciadas de Oscuro puro
private val SystemDarkColorScheme = darkColorScheme(
    primary = AuroraCyan,
    onPrimary = Color(0xFF00363A),
    primaryContainer = Color(0xFF004F53),
    onPrimaryContainer = Color(0xFFE0F7FA),
    secondary = AuroraTeal,
    onSecondary = Color(0xFF003730),
    secondaryContainer = Color(0xFF004D40),
    onSecondaryContainer = Color(0xFFE0F2F1),
    tertiary = AuroraBlue,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF1A237E),
    onTertiaryContainer = Color(0xFFE8EAF6),
    error = ErrorRedLight,
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = SystemDarkBackground,
    onBackground = Color(0xFFECEFF1),
    surface = SystemDarkSurface,
    onSurface = Color(0xFFECEFF1),
    surfaceVariant = SystemDarkSurfaceVariant,
    onSurfaceVariant = Color(0xFF90A4AE),
    outline = Color(0xFF546E7A),
    outlineVariant = Color(0xFF2E3D48),
    inverseSurface = Color(0xFFCFD8DC),
    inverseOnSurface = Color(0xFF263238),
    inversePrimary = Teal300
)

private val MisGastosShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}

@Composable
fun MisGastosTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val isSystemDark = isSystemInDarkTheme()

    val (colorScheme, isDarkForBars) = when (themeMode) {
        ThemeMode.LIGHT -> LightColorScheme to false
        ThemeMode.DARK -> DarkColorScheme to true
        ThemeMode.SYSTEM -> {
            if (isSystemDark) SystemDarkColorScheme to true
            else LightColorScheme to false
        }
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !isDarkForBars
                isAppearanceLightNavigationBars = !isDarkForBars
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MisGastosTypography,
        shapes = MisGastosShapes,
        content = content
    )
}
