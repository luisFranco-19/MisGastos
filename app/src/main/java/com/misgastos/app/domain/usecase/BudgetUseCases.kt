package com.misgastos.app.domain.usecase

import com.misgastos.app.domain.model.Budget
import com.misgastos.app.domain.repository.BudgetRepository
import kotlinx.coroutines.flow.Flow

class GetBudgetUseCase(
    private val repository: BudgetRepository
) {
    operator fun invoke(year: Int, month: Int): Flow<Budget?> {
        return repository.getBudget(year, month)
    }
}

class SetBudgetUseCase(
    private val repository: BudgetRepository
) {
    suspend operator fun invoke(budget: Budget): Result<Unit> {
        return try {
            if (!budget.monthlyAmount.isFinite() || budget.monthlyAmount <= 0) {
                return Result.failure(IllegalArgumentException("El presupuesto debe ser mayor que 0"))
            }
            if (budget.month !in 1..12) {
                return Result.failure(IllegalArgumentException("El mes del presupuesto no es válido"))
            }
            if (budget.year !in 1..9999) {
                return Result.failure(IllegalArgumentException("El año del presupuesto no es válido"))
            }
            repository.setBudget(budget)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
