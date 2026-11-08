package com.misgastos.app.domain.usecase

import com.misgastos.app.domain.model.Expense
import com.misgastos.app.domain.model.PaymentMethod
import com.misgastos.app.domain.model.SyncStatus
import com.misgastos.app.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class FakeExpenseRepository : ExpenseRepository {
    private val expenses = mutableListOf<Expense>()
    private var nextId = 1L

    override suspend fun insertExpense(expense: Expense): Long {
        val id = nextId++
        expenses.add(expense.copy(id = id))
        return id
    }

    override suspend fun updateExpense(expense: Expense) {
        val index = expenses.indexOfFirst { it.id == expense.id }
        if (index >= 0) expenses[index] = expense
    }

    override suspend fun deleteExpense(id: Long) {
        expenses.removeIf { it.id == id }
    }

    override suspend fun getExpenseById(id: Long) = expenses.find { it.id == id }
    override fun getAllExpenses() = flowOf(expenses.toList())
    override fun getExpensesByDateRange(startDate: LocalDate, endDate: LocalDate) = flowOf(
        expenses.filter { it.date in startDate..endDate }
    )
    override fun getExpensesByCategory(category: String) = flowOf(
        expenses.filter { it.category == category }
    )
    override fun getExpensesByPaymentMethod(paymentMethod: String) = flowOf(
        expenses.filter { it.paymentMethod.displayName == paymentMethod }
    )
    override fun getTotalByDateRange(startDate: LocalDate, endDate: LocalDate) = flowOf(
        expenses.filter { it.date in startDate..endDate }.sumOf { it.amount }
    )
    override fun getCountByDateRange(startDate: LocalDate, endDate: LocalDate) = flowOf(
        expenses.count { it.date in startDate..endDate }
    )
    override fun getRecentExpenses(limit: Int) = flowOf(expenses.takeLast(limit))
    override fun searchExpenses(query: String) = flowOf(
        expenses.filter { it.description?.contains(query) == true || it.category.contains(query) }
    )
    override fun getPendingSyncCount() = flowOf(expenses.count { it.syncStatus == SyncStatus.PENDING })
    override suspend fun getPendingSyncExpenses() = expenses.filter { it.syncStatus == SyncStatus.PENDING }
    override suspend fun markAsSynced(id: Long, remoteRowId: String?) {
        val index = expenses.indexOfFirst { it.id == id }
        if (index >= 0) expenses[index] = expenses[index].copy(syncStatus = SyncStatus.SYNCED)
    }
    override suspend fun markAsSyncError(id: Long) {
        val index = expenses.indexOfFirst { it.id == id }
        if (index >= 0) expenses[index] = expenses[index].copy(syncStatus = SyncStatus.ERROR)
    }
    override suspend fun deleteAllExpenses() { expenses.clear() }
}

class RegisterExpenseUseCaseTest {

    private lateinit var repository: FakeExpenseRepository
    private lateinit var useCase: RegisterExpenseUseCase

    @Before
    fun setup() {
        repository = FakeExpenseRepository()
        useCase = RegisterExpenseUseCase(repository)
    }

    @Test
    fun `register expense with valid data succeeds`() = runTest {
        val expense = Expense(
            amount = 150.0,
            paymentMethod = PaymentMethod.CASH,
            category = "Comida",
            description = "Almuerzo",
            date = LocalDate.now()
        )

        val result = useCase(expense)
        assertTrue(result.isSuccess)
        assertEquals(1L, result.getOrNull())
    }

    @Test
    fun `register expense with zero amount fails`() = runTest {
        val expense = Expense(
            amount = 0.0,
            paymentMethod = PaymentMethod.CASH,
            category = "Comida",
            date = LocalDate.now()
        )

        val result = useCase(expense)
        assertTrue(result.isFailure)
    }

    @Test
    fun `register expense with negative amount fails`() = runTest {
        val expense = Expense(
            amount = -50.0,
            paymentMethod = PaymentMethod.CASH,
            category = "Comida",
            date = LocalDate.now()
        )

        val result = useCase(expense)
        assertTrue(result.isFailure)
    }

    @Test
    fun `register expense with blank category fails`() = runTest {
        val expense = Expense(
            amount = 100.0,
            paymentMethod = PaymentMethod.CASH,
            category = "",
            date = LocalDate.now()
        )

        val result = useCase(expense)
        assertTrue(result.isFailure)
    }
}

class GetStatisticsUseCaseTest {

    private lateinit var repository: FakeExpenseRepository
    private lateinit var useCase: GetStatisticsUseCase

    @Before
    fun setup() {
        repository = FakeExpenseRepository()
        useCase = GetStatisticsUseCase(repository)
    }

    @Test
    fun `statistics with no expenses returns empty`() = runTest {
        val startDate = LocalDate.now().withDayOfMonth(1)
        val endDate = LocalDate.now()

        useCase(startDate, endDate).collect { stats ->
            assertEquals(0.0, stats.totalAmount, 0.01)
            assertEquals(0, stats.expenseCount)
        }
    }

    @Test
    fun `statistics calculates correct total`() = runTest {
        repository.insertExpense(
            Expense(amount = 100.0, paymentMethod = PaymentMethod.CASH, category = "Comida", date = LocalDate.now())
        )
        repository.insertExpense(
            Expense(amount = 200.0, paymentMethod = PaymentMethod.DEBIT_CARD, category = "Transporte", date = LocalDate.now())
        )

        val startDate = LocalDate.now().withDayOfMonth(1)
        val endDate = LocalDate.now()

        useCase(startDate, endDate).collect { stats ->
            assertEquals(300.0, stats.totalAmount, 0.01)
            assertEquals(2, stats.expenseCount)
        }
    }

    @Test
    fun `statistics identifies top category`() = runTest {
        repository.insertExpense(
            Expense(amount = 100.0, paymentMethod = PaymentMethod.CASH, category = "Comida", date = LocalDate.now())
        )
        repository.insertExpense(
            Expense(amount = 500.0, paymentMethod = PaymentMethod.CASH, category = "Tecnología", date = LocalDate.now())
        )

        val startDate = LocalDate.now().withDayOfMonth(1)
        val endDate = LocalDate.now()

        useCase(startDate, endDate).collect { stats ->
            assertEquals("Tecnología", stats.topCategory)
        }
    }
}
