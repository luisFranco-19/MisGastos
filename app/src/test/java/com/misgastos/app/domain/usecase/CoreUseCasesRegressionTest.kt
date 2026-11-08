package com.misgastos.app.domain.usecase

import com.misgastos.app.domain.model.Budget
import com.misgastos.app.domain.model.Expense
import com.misgastos.app.domain.model.PaymentMethod
import com.misgastos.app.domain.repository.BudgetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

private class RecordingBudgetRepository : BudgetRepository {
    var saved: Budget? = null

    override fun getBudget(year: Int, month: Int): Flow<Budget?> = flowOf(saved)

    override suspend fun setBudget(budget: Budget) {
        saved = budget
    }

    override suspend fun deleteAllBudgets() {
        saved = null
    }
}

private fun expense(
    amount: Double,
    date: LocalDate = LocalDate.of(2026, 1, 1),
    category: String = "Comida"
) = Expense(
    amount = amount,
    paymentMethod = PaymentMethod.CASH,
    category = category,
    date = date
)

class UpdateExpenseUseCaseTest {

    @Test
    fun `update valid expense delegates to repository`() = runTest {
        val repository = FakeExpenseRepository()
        val insertedId = repository.insertExpense(expense(10.0))
        val useCase = UpdateExpenseUseCase(repository)

        val result = useCase(expense(25.0).copy(id = insertedId))

        assertTrue(result.isSuccess)
        assertEquals(25.0, repository.getExpenseById(insertedId)?.amount, 0.001)
    }

    @Test
    fun `update rejects non-positive amounts`() = runTest {
        val repository = FakeExpenseRepository()
        val useCase = UpdateExpenseUseCase(repository)

        val result = useCase(expense(0.0))

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun `update rejects an invalid id`() = runTest {
        val result = UpdateExpenseUseCase(FakeExpenseRepository())(expense(10.0))

        assertTrue(result.isFailure)
    }

    @Test
    fun `update rejects non-finite amounts`() = runTest {
        val repository = FakeExpenseRepository()
        val id = repository.insertExpense(expense(10.0))
        val useCase = UpdateExpenseUseCase(repository)

        assertTrue(useCase(expense(Double.NaN).copy(id = id)).isFailure)
        assertTrue(useCase(expense(Double.POSITIVE_INFINITY).copy(id = id)).isFailure)
    }
}

class DeleteExpenseUseCaseTest {

    @Test
    fun `delete rejects an invalid id`() = runTest {
        val result = DeleteExpenseUseCase(FakeExpenseRepository())(0L)

        assertTrue(result.isFailure)
    }

    @Test
    fun `delete removes the requested expense`() = runTest {
        val repository = FakeExpenseRepository()
        val id = repository.insertExpense(expense(10.0))

        val result = DeleteExpenseUseCase(repository)(id)

        assertTrue(result.isSuccess)
        assertEquals(null, repository.getExpenseById(id))
    }
}

class BudgetUseCaseTest {

    @Test
    fun `set budget accepts a positive amount`() = runTest {
        val repository = RecordingBudgetRepository()
        val budget = Budget(monthlyAmount = 500.0, year = 2026, month = 1)

        val result = SetBudgetUseCase(repository)(budget)

        assertTrue(result.isSuccess)
        assertEquals(budget, repository.saved)
    }

    @Test
    fun `set budget rejects zero and negative amounts`() = runTest {
        val repository = RecordingBudgetRepository()
        val useCase = SetBudgetUseCase(repository)

        assertTrue(useCase(Budget(0.0, 2026, 1)).isFailure)
        assertTrue(useCase(Budget(-1.0, 2026, 1)).isFailure)
        assertEquals(null, repository.saved)
    }

    @Test
    fun `set budget rejects invalid month, year and non-finite amounts`() = runTest {
        val useCase = SetBudgetUseCase(RecordingBudgetRepository())

        assertTrue(useCase(Budget(100.0, 2026, 0)).isFailure)
        assertTrue(useCase(Budget(100.0, 2026, 13)).isFailure)
        assertTrue(useCase(Budget(100.0, 0, 1)).isFailure)
        assertTrue(useCase(Budget(Double.NaN, 2026, 1)).isFailure)
        assertTrue(useCase(Budget(Double.POSITIVE_INFINITY, 2026, 1)).isFailure)
    }
}

class GetMonthlyTotalUseCaseTest {

    @Test
    fun `monthly total includes every day in the requested month`() = runTest {
        val repository = FakeExpenseRepository()
        repository.insertExpense(expense(10.0, LocalDate.of(2026, 2, 1)))
        repository.insertExpense(expense(20.0, LocalDate.of(2026, 2, 28)))
        repository.insertExpense(expense(100.0, LocalDate.of(2026, 3, 1)))

        val total = GetMonthlyTotalUseCase(repository)(2026, 2).first()

        assertEquals(30.0, total, 0.001)
    }
}

class GetStatisticsRegressionTest {

    @Test
    fun `daily average uses the inclusive number of days`() = runTest {
        val repository = FakeExpenseRepository()
        repository.insertExpense(expense(100.0, LocalDate.of(2026, 1, 1)))
        repository.insertExpense(expense(200.0, LocalDate.of(2026, 1, 3)))

        val stats = GetStatisticsUseCase(repository)
            .invoke(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 3))
            .first()

        assertEquals(100.0, stats.dailyAverage, 0.001)
    }
}
