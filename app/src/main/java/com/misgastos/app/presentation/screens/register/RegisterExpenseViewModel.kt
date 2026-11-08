package com.misgastos.app.presentation.screens.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.misgastos.app.MisGastosApplication
import com.misgastos.app.data.local.PreferencesManager
import com.misgastos.app.domain.model.Expense
import com.misgastos.app.domain.model.PaymentMethod
import com.misgastos.app.domain.model.SyncStatus
import com.misgastos.app.domain.repository.CategoryRepository
import com.misgastos.app.domain.usecase.GetExpensesUseCase
import com.misgastos.app.domain.usecase.RegisterExpenseUseCase
import com.misgastos.app.domain.usecase.UpdateExpenseUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class RegisterExpenseViewModel(
    private val registerExpenseUseCase: RegisterExpenseUseCase,
    private val updateExpenseUseCase: UpdateExpenseUseCase,
    private val getExpensesUseCase: GetExpensesUseCase,
    private val categoryRepository: CategoryRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterExpenseUiState())
    val uiState: StateFlow<RegisterExpenseUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            categoryRepository.getAllCategories().collect { categories ->
                _uiState.update { it.copy(categories = categories) }
            }
        }
        viewModelScope.launch {
            preferencesManager.currency.collect { curr ->
                _uiState.update { it.copy(currency = curr) }
            }
        }
    }

    /**
     * Reinicia completamente el formulario para que cada nuevo gasto empiece limpio:
     * Monto: vacío, Método: ninguno, Categoría: ninguna, Descripción: vacía, Fecha: hoy, Paso: 1.
     */
    fun resetForm() {
        _uiState.update {
            it.copy(
                currentStep = 1,
                showConfirmation = false,
                amount = "",
                amountError = null,
                selectedPaymentMethod = null,
                paymentMethodError = null,
                selectedCategory = "",
                selectedCategoryIcon = "",
                categoryError = null,
                description = "",
                date = LocalDate.now(),
                isEditing = false,
                editingExpenseId = null,
                isSaving = false,
                saveSuccess = false,
                errorMessage = null
            )
        }
    }

    fun loadExpenseForEditing(expenseId: Long) {
        viewModelScope.launch {
            val expense = getExpensesUseCase.getById(expenseId)
            if (expense != null) {
                _uiState.update {
                    it.copy(
                        amount = if (expense.amount > 0) expense.amount.toString() else "",
                        selectedPaymentMethod = expense.paymentMethod,
                        selectedCategory = expense.category,
                        selectedCategoryIcon = expense.categoryIcon,
                        description = expense.description ?: "",
                        date = expense.date,
                        isEditing = true,
                        editingExpenseId = expense.id,
                        currentStep = 1,
                        showConfirmation = false
                    )
                }
            }
        }
    }

    // ─── Cambios de campo ───────────────────────────────────────────────────

    fun onAmountChange(amount: String) {
        val filtered = amount.filter { it.isDigit() || it == '.' }
        _uiState.update { it.copy(amount = filtered, amountError = null) }
    }

    fun onPaymentMethodChange(method: PaymentMethod) {
        _uiState.update { it.copy(selectedPaymentMethod = method, paymentMethodError = null) }
    }

    fun onCategoryChange(category: String, icon: String) {
        _uiState.update { it.copy(selectedCategory = category, selectedCategoryIcon = icon, categoryError = null) }
    }

    fun onDescriptionChange(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun onDateChange(date: LocalDate) {
        _uiState.update { it.copy(date = date) }
    }

    // ─── Navegación por pasos ───────────────────────────────────────────────

    fun nextStep() {
        val state = _uiState.value
        when (state.currentStep) {
            1 -> {
                val amount = state.amount.toDoubleOrNull()
                when {
                    state.amount.isBlank() ->
                        _uiState.update { it.copy(amountError = "Introduce el monto del gasto.") }
                    amount == null || amount <= 0 ->
                        _uiState.update { it.copy(amountError = "El monto debe ser mayor que ${state.currency} 0.00.") }
                    else ->
                        _uiState.update { it.copy(currentStep = 2, amountError = null) }
                }
            }
            2 -> {
                if (state.selectedPaymentMethod == null) {
                    _uiState.update { it.copy(paymentMethodError = "Selecciona un método de pago.") }
                } else {
                    _uiState.update { it.copy(currentStep = 3, paymentMethodError = null) }
                }
            }
            3 -> {
                if (state.selectedCategory.isBlank()) {
                    _uiState.update { it.copy(categoryError = "Selecciona una categoría.") }
                } else {
                    _uiState.update { it.copy(currentStep = 4, categoryError = null) }
                }
            }
            4 -> {
                saveExpense()
            }
        }
    }

    fun previousStep() {
        val currentStep = _uiState.value.currentStep
        if (currentStep > 1) {
            _uiState.update { it.copy(currentStep = currentStep - 1) }
        }
    }

    private fun saveExpense() {
        val state = _uiState.value

        val amount = state.amount.toDoubleOrNull() ?: return
        val paymentMethod = state.selectedPaymentMethod ?: return

        _uiState.update { it.copy(isSaving = true, errorMessage = null) }

        viewModelScope.launch {
            val expense = Expense(
                id = state.editingExpenseId ?: 0,
                amount = amount,
                paymentMethod = paymentMethod,
                category = state.selectedCategory,
                categoryIcon = state.selectedCategoryIcon,
                description = state.description.ifBlank { null },
                date = state.date,
                syncStatus = SyncStatus.PENDING
            )

            val result = if (state.isEditing) {
                updateExpenseUseCase(expense).map { expense.id }
            } else {
                registerExpenseUseCase(expense)
            }

            result.onSuccess {
                // Actualizar widget local
                try {
                    com.misgastos.app.widget.MisGastosWidgetReceiver.scheduleWidgetUpdate(
                        com.misgastos.app.MisGastosApplication.appContext
                    )
                } catch (_: Exception) {}

                _uiState.update {
                    it.copy(
                        isSaving = false,
                        saveSuccess = true,
                        showConfirmation = true,
                        savedAmount = amount,
                        savedPaymentMethod = paymentMethod,
                        savedCategory = state.selectedCategory,
                        savedCategoryIcon = state.selectedCategoryIcon,
                        savedDate = state.date
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(isSaving = false, errorMessage = error.message)
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as MisGastosApplication
                RegisterExpenseViewModel(
                    registerExpenseUseCase = app.container.registerExpenseUseCase,
                    updateExpenseUseCase = app.container.updateExpenseUseCase,
                    getExpensesUseCase = app.container.getExpensesUseCase,
                    categoryRepository = app.container.categoryRepository,
                    preferencesManager = app.container.preferencesManager
                )
            }
        }
    }
}
