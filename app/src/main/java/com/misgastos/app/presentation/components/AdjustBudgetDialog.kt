package com.misgastos.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.misgastos.app.presentation.theme.Spacing

enum class BudgetAdjustMode {
    SET_NEW,
    ADD_AMOUNT
}

/**
 * Diálogo para ajustar el presupuesto mensual:
 * Permite "Establecer nuevo" o "Agregar cantidad" al presupuesto actual.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdjustBudgetDialog(
    currentBudget: Double,
    currency: String,
    onSaveBudget: (newBudget: Double) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedModeIndex by remember { mutableIntStateOf(0) }
    var inputAmount by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val currentMode = if (selectedModeIndex == 0) BudgetAdjustMode.SET_NEW else BudgetAdjustMode.ADD_AMOUNT

    val parsedInput = inputAmount.toDoubleOrNull() ?: 0.0
    val resultingBudget = when (currentMode) {
        BudgetAdjustMode.SET_NEW -> parsedInput
        BudgetAdjustMode.ADD_AMOUNT -> currentBudget + parsedInput
    }

    val handleSave = {
        val amount = inputAmount.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            errorMessage = "Ingresa una cantidad válida."
        } else {
            val finalBudget = when (currentMode) {
                BudgetAdjustMode.SET_NEW -> amount
                BudgetAdjustMode.ADD_AMOUNT -> currentBudget + amount
            }
            if (finalBudget <= 0) {
                errorMessage = "El presupuesto resultante debe ser mayor a 0."
            } else {
                onSaveBudget(finalBudget)
                onDismiss()
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Ajustar presupuesto",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                // Tarjeta con presupuesto actual
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(horizontal = Spacing.md, vertical = Spacing.sm)
                ) {
                    Text(
                        text = "Presupuesto actual",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$currency ${String.format("%,.2f", currentBudget)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Selector de modo: Establecer nuevo | Agregar cantidad
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    SegmentedButton(
                        selected = selectedModeIndex == 0,
                        onClick = {
                            selectedModeIndex = 0
                            errorMessage = null
                        },
                        shape = SegmentedButtonDefaults.itemShape(0, 2)
                    ) {
                        Text("Establecer nuevo")
                    }
                    SegmentedButton(
                        selected = selectedModeIndex == 1,
                        onClick = {
                            selectedModeIndex = 1
                            errorMessage = null
                        },
                        shape = SegmentedButtonDefaults.itemShape(1, 2)
                    ) {
                        Text("Agregar cantidad")
                    }
                }

                // Campo de entrada
                OutlinedTextField(
                    value = inputAmount,
                    onValueChange = { newValue ->
                        val filtered = newValue.filter { it.isDigit() || it == '.' }
                        inputAmount = filtered
                        errorMessage = null
                    },
                    label = {
                        Text(
                            if (currentMode == BudgetAdjustMode.SET_NEW) "Nuevo presupuesto"
                            else "¿Cuánto deseas agregar?"
                        )
                    },
                    prefix = { Text("$currency ") },
                    isError = errorMessage != null,
                    supportingText = errorMessage?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth()
                )

                // Previsualización del resultado si es modo agregar
                if (currentMode == BudgetAdjustMode.ADD_AMOUNT && parsedInput > 0) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f))
                            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Resultado:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "$currency ${String.format("%,.2f", resultingBudget)}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { handleSave() }) {
                Text("Guardar", fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
