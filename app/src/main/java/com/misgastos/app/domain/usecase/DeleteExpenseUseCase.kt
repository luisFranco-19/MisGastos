package com.misgastos.app.domain.usecase

import com.misgastos.app.domain.repository.ExpenseRepository

class DeleteExpenseUseCase(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke(id: Long): Result<Unit> {
        return try {
            if (id <= 0) {
                return Result.failure(IllegalArgumentException("El gasto no tiene un ID válido"))
            }
            repository.deleteExpense(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
