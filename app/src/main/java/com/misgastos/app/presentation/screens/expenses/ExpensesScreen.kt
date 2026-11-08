package com.misgastos.app.presentation.screens.expenses

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.misgastos.app.domain.model.Category
import com.misgastos.app.domain.model.Expense
import com.misgastos.app.domain.model.PaymentMethod
import com.misgastos.app.presentation.components.AppDuration
import com.misgastos.app.presentation.components.AppEasing
import com.misgastos.app.presentation.components.AppEmptyState
import com.misgastos.app.presentation.components.ExpenseCard
import com.misgastos.app.presentation.components.getCategoryVectorIcon
import com.misgastos.app.presentation.components.pressScale
import com.misgastos.app.presentation.theme.Spacing
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ExpensesScreen(
    onExpenseClick: (Long) -> Unit,
    onEditExpense: (Long) -> Unit,
    viewModel: ExpensesViewModel = viewModel(factory = ExpensesViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var showFilters by remember { mutableStateOf(false) }
    var deleteExpenseId by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(uiState.deleteSuccess) {
        if (uiState.deleteSuccess) {
            snackbarHostState.showSnackbar("Gasto eliminado correctamente")
            viewModel.resetDeleteSuccess()
        }
    }

    // Modal de confirmación para eliminar
    deleteExpenseId?.let { id ->
        AlertDialog(
            onDismissRequest = { deleteExpenseId = null },
            title = { Text("Eliminar gasto", fontWeight = FontWeight.Bold) },
            text = { Text("¿Estás seguro de que deseas eliminar esta transacción?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteExpense(id)
                    deleteExpenseId = null
                }) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteExpenseId = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    val totalFilteredAmount = remember(uiState.expenses) {
        uiState.expenses.sumOf { it.amount }
    }

    // Agrupación de gastos por fecha
    val groupedExpenses = remember(uiState.expenses) {
        uiState.expenses.groupBy { it.date }
    }

    val hasActiveFilters = uiState.selectedCategory != null || uiState.selectedPaymentMethod != null

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Historial de Gastos",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${uiState.expenses.size}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                actions = {
                    // Botón de filtros con badge si hay filtro activo
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .border(
                                width = 1.dp,
                                color = if (hasActiveFilters) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                                shape = CircleShape
                            )
                            .background(
                                if (hasActiveFilters) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                else MaterialTheme.colorScheme.surface
                            )
                            .pressScale(onClick = { showFilters = !showFilters }),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.FilterList,
                            contentDescription = "Filtrar",
                            tint = if (hasActiveFilters) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Botón de ordenación por monto
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .border(
                                width = 1.dp,
                                color = if (uiState.sortByAmount) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                                shape = CircleShape
                            )
                            .background(
                                if (uiState.sortByAmount) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                else MaterialTheme.colorScheme.surface
                            )
                            .pressScale(onClick = { viewModel.toggleSortByAmount() }),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (uiState.sortByAmount) Icons.Filled.ArrowDownward else Icons.Filled.ArrowUpward,
                            contentDescription = "Ordenar",
                            tint = if (uiState.sortByAmount) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = Spacing.lg, vertical = Spacing.sm),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            // Barra de búsqueda redondeada estilo iOS
            item(key = "search_and_summary") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = viewModel::onSearchQueryChange,
                        modifier = Modifier
                            .fillMaxWidth(),
                        placeholder = {
                            Text(
                                "Buscar por concepto o categoría...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = "Buscar",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon = {
                            if (uiState.searchQuery.isNotBlank()) {
                                IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = "Limpiar",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(18.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                            focusedBorderColor = MaterialTheme.colorScheme.primary
                        )
                    )

                    // Banner de Resumen Rápido (Fintech pill)
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp)),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Total en este filtro",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${uiState.currency} ${String.format("%,.2f", totalFilteredAmount)}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // Panel desplegable de Filtros con animación suave
            item(key = "filters_section") {
                AnimatedVisibility(
                    visible = showFilters,
                    enter = expandVertically(animationSpec = tween(AppDuration.TRANSITION, easing = AppEasing.EaseOut)) + fadeIn(),
                    exit = shrinkVertically(animationSpec = tween(AppDuration.QUICK, easing = AppEasing.EaseOut)) + fadeOut()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Filtrar por Categoría",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            item {
                                FilterChip(
                                    selected = uiState.selectedCategory == null,
                                    onClick = { viewModel.onCategoryFilter(null) },
                                    label = { Text("Todas") },
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }
                            items(Category.DEFAULT_CATEGORIES) { cat ->
                                FilterChip(
                                    selected = uiState.selectedCategory == cat.name,
                                    onClick = { viewModel.onCategoryFilter(cat.name) },
                                    label = { Text("${cat.icon} ${cat.name}") },
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }
                        }

                        Text(
                            text = "Filtrar por Método de Pago",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            item {
                                FilterChip(
                                    selected = uiState.selectedPaymentMethod == null,
                                    onClick = { viewModel.onPaymentMethodFilter(null) },
                                    label = { Text("Todos") },
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }
                            items(PaymentMethod.entries) { method ->
                                FilterChip(
                                    selected = uiState.selectedPaymentMethod == method.displayName,
                                    onClick = { viewModel.onPaymentMethodFilter(method.displayName) },
                                    label = { Text(method.displayName) },
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Lista de gastos agrupada por fecha
            if (uiState.expenses.isNotEmpty()) {
                groupedExpenses.forEach { (date, expensesForDate) ->
                    val dateHeader = formatDateHeader(date)
                    val dayTotal = expensesForDate.sumOf { it.amount }

                    // Encabezado de la fecha con subtotal del día
                    item(key = "header_${date}") {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = Spacing.sm, bottom = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = dateHeader,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "${uiState.currency} ${String.format("%,.2f", dayTotal)}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Transacciones del día
                    itemsIndexed(
                        items = expensesForDate,
                        key = { _, expense -> "exp_${expense.id}" }
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
                            label = "expense_alpha_${expense.id}"
                        )

                        ExpenseCard(
                            expense = expense,
                            currency = uiState.currency,
                            onClick = { onExpenseClick(expense.id) },
                            modifier = Modifier.graphicsLayer { this.alpha = alpha }
                        )
                    }
                }
            } else if (!uiState.isLoading) {
                item(key = "empty_state") {
                    AppEmptyState(
                        icon = Icons.Outlined.SearchOff,
                        title = "No se encontraron gastos",
                        subtitle = if (uiState.searchQuery.isNotBlank() || hasActiveFilters)
                            "Prueba cambiando o limpiando los filtros de búsqueda"
                        else
                            "Agrega tu primer gasto desde la pantalla de inicio",
                        actionLabel = if (hasActiveFilters || uiState.searchQuery.isNotBlank()) "Limpiar filtros" else null,
                        onAction = {
                            viewModel.onSearchQueryChange("")
                            viewModel.onCategoryFilter(null)
                            viewModel.onPaymentMethodFilter(null)
                        }
                    )
                }
            }

            item(key = "bottom_spacer") {
                Spacer(modifier = Modifier.height(Spacing.xxl))
            }
        }
    }
}

/**
 * Formatea el encabezado de fecha agrupada: "Hoy", "Ayer", o "Lunes, 15 de Septiembre"
 */
private fun formatDateHeader(date: LocalDate): String {
    val today = LocalDate.now()
    val daysBetween = ChronoUnit.DAYS.between(date, today)
    return when {
        daysBetween == 0L -> "Hoy"
        daysBetween == 1L -> "Ayer"
        else -> {
            val dayOfWeek = date.dayOfWeek.getDisplayName(java.time.format.TextStyle.FULL, Locale("es"))
                .replaceFirstChar { it.uppercase() }
            val dayOfMonth = date.dayOfMonth
            val month = date.month.getDisplayName(java.time.format.TextStyle.FULL, Locale("es"))
            "$dayOfWeek, $dayOfMonth de $month"
        }
    }
}
