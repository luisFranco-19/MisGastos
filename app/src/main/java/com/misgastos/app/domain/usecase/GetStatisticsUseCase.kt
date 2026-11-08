package com.misgastos.app.domain.usecase

import com.misgastos.app.domain.model.Expense
import com.misgastos.app.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class ExpenseStatistics(
    val totalAmount: Double = 0.0,
    val expenseCount: Int = 0,
    val dailyAverage: Double = 0.0,
    val topCategory: String = "",
    val topCategoryAmount: Double = 0.0,
    val byCategory: Map<String, Double> = emptyMap(),
    val byPaymentMethod: Map<String, Double> = emptyMap(),
    val byDay: Map<LocalDate, Double> = emptyMap()
)

class GetStatisticsUseCase(
    private val repository: ExpenseRepository
) {
    operator fun invoke(startDate: LocalDate, endDate: LocalDate): Flow<ExpenseStatistics> {
        return repository.getExpensesByDateRange(startDate, endDate).map { expenses ->
            computeStatistics(expenses, startDate, endDate)
        }
    }

    private fun computeStatistics(
        expenses: List<Expense>,
        startDate: LocalDate,
        endDate: LocalDate
    ): ExpenseStatistics {
        if (expenses.isEmpty()) return ExpenseStatistics()

        val totalAmount = expenses.sumOf { it.amount }
        // The range is inclusive: Jan 1 through Jan 3 contains three calendar days.
        val daysBetween = (ChronoUnit.DAYS.between(startDate, endDate) + 1)
            .coerceAtLeast(1)
        val dailyAverage = totalAmount / daysBetween

        val byCategory = expenses.groupBy { it.category }
            .mapValues { (_, exps) -> exps.sumOf { it.amount } }
            .toSortedMap()

        val topEntry = byCategory.maxByOrNull { it.value }

        val byPaymentMethod = expenses.groupBy { it.paymentMethod.displayName }
            .mapValues { (_, exps) -> exps.sumOf { it.amount } }

        val byDay = expenses.groupBy { it.date }
            .mapValues { (_, exps) -> exps.sumOf { it.amount } }
            .toSortedMap()

        return ExpenseStatistics(
            totalAmount = totalAmount,
            expenseCount = expenses.size,
            dailyAverage = dailyAverage,
            topCategory = topEntry?.key ?: "",
            topCategoryAmount = topEntry?.value ?: 0.0,
            byCategory = byCategory,
            byPaymentMethod = byPaymentMethod,
            byDay = byDay
        )
    }
}
