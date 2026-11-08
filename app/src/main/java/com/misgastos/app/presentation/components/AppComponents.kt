package com.misgastos.app.presentation.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Flight
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Laptop
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material.icons.outlined.Subscriptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import com.misgastos.app.R
import com.misgastos.app.presentation.theme.AuroraBorderBrush
import com.misgastos.app.presentation.theme.AuroraCardBrush
import com.misgastos.app.presentation.theme.AuroraCyan
import com.misgastos.app.presentation.theme.Spacing

/**
 * Logotipo oficial de Mis Gastos:
 * Muestra el ícono original con fondo redondeado (squircle) y el arte de billetera, moneda y gráfica.
 */
@Composable
fun AppOriginalLogo(
    modifier: Modifier = Modifier,
    size: Dp = 108.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(size * 0.28f)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "Mis Gastos Logo",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
    }
}

/**
 * Tarjeta Hero de Finanzas con Estética Fintech / Apple.
 * - Balance principal en tipografía destacada
 * - Botón circular '+' integrado a la derecha para registro rápido e instantáneo
 * - Fondo con sutil gradiente moderno adaptado a tema Claro y Oscuro
 */
@Composable
fun FintechHeroCard(
    total: Double,
    count: Int,
    currency: String,
    monthName: String,
    onRegisterExpense: () -> Unit,
    onViewExpenses: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.surface.let {
        // Determinamos si el fondo es oscuro por la luminancia
        (it.red * 0.299 + it.green * 0.587 + it.blue * 0.114) < 0.5
    }

    val cardBrush = if (isDark) {
        Brush.linearGradient(
            colors = listOf(
                Color(0xFF132F38),
                Color(0xFF1E1B38),
                Color(0xFF111E2E)
            )
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                Color(0xFFE8F5F5),
                Color(0xFFEDE9FE),
                Color(0xFFE0F2FE)
            )
        )
    }

    val textColor = if (isDark) Color.White else Color(0xFF0F172A)
    val subtextColor = if (isDark) Color.White.copy(alpha = 0.7f) else Color(0xFF64748B)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
            .border(
                width = 1.dp,
                color = if (isDark) Color.White.copy(alpha = 0.12f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                shape = RoundedCornerShape(26.dp)
            ),
        shape = RoundedCornerShape(26.dp),
        color = Color.Transparent,
        tonalElevation = 2.dp,
        shadowElevation = if (isDark) 4.dp else 2.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(cardBrush)
                .padding(horizontal = 22.dp, vertical = 22.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Columna izquierda: Balance
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Total del mes",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium,
                            color = subtextColor
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isDark) Color.White.copy(alpha = 0.12f) else Color.Black.copy(alpha = 0.06f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = monthName,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isDark) AuroraCyan else MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "$currency ${String.format("%,.2f", total)}",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.5).sp
                        ),
                        color = textColor,
                        modifier = Modifier.animateContentSize()
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "$count ${if (count == 1) "gasto registrado este mes" else "gastos registrados este mes"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = subtextColor
                    )
                }
            }
        }
    }
}

/**
 * Mapeo inteligente de categorías a Emojis oficiales de Mis Gastos.
 */
fun getCategoryEmoji(categoryName: String, fallback: String = ""): String {
    if (fallback.isNotBlank() && fallback != "📦" && fallback.length <= 4) {
        return fallback
    }
    return when (categoryName.trim().lowercase()) {
        "comida", "alimentación", "restaurante", "almuerzo", "cena", "desayuno" -> "🍔"
        "transporte", "gasolina", "uber", "taxi", "bus" -> "🚗"
        "supermercado", "compras", "mercado", "super" -> "🛒"
        "entretenimiento", "cine", "diversión", "ocio" -> "🎬"
        "educación", "estudios", "cursos", "libros" -> "📚"
        "salud", "médico", "farmacia", "medicina", "doctor" -> "🏥"
        "hogar", "casa", "alquiler", "mantenimiento" -> "🏠"
        "ropa", "vestimenta", "calzado" -> "👕"
        "videojuegos", "juegos", "gaming" -> "🎮"
        "tecnología", "computadora", "gadgets" -> "💻"
        "suscripciones", "streaming", "spotify", "netflix" -> "📱"
        "servicios", "luz", "agua", "internet", "teléfono" -> "⚡"
        "ahorro", "inversión" -> "💰"
        "viajes", "vuelo", "hotel" -> "✈️"
        else -> fallback.ifBlank { "📦" }
    }
}

/**
 * Mapeo inteligente de categorías a iconos vectoriales profesionales.
 */
