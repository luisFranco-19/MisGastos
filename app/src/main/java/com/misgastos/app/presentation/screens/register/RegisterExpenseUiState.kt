package com.misgastos.app.presentation.screens.register

import com.misgastos.app.domain.model.AppCurrency
import com.misgastos.app.domain.model.Category
import com.misgastos.app.domain.model.PaymentMethod
import java.time.LocalDate

/**
 * Pasos del flujo de registro:
 * 1 — Monto
 * 2 — Método de pago
 * 3 — Categoría
 * 4 — Detalles (descripción + fecha)
 * 5 — Confirmación (pantalla de éxito)
 */
data class RegisterExpenseUiState(
    // Paso actual (1-4) y confirmación
    val currentStep: Int = 1,
    val showConfirmation: Boolean = false,

    // Moneda activa
    val currency: String = AppCurrency.DEFAULT.symbol,

    // Paso 1 — Monto
    val amount: String = "",
    val amountError: String? = null,

    // Paso 2 — Método de pago
    val selectedPaymentMethod: PaymentMethod? = null,
    val paymentMethodError: String? = null,

    // Paso 3 — Categoría
    val categories: List<Category> = emptyList(),
    val selectedCategory: String = "",
    val selectedCategoryIcon: String = "",
    val categoryError: String? = null,

    // Paso 4 — Detalles
    val description: String = "",
    val date: LocalDate = LocalDate.now(),

    // Estado de edición
    val isEditing: Boolean = false,
    val editingExpenseId: Long? = null,

    // Estado de guardado
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null,

    // Datos del gasto guardado (para mostrar en confirmación)
    val savedAmount: Double = 0.0,
    val savedPaymentMethod: PaymentMethod? = null,
    val savedCategory: String = "",
    val savedCategoryIcon: String = "",
    val savedDate: LocalDate = LocalDate.now()
)
