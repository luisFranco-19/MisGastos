package com.misgastos.app.domain.usecase

import com.misgastos.app.domain.model.Expense
import com.misgastos.app.domain.repository.ExpenseRepository

class UpdateExpenseUseCase(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke(expense: Expense): Result<Unit> {
        return try {
            if (expense.id <= 0) {
                return Result.failure(IllegalArgumentException("El gasto no tiene un ID válido"))
            }
            if (!expense.amount.isFinite() || expense.amount <= 0) {
                return Result.failure(IllegalArgumentException("El monto debe ser mayor que 0"))
            }
            if (expense.category.isBlank()) {
                return Result.failure(IllegalArgumentException("Selecciona una categoría"))
            }
            repository.updateExpense(expense)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