fun getCategoryVectorIcon(categoryName: String): ImageVector {
    return when (categoryName.trim().lowercase()) {
        "comida", "alimentación", "restaurante", "almuerzo", "cena", "desayuno" -> Icons.Outlined.Restaurant
        "transporte", "gasolina", "uber", "taxi", "bus" -> Icons.Outlined.DirectionsCar
        "supermercado", "compras", "mercado", "super" -> Icons.Outlined.ShoppingCart
        "entretenimiento", "cine", "diversión", "ocio" -> Icons.Outlined.Movie
        "educación", "estudios", "cursos", "libros" -> Icons.Outlined.School
        "salud", "médico", "farmacia", "medicina", "doctor" -> Icons.Outlined.LocalHospital
        "hogar", "casa", "alquiler", "mantenimiento" -> Icons.Outlined.Home
        "ropa", "vestimenta", "calzado" -> Icons.Outlined.Checkroom
        "videojuegos", "juegos", "gaming" -> Icons.Outlined.SportsEsports
        "tecnología", "computadora", "gadgets" -> Icons.Outlined.Laptop
        "suscripciones", "streaming", "spotify", "netflix" -> Icons.Outlined.Subscriptions
        "servicios", "luz", "agua", "internet", "teléfono" -> Icons.Outlined.Bolt
        "ahorro", "inversión" -> Icons.Outlined.Savings
        "viajes", "vuelo", "hotel" -> Icons.Outlined.Flight
        else -> Icons.Outlined.Category
    }
}

/**
 * Tarjeta individual de Categoría destacada para carrusel horizontal con Emoji.
 */
@Composable
fun CategoryHighlightCard(
    categoryName: String,
    icon: String = "",
    amount: Double,
    count: Int,
    currency: String,
    isPrimary: Boolean = false,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.surface.let {
        (it.red * 0.299 + it.green * 0.587 + it.blue * 0.114) < 0.5
    }

    val backgroundBrush = if (isPrimary) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF0D9488),
                Color(0xFF0F766E),
                Color(0xFF115E59)
            )
        )
    } else null

    val cardBgColor = if (isPrimary) {
        Color.Transparent
    } else if (isDark) {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    } else {
        MaterialTheme.colorScheme.surface
    }

    val contentColor = if (isPrimary) Color.White else MaterialTheme.colorScheme.onSurface
    val subColor = if (isPrimary) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant

    Surface(
        modifier = modifier
            .width(160.dp)
            .height(170.dp)
            .clip(RoundedCornerShape(22.dp))
            .border(
                width = 1.dp,
                color = if (isPrimary) Color.White.copy(alpha = 0.2f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(22.dp)
            )
            .pressScale(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        color = cardBgColor,
        shadowElevation = if (isPrimary) 6.dp else 1.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(if (backgroundBrush != null) Modifier.background(backgroundBrush) else Modifier)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header de la tarjeta: Emoji en círculo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                if (isPrimary) Color.White.copy(alpha = 0.22f)
                                else MaterialTheme.colorScheme.surfaceVariant
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = getCategoryEmoji(categoryName, icon),
                            fontSize = 22.sp
                        )
                    }

                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = null,
                        tint = subColor.copy(alpha = 0.6f),
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Info inferior
                Column {
                    Text(
                        text = categoryName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = contentColor,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$currency ${String.format("%,.2f", amount)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = contentColor
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$count ${if (count == 1) "gasto" else "gastos"}",
                        style = MaterialTheme.typography.labelSmall,
                        color = subColor
                    )
                }
            }
        }
    }
}

// ─── Section Header ─────────────────────────────────────────────────────────

@Composable
fun AppSectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier.padding(vertical = Spacing.xs)
    )
}

// ─── Empty State con Icono Vectorial ─────────────────────────────────────────

@Composable
fun AppEmptyState(
    icon: ImageVector = Icons.Outlined.ReceiptLong,
    title: String,
    subtitle: String? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(AppDuration.MODAL, easing = AppEasing.EaseOut),
        label = "empty_alpha"
    )
    val translationY by animateFloatAsState(
        targetValue = if (visible) 0f else 24f,
        animationSpec = tween(AppDuration.MODAL, easing = AppEasing.EaseOut),
        label = "empty_translate"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.xxl)
            .graphicsLayer {
                this.alpha = alpha
                this.translationY = translationY
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(30.dp)
            )
        }
        Spacer(modifier = Modifier.height(Spacing.md))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        if (subtitle != null) {
            Spacer(modifier = Modifier.height(Spacing.xs))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
        if (actionLabel != null && onAction != null) {
            Spacer(modifier = Modifier.height(Spacing.lg))
            Button(
                onClick = onAction,
                modifier = Modifier.pressScale(onClick = onAction),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(actionLabel, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ─── Error State ────────────────────────────────────────────────────────────

@Composable
fun AppErrorState(
    message: String,
    onRetry: (() -> Unit)? = null,
    icon: ImageVector = Icons.Outlined.ErrorOutline,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.xxxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.errorContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(Spacing.lg))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        if (onRetry != null) {
            Spacer(modifier = Modifier.height(Spacing.lg))
            Button(
                onClick = onRetry,
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Reintentar")
            }
        }
    }
}

// ─── Loading State ──────────────────────────────────────────────────────────

@Composable
fun AppLoadingState(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.xxl),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(24.dp),
            strokeWidth = 2.5.dp,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
