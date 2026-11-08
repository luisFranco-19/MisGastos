package com.misgastos.app.domain.usecase

import com.misgastos.app.domain.model.Expense
import com.misgastos.app.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class GetExpensesUseCase(
    private val repository: ExpenseRepository
) {
    fun all(): Flow<List<Expense>> = repository.getAllExpenses()

    fun byDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<Expense>> =
        repository.getExpensesByDateRange(startDate, endDate)

    fun byCategory(category: String): Flow<List<Expense>> =
        repository.getExpensesByCategory(category)

    fun byPaymentMethod(paymentMethod: String): Flow<List<Expense>> =
        repository.getExpensesByPaymentMethod(paymentMethod)

    fun recent(limit: Int = 5): Flow<List<Expense>> =
        repository.getRecentExpenses(limit)

    fun search(query: String): Flow<List<Expense>> =
        repository.searchExpenses(query)

    suspend fun getById(id: Long): Expense? =
        repository.getExpenseById(id)
}
