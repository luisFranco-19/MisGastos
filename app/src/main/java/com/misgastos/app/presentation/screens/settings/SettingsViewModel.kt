package com.misgastos.app.presentation.screens.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.misgastos.app.MisGastosApplication
import com.misgastos.app.data.local.PreferencesManager
import com.misgastos.app.domain.model.AppCurrency
import com.misgastos.app.domain.model.Budget
import com.misgastos.app.domain.repository.BudgetRepository
import com.misgastos.app.domain.repository.CategoryRepository
import com.misgastos.app.domain.repository.ExpenseRepository
import com.misgastos.app.notification.DailyReminderScheduler
import com.misgastos.app.presentation.theme.ThemeMode
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class SettingsUiState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val currency: String = AppCurrency.DEFAULT.symbol,
    val currencyCode: String = AppCurrency.DEFAULT.code,
    val budgetAlerts: Boolean = true,
    val monthlyBudget: Double = 0.0,
    val totalExpensesCount: Int = 0,
    val showDeleteConfirmation: Boolean = false,
    val showCurrencyDialog: Boolean = false,
    val showAdjustBudgetDialog: Boolean = false,
    val message: String? = null,
    val dailyReminderEnabled: Boolean = false
)

class SettingsViewModel(
    private val preferencesManager: PreferencesManager,
    private val expenseRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository,
    private val budgetRepository: BudgetRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            val themeDeferred = async { preferencesManager.themeMode.first() }
            val currencyDeferred = async { preferencesManager.currency.first() }
            val currencyCodeDeferred = async { preferencesManager.currencyCode.first() }
            val budgetAlertsDeferred = async { preferencesManager.budgetAlerts.first() }
            val reminderEnabledDeferred = async { preferencesManager.dailyReminderEnabled.first() }
            val now = LocalDate.now()
            val budgetDeferred = async { budgetRepository.getBudget(now.year, now.monthValue).first() }
            val expensesDeferred = async { expenseRepository.getAllExpenses().first() }

            val theme = themeDeferred.await()
            val currency = currencyDeferred.await()
            val currencyCode = currencyCodeDeferred.await()
            val budgetAlerts = budgetAlertsDeferred.await()
            val reminderEnabled = reminderEnabledDeferred.await()
            val budget = budgetDeferred.await()
            val allExpenses = expensesDeferred.await()

            _uiState.update {
                it.copy(
                    themeMode = theme,
                    currency = currency,
                    currencyCode = currencyCode,
                    budgetAlerts = budgetAlerts,
                    monthlyBudget = budget?.monthlyAmount ?: 0.0,
                    totalExpensesCount = allExpenses.size,
                    dailyReminderEnabled = reminderEnabled
                )
            }
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            preferencesManager.setThemeMode(mode)
            _uiState.update { it.copy(themeMode = mode) }
            try {
                com.misgastos.app.widget.MisGastosWidgetReceiver.updateWidgets(com.misgastos.app.MisGastosApplication.appContext)
            } catch (_: Exception) {}
        }
    }

    fun setAppCurrency(appCurrency: AppCurrency) {
        viewModelScope.launch {
            preferencesManager.setAppCurrency(appCurrency)
            _uiState.update {
                it.copy(
                    currency = appCurrency.symbol,
                    currencyCode = appCurrency.code,
                    message = "Moneda cambiada a ${appCurrency.currencyName} (${appCurrency.symbol})"
                )
            }
            try {
                com.misgastos.app.widget.MisGastosWidgetReceiver.updateWidgets(com.misgastos.app.MisGastosApplication.appContext)
            } catch (_: Exception) {}
        }
    }

    fun showCurrencyDialog() {
        _uiState.update { it.copy(showCurrencyDialog = true) }
    }

    fun dismissCurrencyDialog() {
        _uiState.update { it.copy(showCurrencyDialog = false) }
    }

    fun showAdjustBudgetDialog() {
        _uiState.update { it.copy(showAdjustBudgetDialog = true) }
    }

    fun dismissAdjustBudgetDialog() {
        _uiState.update { it.copy(showAdjustBudgetDialog = false) }
    }

    fun saveAdjustedBudget(amount: Double) {
        if (amount <= 0) return
        viewModelScope.launch {
            val now = LocalDate.now()
            budgetRepository.setBudget(
                Budget(monthlyAmount = amount, year = now.year, month = now.monthValue)
            )
            _uiState.update {
                it.copy(
                    monthlyBudget = amount,
                    showAdjustBudgetDialog = false,
                    message = "Presupuesto actualizado a ${_uiState.value.currency} ${String.format("%,.2f", amount)}"
                )
            }
            try {
                com.misgastos.app.widget.MisGastosWidgetReceiver.updateWidgets(com.misgastos.app.MisGastosApplication.appContext)
            } catch (_: Exception) {}
        }
    }

    fun setBudgetAlerts(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setBudgetAlerts(enabled)
            _uiState.update { it.copy(budgetAlerts = enabled) }
        }
    }

    fun setDailyReminderEnabled(enabled: Boolean, context: Context) {
        viewModelScope.launch {
            preferencesManager.setDailyReminderEnabled(enabled)
            _uiState.update { it.copy(dailyReminderEnabled = enabled) }

            if (enabled) {
                DailyReminderScheduler.scheduleNextReminder(context)
                _uiState.update { it.copy(message = "Recordatorio inteligente activado") }
            } else {
                DailyReminderScheduler.cancelReminder(context)
                _uiState.update { it.copy(message = "Recordatorio inteligente desactivado") }
            }
        }
    }

    fun showDeleteConfirmation() {
        _uiState.update { it.copy(showDeleteConfirmation = true) }
    }

    fun dismissDeleteConfirmation() {
        _uiState.update { it.copy(showDeleteConfirmation = false) }
    }

    fun deleteAllData() {
        viewModelScope.launch {
            expenseRepository.deleteAllExpenses()
            categoryRepository.deleteAllCategories()
            budgetRepository.deleteAllBudgets()
            categoryRepository.initializeDefaultCategories()
            _uiState.update {
                it.copy(
                    showDeleteConfirmation = false,
                    message = "Datos locales eliminados",
                    monthlyBudget = 0.0,
                    totalExpensesCount = 0
                )
            }
            try {
                com.misgastos.app.widget.MisGastosWidgetReceiver.updateWidgets(com.misgastos.app.MisGastosApplication.appContext)
            } catch (_: Exception) {}
        }
    }

    fun showMessage(msg: String) {
        _uiState.update { it.copy(message = msg) }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as MisGastosApplication
                SettingsViewModel(
                    preferencesManager = app.container.preferencesManager,
                    expenseRepository = app.container.expenseRepository,
                    categoryRepository = app.container.categoryRepository,
                    budgetRepository = app.container.budgetRepository
                )
            }
        }
    }
}
