package com.misgastos.app.presentation.screens.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.misgastos.app.presentation.components.AppDuration
import com.misgastos.app.presentation.components.AppEasing
import com.misgastos.app.presentation.components.AppEmptyState
import com.misgastos.app.presentation.components.AppOriginalLogo
import com.misgastos.app.presentation.components.AppSectionHeader
import com.misgastos.app.presentation.components.BudgetProgressCard
import com.misgastos.app.presentation.components.CategoryHighlightCard
import com.misgastos.app.presentation.components.ExpenseCard
import com.misgastos.app.presentation.components.FintechHeroCard
import com.misgastos.app.presentation.components.pressScale
import com.misgastos.app.presentation.theme.Spacing
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun HomeScreen(
    onRegisterExpense: () -> Unit,
    onExpenseClick: (Long) -> Unit,
    onNavigateToExpenses: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentMonth = LocalDate.now().month.getDisplayName(TextStyle.FULL, Locale("es"))
        .replaceFirstChar { it.uppercase() }

    var showNotificationsDialog by remember { mutableStateOf(false) }

    if (showNotificationsDialog) {
        AlertDialog(
            onDismissRequest = { showNotificationsDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0D9488).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = null,
                            tint = Color(0xFF0D9488),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        text = "Avisos y Recordatorios",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Item 1: Recordatorio Diario
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Notifications,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Recordatorio Diario",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = "Configura alertas automáticas para no olvidar registrar tus gastos del día a día.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Item 2: Estado del Presupuesto
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF6366F1).copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Analytics,
                                    contentDescription = null,
                                    tint = Color(0xFF6366F1),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Presupuesto del Mes",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = if (uiState.budgetAmount != null && uiState.budgetAmount!! > 0)
                                        "Has gastado ${uiState.currency} ${String.format("%,.2f", uiState.monthlyTotal)} de tu presupuesto asignado (${String.format("%,.2f", uiState.budgetAmount!!)})."
                                    else
                                        "No tienes un presupuesto configurado aún. Puedes asignarlo en Ajustes.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showNotificationsDialog = false
                        onNavigateToSettings()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Configurar en Ajustes", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showNotificationsDialog = false }) {
                    Text("Cerrar")
                }
            }
        )
    }

    Scaffold { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = Spacing.lg, vertical = Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg)
        ) {
            // ── Encabezado Principal Estilo Fintech ──────────────────────────
            item(key = "header") {
                val currentHour = remember { LocalTime.now().hour }
                val greeting = when (currentHour) {
                    in 5..11 -> "Buenos días"
                    in 12..18 -> "Buenas tardes"
                    else -> "Buenas noches"
                }

                val formattedDate = remember {
                    val date = LocalDate.now()
                    val dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("es"))
                        .replaceFirstChar { it.uppercase() }
                    val dayOfMonth = date.dayOfMonth
                    val month = date.month.getDisplayName(TextStyle.FULL, Locale("es"))
                    "$dayOfWeek, $dayOfMonth de $month"
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = Spacing.xs),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Columna izquierda: Logo + Textos con weight para no empujar a los botones
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AppOriginalLogo(size = 42.dp)
                        Column {
                            Text(
                                text = "Mis Gastos",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = (-0.5).sp
                                ),
                                color = MaterialTheme.colorScheme.onBackground,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "$greeting · $formattedDate",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Botones circulares superiores (Buscar y Notificaciones con Badge bien separados)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Botón Buscar
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                                    shape = CircleShape
                                )
                                .background(MaterialTheme.colorScheme.surface)
                                .pressScale(onClick = onNavigateToExpenses),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = "Buscar gastos",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Botón Notificaciones con Punto Rojo
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                                    shape = CircleShape
                                )
                                .background(MaterialTheme.colorScheme.surface)
                                .pressScale(onClick = { showNotificationsDialog = true }),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = "Notificaciones",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(20.dp)
                            )

                            // Punto rojo indicador
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(top = 4.dp, end = 4.dp)
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEF4444))
                                    .border(1.5.dp, MaterialTheme.colorScheme.surface, CircleShape)
                            )
                        }
                    }
                }
            }

            // ── Total del mes — Tarjeta Fintech con Botón '+' Integrado ─────
            item(key = "hero_balance") {
                FintechHeroCard(
                    total = uiState.monthlyTotal,
                    count = uiState.monthlyCount,
                    currency = uiState.currency,
                    monthName = currentMonth,
                    onRegisterExpense = onRegisterExpense,
                    onViewExpenses = onNavigateToExpenses
                )
            }

            // ── Presupuesto mensual ─────────────────────────────────────────
            if (uiState.budgetAmount != null && uiState.budgetAmount!! > 0) {
                item(key = "budget") {
                    BudgetProgressCard(
                        budgetAmount = uiState.budgetAmount!!,
                        spentAmount = uiState.monthlyTotal,
                        currency = uiState.currency
                    )
                }
            }

            // ── Carrusel Horizontal de Categorías Destacadas ────────────────
            if (uiState.categoryHighlights.isNotEmpty()) {
                item(key = "category_highlights_header") {
                    Text(
                        text = "Categorías activas",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(vertical = Spacing.xs)
                    )
                }

                item(key = "category_highlights_row") {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(horizontal = 2.dp)
                    ) {
                        itemsIndexed(
                            items = uiState.categoryHighlights,
                            key = { _, cat -> "cat_${cat.name}" }
                        ) { index, category ->
                            CategoryHighlightCard(
                                categoryName = category.name,
                                icon = category.icon,
                                amount = category.totalAmount,
                                count = category.count,
                                currency = uiState.currency,
                                isPrimary = index == 0,
                                onClick = onNavigateToExpenses
                            )
                        }
                    }
                }
            }

            // ── Gastos de hoy header con acción 'Ver todo' ──────────────────
            item(key = "today_header") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Gastos de hoy",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    TextButton(onClick = onNavigateToExpenses) {
                        Text(
                            text = "Ver todo",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // ── Gastos de hoy — tarjetas estilo Fintech / Apple ─────────────
            if (uiState.todayExpenses.isNotEmpty()) {
                itemsIndexed(
                    items = uiState.todayExpenses,
                    key = { _, expense -> "today_${expense.id}" }
                ) { index, expense ->
                    var visible by remember { mutableStateOf(false) }
                    LaunchedEffect(expense.id) { visible = true }

                    val alpha by animateFloatAsState(
                        targetValue = if (visible) 1f else 0f,
                        animationSpec = tween(
                            durationMillis = AppDuration.TRANSITION,
                            delayMillis = index * AppDuration.STAGGER_DELAY,
                            easing = AppEasing.EaseOut
                        ),
                        label = "item_alpha_today_$index"
                    )
                    val translationY by animateFloatAsState(
                        targetValue = if (visible) 0f else 16f,
                        animationSpec = tween(
                            durationMillis = AppDuration.TRANSITION,
                            delayMillis = index * AppDuration.STAGGER_DELAY,
                            easing = AppEasing.EaseOut
                        ),
                        label = "item_translate_today_$index"
                    )

                    ExpenseCard(
                        expense = expense,
                        currency = uiState.currency,
                        onClick = { onExpenseClick(expense.id) },
                        modifier = Modifier.graphicsLayer {
                            this.alpha = alpha
                            this.translationY = translationY
                        }
                    )
                }
            } else if (!uiState.isLoading) {
                item(key = "empty_today") {
                    AppEmptyState(
                        icon = Icons.Outlined.ReceiptLong,
                        title = "Sin gastos hoy",
                        subtitle = "No has registrado gastos el día de hoy",
                        actionLabel = "Registrar gasto de hoy",
                        onAction = onRegisterExpense
                    )
                }
            }

            // ── Gastos de ayer header ────────────────────────────────────────
            item(key = "yesterday_header") {
                Text(
                    text = "Gastos de ayer",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = Spacing.xs)
                )
            }

            // ── Gastos de ayer — tarjetas estilo Fintech / Apple ─────────────
            if (uiState.yesterdayExpenses.isNotEmpty()) {
                itemsIndexed(
                    items = uiState.yesterdayExpenses,
                    key = { _, expense -> "yesterday_${expense.id}" }
                ) { index, expense ->
                    var visible by remember { mutableStateOf(false) }
                    LaunchedEffect(expense.id) { visible = true }

                    val alpha by animateFloatAsState(
                        targetValue = if (visible) 1f else 0f,
                        animationSpec = tween(
                            durationMillis = AppDuration.TRANSITION,
                            delayMillis = index * AppDuration.STAGGER_DELAY,
                            easing = AppEasing.EaseOut
                        ),
                        label = "item_alpha_yest_$index"
                    )
                    val translationY by animateFloatAsState(
                        targetValue = if (visible) 0f else 16f,
                        animationSpec = tween(
                            durationMillis = AppDuration.TRANSITION,
                            delayMillis = index * AppDuration.STAGGER_DELAY,
                            easing = AppEasing.EaseOut
                        ),
                        label = "item_translate_yest_$index"
                    )

                    ExpenseCard(
                        expense = expense,
                        currency = uiState.currency,
                        onClick = { onExpenseClick(expense.id) },
                        modifier = Modifier.graphicsLayer {
                            this.alpha = alpha
                            this.translationY = translationY
                        }
                    )
                }
            } else if (!uiState.isLoading) {
                item(key = "empty_yesterday") {
                    AppEmptyState(
                        icon = Icons.Outlined.History,
                        title = "Sin gastos ayer",
                        subtitle = "No se registraron gastos en el día de ayer"
                    )
                }
            }

            // ── Bottom padding ──────────────────────────────────────────────
            item(key = "spacer") { Spacer(modifier = Modifier.height(Spacing.xl)) }
        }
    }
}
