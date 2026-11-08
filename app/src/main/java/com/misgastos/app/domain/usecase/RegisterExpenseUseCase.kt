package com.misgastos.app.domain.usecase

import com.misgastos.app.domain.model.Expense
import com.misgastos.app.domain.repository.ExpenseRepository

class RegisterExpenseUseCase(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke(expense: Expense): Result<Long> {
        return try {
            if (!expense.amount.isFinite() || expense.amount <= 0) {
                return Result.failure(IllegalArgumentException("El monto debe ser mayor que 0"))
            }
            if (expense.category.isBlank()) {
                return Result.failure(IllegalArgumentException("Selecciona una categoría"))
            }
            val id = repository.insertExpense(expense)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
