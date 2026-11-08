package com.misgastos.app.presentation.screens.statistics

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.misgastos.app.MisGastosApplication
import com.misgastos.app.data.local.PreferencesManager
import com.misgastos.app.domain.model.Expense
import com.misgastos.app.domain.usecase.ExpenseStatistics
import com.misgastos.app.domain.usecase.GetExpensesUseCase
import com.misgastos.app.domain.usecase.GetStatisticsUseCase
import com.misgastos.app.util.ExpenseExporter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.TemporalAdjusters

enum class StatsPeriod(val label: String) {
    TODAY("Hoy"),
    THIS_WEEK("Esta semana"),
    THIS_MONTH("Este mes"),
    LAST_MONTH("Mes anterior"),
    THIS_YEAR("Este año")
}

data class StatisticsUiState(
    val statistics: ExpenseStatistics = ExpenseStatistics(),
    val expenses: List<Expense> = emptyList(),
    val selectedPeriod: StatsPeriod = StatsPeriod.THIS_MONTH,
    val currency: String = "C$",
    val isLoading: Boolean = true
)

@OptIn(ExperimentalCoroutinesApi::class)
class StatisticsViewModel(
    private val getStatisticsUseCase: GetStatisticsUseCase,
    private val getExpensesUseCase: GetExpensesUseCase,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _selectedPeriod = MutableStateFlow(StatsPeriod.THIS_MONTH)

    /**
     * flatMapLatest cancela automáticamente la colección anterior cuando cambia el período.
     * Carga estadísticas y la lista de gastos en un solo pipeline reactivo.
     */
    val uiState: StateFlow<StatisticsUiState> = combine(
        _selectedPeriod.flatMapLatest { period ->
            val (startDate, endDate) = getDateRange(period)
            combine(
                getStatisticsUseCase(startDate, endDate),
                getExpensesUseCase.byDateRange(startDate, endDate)
            ) { stats, expenseList ->
                stats to expenseList
            }
        },
        _selectedPeriod,
        preferencesManager.currency
    ) { (stats, expenseList), period, currency ->
        StatisticsUiState(
            statistics = stats,
            expenses = expenseList,
            selectedPeriod = period,
            currency = currency,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = StatisticsUiState()
    )

    fun selectPeriod(period: StatsPeriod) {
        _selectedPeriod.update { period }
    }

    /**
     * Exporta los gastos del período actual a un archivo CSV compatible con Excel
     * y abre el selector de Android para guardar en Google Drive, compartir por WhatsApp, etc.
     */
    fun exportCurrentPeriod(context: Context): Boolean {
        val state = uiState.value
        return ExpenseExporter.exportAndShare(
            context = context,
            expenses = state.expenses,
            periodTitle = state.selectedPeriod.label,
            currency = state.currency
        )
    }

    private fun getDateRange(period: StatsPeriod): Pair<LocalDate, LocalDate> {
        val today = LocalDate.now()
        return when (period) {
            StatsPeriod.TODAY -> today to today
            StatsPeriod.THIS_WEEK -> {
                val start = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                start to today
            }
            StatsPeriod.THIS_MONTH -> {
                val ym = YearMonth.from(today)
                ym.atDay(1) to ym.atEndOfMonth()
            }
            StatsPeriod.LAST_MONTH -> {
                val ym = YearMonth.from(today).minusMonths(1)
                ym.atDay(1) to ym.atEndOfMonth()
            }
            StatsPeriod.THIS_YEAR -> {
                LocalDate.of(today.year, 1, 1) to today
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as MisGastosApplication
                StatisticsViewModel(
                    getStatisticsUseCase = app.container.getStatisticsUseCase,
                    getExpensesUseCase = app.container.getExpensesUseCase,
                    preferencesManager = app.container.preferencesManager
                )
            }
        }
    }
}
