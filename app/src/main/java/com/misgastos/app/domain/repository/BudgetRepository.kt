package com.misgastos.app.domain.repository

import com.misgastos.app.domain.model.Budget
import kotlinx.coroutines.flow.Flow

interface BudgetRepository {
    fun getBudget(year: Int, month: Int): Flow<Budget?>
    suspend fun setBudget(budget: Budget)
    suspend fun deleteAllBudgets()
}
