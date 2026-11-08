package com.misgastos.app.presentation.screens.expenses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.misgastos.app.MisGastosApplication
import com.misgastos.app.data.local.PreferencesManager
import com.misgastos.app.domain.model.Expense
import com.misgastos.app.domain.usecase.DeleteExpenseUseCase
import com.misgastos.app.domain.usecase.GetExpensesUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ExpensesUiState(
    val expenses: List<Expense> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: String? = null,
    val selectedPaymentMethod: String? = null,
    val sortByAmount: Boolean = false,
    val currency: String = "C$",
    val isLoading: Boolean = true,
    val deleteSuccess: Boolean = false,
    val errorMessage: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
class ExpensesViewModel(
    private val getExpensesUseCase: GetExpensesUseCase,
    private val deleteExpenseUseCase: DeleteExpenseUseCase,
    private val preferencesManager: PreferencesManager
    // SyncManager ELIMINADO — no hay sincronización automática al eliminar gastos
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategory = MutableStateFlow<String?>(null)
    private val _selectedPaymentMethod = MutableStateFlow<String?>(null)
    private val _sortByAmount = MutableStateFlow(false)
    private val _deleteSuccess = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<ExpensesUiState> = combine(
        _searchQuery.flatMapLatest { query ->
            if (query.isBlank()) getExpensesUseCase.all()
            else getExpensesUseCase.search(query)
        },
        _selectedCategory,
        _selectedPaymentMethod,
        _sortByAmount,
        preferencesManager.currency
    ) { expenses, category, payment, sortAmount, currency ->
        val filtered = expenses.filter { expense ->
            (category == null || expense.category == category) &&
            (payment == null || expense.paymentMethod.displayName == payment)
        }
        val sorted = if (sortAmount) filtered.sortedByDescending { it.amount } else filtered

        ExpensesUiState(
            expenses = sorted,
            searchQuery = _searchQuery.value,
            selectedCategory = category,
            selectedPaymentMethod = payment,
            sortByAmount = sortAmount,
            currency = currency,
            isLoading = false,
            deleteSuccess = _deleteSuccess.value,
            errorMessage = _errorMessage.value
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ExpensesUiState()
    )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onCategoryFilter(category: String?) {
        _selectedCategory.value = category
    }

    fun onPaymentMethodFilter(method: String?) {
        _selectedPaymentMethod.value = method
    }

    fun toggleSortByAmount() {
        _sortByAmount.value = !_sortByAmount.value
    }

    fun deleteExpense(id: Long) {
        viewModelScope.launch {
            deleteExpenseUseCase(id)
                .onSuccess {
                    _deleteSuccess.value = true
                    // Actualizar widget para reflejar el gasto eliminado
                    try {
                        com.misgastos.app.widget.MisGastosWidgetReceiver.scheduleWidgetUpdate(
                            com.misgastos.app.MisGastosApplication.appContext
                        )
                    } catch (_: Exception) {}
                    // NO sincronizar — el gasto queda marcado con soft-delete y PENDING
                    // El usuario sincronizará manualmente desde Ajustes > Google Sheets
                }
                .onFailure {
                    _errorMessage.value = "Error al eliminar el gasto"
                }
        }
    }

    fun resetDeleteSuccess() {
        _deleteSuccess.value = false
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as MisGastosApplication
                ExpensesViewModel(
                    getExpensesUseCase = app.container.getExpensesUseCase,
                    deleteExpenseUseCase = app.container.deleteExpenseUseCase,
                    preferencesManager = app.container.preferencesManager
                    // SyncManager ya no se usa
                )
            }
        }
    }
}
