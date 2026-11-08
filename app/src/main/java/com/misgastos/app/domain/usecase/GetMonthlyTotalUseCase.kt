package com.misgastos.app.domain.usecase

import com.misgastos.app.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.YearMonth

class GetMonthlyTotalUseCase(
    private val repository: ExpenseRepository
) {
    operator fun invoke(year: Int, month: Int): Flow<Double> {
        val yearMonth = YearMonth.of(year, month)
        val startDate = yearMonth.atDay(1)
        val endDate = yearMonth.atEndOfMonth()
        return repository.getTotalByDateRange(startDate, endDate)
    }

    fun count(year: Int, month: Int): Flow<Int> {
        val yearMonth = YearMonth.of(year, month)
        val startDate = yearMonth.atDay(1)
        val endDate = yearMonth.atEndOfMonth()
        return repository.getCountByDateRange(startDate, endDate)
    }

    fun forDateRange(startDate: LocalDate, endDate: LocalDate): Flow<Double> {
        return repository.getTotalByDateRange(startDate, endDate)
    }
}
