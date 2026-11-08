package com.misgastos.app.presentation.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// ─── Primary — Teal (refined, deeper tones) ─────────────────────────────────

val Teal700 = Color(0xFF00695C)
val Teal600 = Color(0xFF00796B)
val Teal500 = Color(0xFF00897B)
val Teal400 = Color(0xFF26A69A)
val Teal300 = Color(0xFF4DB6AC)
val Teal200 = Color(0xFF80CBC4)
val Teal100 = Color(0xFFB2DFDB)
val Teal50 = Color(0xFFE0F2F1)

// ─── Secondary — Warm Amber/Gold ─────────────────────────────────────────────

val Amber700 = Color(0xFFFF8F00)
val Amber600 = Color(0xFFFFB300)
val Amber400 = Color(0xFFFFCA28)
val Amber200 = Color(0xFFFFE082)
val Amber50 = Color(0xFFFFF8E1)

// ─── Aurora / Neon / RGB Gradient Colors ─────────────────────────────────────

val AuroraCyan = Color(0xFF00E5FF)
val AuroraTeal = Color(0xFF00BFA5)
val AuroraBlue = Color(0xFF2979FF)
val AuroraPurple = Color(0xFF7C4DFF)
val AuroraMagenta = Color(0xFFE040FB)

val AuroraCardBrush = Brush.linearGradient(
    colors = listOf(
        Color(0xFF0B1E2D),
        Color(0xFF161233),
        Color(0xFF25102D)
    )
)

val AuroraBorderBrush = Brush.linearGradient(
    colors = listOf(
        AuroraCyan.copy(alpha = 0.6f),
        AuroraPurple.copy(alpha = 0.5f),
        AuroraMagenta.copy(alpha = 0.4f)
    )
)

val AuroraTextBrush = Brush.linearGradient(
    colors = listOf(
        Color(0xFFFFFFFF),
        Color(0xFFE0F7FA),
        AuroraCyan
    )
)

// ─── Neutrals — Refined gray scale ───────────────────────────────────────────

val Gray50 = Color(0xFFFAFAFA)
val Gray100 = Color(0xFFF5F5F5)
val Gray200 = Color(0xFFEEEEEE)
val Gray300 = Color(0xFFE0E0E0)
val Gray400 = Color(0xFFBDBDBD)
val Gray500 = Color(0xFF9E9E9E)
val Gray600 = Color(0xFF757575)
val Gray700 = Color(0xFF616161)
val Gray800 = Color(0xFF424242)
val Gray900 = Color(0xFF212121)

// ─── Semantic ────────────────────────────────────────────────────────────────

val ErrorRed = Color(0xFFD32F2F)
val ErrorRedLight = Color(0xFFEF5350)
val SuccessGreen = Color(0xFF2E7D32)
val SuccessGreenLight = Color(0xFF66BB6A)
val WarningOrange = Color(0xFFE65100)
val WarningOrangeLight = Color(0xFFFFA726)
val InfoBlue = Color(0xFF1565C0)

// ─── Dark Theme Surfaces (Pure Deep Dark) ───────────────────────────────────

val DarkBackground = Color(0xFF121214)
val DarkSurface = Color(0xFF1C1C1E)
val DarkSurfaceVariant = Color(0xFF2C2C2E)
val DarkSurfaceElevated = Color(0xFF363638)

// ─── System Dark Theme Surfaces (Slate / Cyan Tinted Dark) ───────────────────

val SystemDarkBackground = Color(0xFF10161A)
val SystemDarkSurface = Color(0xFF182026)
val SystemDarkSurfaceVariant = Color(0xFF222C35)
val SystemDarkSurfaceElevated = Color(0xFF2C3843)

// ─── Spacing system (4dp grid) ──────────────────────────────────────────────

object Spacing {
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 24.dp
    val xxl = 32.dp
    val xxxl = 48.dp
}
