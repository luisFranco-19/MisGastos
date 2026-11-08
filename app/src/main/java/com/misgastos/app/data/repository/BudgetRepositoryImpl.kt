package com.misgastos.app.data.repository

import com.misgastos.app.data.local.dao.BudgetDao
import com.misgastos.app.data.local.entity.BudgetEntity
import com.misgastos.app.domain.model.Budget
import com.misgastos.app.domain.repository.BudgetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BudgetRepositoryImpl(
    private val budgetDao: BudgetDao
) : BudgetRepository {

    override fun getBudget(year: Int, month: Int): Flow<Budget?> {
        return budgetDao.getBudget(year, month).map { it?.toDomain() }
    }

    override suspend fun setBudget(budget: Budget) {
        val existing = budgetDao.getBudgetOnce(budget.year, budget.month)
        val entity = if (existing != null) {
            BudgetEntity.fromDomain(budget.copy(id = existing.id))
        } else {
            BudgetEntity.fromDomain(budget)
        }
        budgetDao.insertBudget(entity)
    }

    override suspend fun deleteAllBudgets() {
        budgetDao.deleteAllBudgets()
    }
}
