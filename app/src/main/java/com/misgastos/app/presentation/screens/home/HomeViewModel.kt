package com.misgastos.app.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.misgastos.app.MisGastosApplication
import com.misgastos.app.domain.usecase.GetBudgetUseCase
import com.misgastos.app.domain.usecase.GetExpensesUseCase
import com.misgastos.app.domain.usecase.GetMonthlyTotalUseCase
import com.misgastos.app.data.local.PreferencesManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

class HomeViewModel(
    private val getMonthlyTotalUseCase: GetMonthlyTotalUseCase,
    private val getExpensesUseCase: GetExpensesUseCase,
    private val getBudgetUseCase: GetBudgetUseCase,
    private val preferencesManager: PreferencesManager
    // expenseRepository ya no se inyecta aquí — no se observa pendingSyncCount
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = run {
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)
        val startOfMonth = today.withDayOfMonth(1)
        val endOfMonth = startOfMonth.plusMonths(1).minusDays(1)

        /*
         * Una sola observación de Room alimenta el inicio. Antes, la misma
         * modificación invalidaba cinco consultas (total, conteo, mes, hoy y
         * ayer); ahora los resúmenes se derivan de la lista ya cargada.
         * El rango comienza ayer para mantener correcta la vista el día 1.
         */
        val expensesFlow = getExpensesUseCase.byDateRange(yesterday, endOfMonth)

        combine(
            expensesFlow,
            getBudgetUseCase(today.year, today.monthValue),
            preferencesManager.currency
        ) { expensesFromYesterday, budget, currency ->
            val monthlyExpenses = expensesFromYesterday.filter { it.date >= startOfMonth }
            val highlights = monthlyExpenses
                .groupBy { it.category }
                .map { (category, expenses) ->
                    CategoryHighlight(
                        name = category,
                        icon = expenses.firstOrNull()?.categoryIcon?.ifEmpty { "📦" } ?: "📦",
                        totalAmount = expenses.sumOf { it.amount },
                        count = expenses.size
                    )
                }
                .sortedByDescending { it.totalAmount }

            HomeUiState(
                monthlyTotal = monthlyExpenses.sumOf { it.amount },
                monthlyCount = monthlyExpenses.size,
                todayExpenses = expensesFromYesterday.filter { it.date == today },
                yesterdayExpenses = expensesFromYesterday.filter { it.date == yesterday },
                recentExpenses = expensesFromYesterday.filter { it.date == today },
                categoryHighlights = highlights,
                budgetAmount = budget?.monthlyAmount,
                currency = currency,
                isLoading = false
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState()
        )
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as MisGastosApplication
                HomeViewModel(
                    getMonthlyTotalUseCase = app.container.getMonthlyTotalUseCase,
                    getExpensesUseCase = app.container.getExpensesUseCase,
                    getBudgetUseCase = app.container.getBudgetUseCase,
                    preferencesManager = app.container.preferencesManager
                )
            }
        }
    }
}
