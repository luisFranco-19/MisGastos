package com.misgastos.app.presentation.screens.settings

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.misgastos.app.domain.model.AppCurrency
import com.misgastos.app.presentation.components.AdjustBudgetDialog
import com.misgastos.app.presentation.components.CurrencySelectorDialog
import com.misgastos.app.presentation.theme.Spacing
import com.misgastos.app.presentation.theme.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.setDailyReminderEnabled(true, context)
        } else {
            viewModel.showMessage(
                "Las notificaciones están desactivadas. Puedes activarlas desde los ajustes de Android."
            )
        }
    }

    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    // ─── Dialogs ────────────────────────────────────────────────────────────

    if (uiState.showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = viewModel::dismissDeleteConfirmation,
            title = { Text("Eliminar datos", fontWeight = FontWeight.Bold) },
            text = { Text("¿Estás seguro de que quieres eliminar todos los datos locales? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = viewModel::deleteAllData) {
                    Text("Eliminar todo", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::dismissDeleteConfirmation) { Text("Cancelar") }
            }
        )
    }

    if (uiState.showCurrencyDialog) {
        CurrencySelectorDialog(
            currentCurrency = uiState.currency,
            onCurrencySelected = { appCurrency ->
                viewModel.setAppCurrency(appCurrency)
            },
            onDismiss = viewModel::dismissCurrencyDialog
        )
    }

    if (uiState.showAdjustBudgetDialog) {
        AdjustBudgetDialog(
            currentBudget = uiState.monthlyBudget,
            currency = uiState.currency,
            onSaveBudget = { newAmount ->
                viewModel.saveAdjustedBudget(newAmount)
            },
            onDismiss = viewModel::dismissAdjustBudgetDialog
        )
    }

    // ─── Pantalla Principal ──────────────────────────────────────────────────

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ajustes", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineSmall) },
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
            contentPadding = PaddingValues(horizontal = Spacing.lg, vertical = Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg)
        ) {

            // ── Grupo 1: Recordatorios ─────────────────────────────────────
            item(key = "group_reminders") {
                SettingsGroupHeader(title = "Recordatorios y Notificaciones")
                SettingsCardContainer {
                    SettingsSwitchRow(
                        icon = Icons.Outlined.Notifications,
                        iconBgColor = Color(0xFF0D9488).copy(alpha = 0.15f),
                        iconTint = Color(0xFF0D9488),
                        title = "Recordatorio inteligente",
                        subtitle = "Notificaciones automáticas durante el día para registrar gastos",
                        checked = uiState.dailyReminderEnabled,
                        onCheckedChange = { enable ->
                            if (enable) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    val hasPermission = ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.POST_NOTIFICATIONS
                                    ) == PackageManager.PERMISSION_GRANTED

                                    if (hasPermission) {
                                        viewModel.setDailyReminderEnabled(true, context)
                                    } else {
                                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                } else {
                                    viewModel.setDailyReminderEnabled(true, context)
                                }
                            } else {
                                viewModel.setDailyReminderEnabled(false, context)
                            }
                        }
                    )
                }
            }

            // ── Grupo 2: Apariencia y Moneda ───────────────────────────────
            item(key = "group_general") {
                SettingsGroupHeader(title = "General y Apariencia")
                SettingsCardContainer {
                    // Tema
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            SettingsIconBadge(
                                icon = Icons.Outlined.Palette,
                                bgColor = Color(0xFF6366F1).copy(alpha = 0.15f),
                                tint = Color(0xFF6366F1)
                            )
                            Text(
                                text = "Tema de la aplicación",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                            val options = listOf(
                                ThemeMode.LIGHT to "Claro",
                                ThemeMode.DARK to "Oscuro",
                                ThemeMode.SYSTEM to "Sistema"
                            )
                            options.forEachIndexed { index, (mode, label) ->
                                SegmentedButton(
                                    selected = uiState.themeMode == mode,
                                    onClick = { viewModel.setThemeMode(mode) },
                                    shape = SegmentedButtonDefaults.itemShape(index, options.size)
                                ) { Text(label, fontWeight = FontWeight.Medium) }
                            }
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                    // Moneda
                    val currentAppCurrency = AppCurrency.fromCode(uiState.currencyCode)
                    SettingsClickableRow(
                        icon = Icons.Outlined.Payments,
                        iconBgColor = Color(0xFF10B981).copy(alpha = 0.15f),
                        iconTint = Color(0xFF10B981),
                        title = "Moneda principal",
                        subtitle = "${currentAppCurrency.flag} ${currentAppCurrency.currencyName} (${uiState.currency})",
                        onClick = viewModel::showCurrencyDialog
                    )
                }
            }

            // ── Grupo 3: Presupuesto ───────────────────────────────────────
            item(key = "group_budget") {
                SettingsGroupHeader(title = "Presupuesto Mensual")
                SettingsCardContainer {
                    SettingsClickableRow(
                        icon = Icons.Outlined.AccountBalanceWallet,
                        iconBgColor = Color(0xFFF59E0B).copy(alpha = 0.15f),
                        iconTint = Color(0xFFF59E0B),
                        title = "Límite de presupuesto",
                        subtitle = if (uiState.monthlyBudget > 0)
                            "${uiState.currency} ${String.format("%,.2f", uiState.monthlyBudget)}"
                        else "Sin límite configurado",
                        onClick = viewModel::showAdjustBudgetDialog
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                    SettingsSwitchRow(
                        icon = Icons.Outlined.Notifications,
                        iconBgColor = Color(0xFFF59E0B).copy(alpha = 0.15f),
                        iconTint = Color(0xFFF59E0B),
                        title = "Alertas de presupuesto",
                        subtitle = "Avisar cuando se supere el 80% del límite mensual",
                        checked = uiState.budgetAlerts,
                        onCheckedChange = viewModel::setBudgetAlerts
                    )
                }
            }

            // ── Grupo 4: Datos y Almacenamiento ────────────────────────────
            item(key = "group_data") {
                SettingsGroupHeader(title = "Almacenamiento")
                SettingsCardContainer {
                    SettingsClickableRow(
                        icon = Icons.Outlined.DeleteOutline,
                        iconBgColor = MaterialTheme.colorScheme.error.copy(alpha = 0.12f),
                        iconTint = MaterialTheme.colorScheme.error,
                        title = "Eliminar datos locales",
                        subtitle = "Borra todos los gastos y restablece la app",
                        onClick = viewModel::showDeleteConfirmation,
                        isDestructive = true
                    )
                }
            }

            item(key = "bottom_spacer") { Spacer(modifier = Modifier.height(Spacing.xxl)) }
        }
    }
}

// ─── Componentes Auxiliares de Ajustes Estilo Apple / Fintech ─────────────────

@Composable
private fun SettingsGroupHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.3.sp
        ),
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
    )
}

@Composable
private fun SettingsCardContainer(
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            content()
        }
    }
}

@Composable
private fun SettingsIconBadge(
    icon: ImageVector,
    bgColor: Color,
    tint: Color
) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun SettingsClickableRow(
    icon: ImageVector,
    iconBgColor: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            SettingsIconBadge(icon = icon, bgColor = iconBgColor, tint = iconTint)
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Icon(
            Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun SettingsSwitchRow(
    icon: ImageVector,
    iconBgColor: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            SettingsIconBadge(icon = icon, bgColor = iconBgColor, tint = iconTint)
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                checkedTrackColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}
