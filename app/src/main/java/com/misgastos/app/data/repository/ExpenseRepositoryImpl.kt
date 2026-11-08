package com.misgastos.app.data.repository

import com.misgastos.app.data.local.dao.ExpenseDao
import com.misgastos.app.data.local.entity.ExpenseEntity
import com.misgastos.app.domain.model.Expense
import com.misgastos.app.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset

class ExpenseRepositoryImpl(
    private val expenseDao: ExpenseDao
) : ExpenseRepository {

    override suspend fun insertExpense(expense: Expense): Long {
        val id = expenseDao.insertExpense(ExpenseEntity.fromDomain(expense))
        try {
            com.misgastos.app.widget.MisGastosWidgetReceiver.updateWidgets(com.misgastos.app.MisGastosApplication.appContext)
        } catch (_: Exception) {}
        return id
    }

    override suspend fun updateExpense(expense: Expense) {
        val updated = expense.copy(
            updatedAt = LocalDateTime.now(),
            syncStatus = com.misgastos.app.domain.model.SyncStatus.PENDING
        )
        expenseDao.updateExpense(ExpenseEntity.fromDomain(updated))
        try {
            com.misgastos.app.widget.MisGastosWidgetReceiver.updateWidgets(com.misgastos.app.MisGastosApplication.appContext)
        } catch (_: Exception) {}
    }

    override suspend fun deleteExpense(id: Long) {
        val now = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
        expenseDao.softDeleteExpense(id, now)
        try {
            com.misgastos.app.widget.MisGastosWidgetReceiver.updateWidgets(com.misgastos.app.MisGastosApplication.appContext)
        } catch (_: Exception) {}
    }

    override suspend fun getExpenseById(id: Long): Expense? {
        return expenseDao.getExpenseById(id)?.toDomain()
    }

    override fun getAllExpenses(): Flow<List<Expense>> {
        return expenseDao.getAllExpenses().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getExpensesByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<Expense>> {
        return expenseDao.getExpensesByDateRange(startDate, endDate).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getExpensesByCategory(category: String): Flow<List<Expense>> {
        return expenseDao.getExpensesByCategory(category).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getExpensesByPaymentMethod(paymentMethod: String): Flow<List<Expense>> {
        return expenseDao.getExpensesByPaymentMethod(paymentMethod).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getTotalByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<Double> {
        return expenseDao.getTotalByDateRange(startDate, endDate)
    }

    override fun getCountByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<Int> {
        return expenseDao.getCountByDateRange(startDate, endDate)
    }

    override fun getRecentExpenses(limit: Int): Flow<List<Expense>> {
        return expenseDao.getRecentExpenses(limit).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun searchExpenses(query: String): Flow<List<Expense>> {
        return expenseDao.searchExpenses(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getPendingSyncCount(): Flow<Int> {
        return expenseDao.getPendingSyncCount()
    }

    override suspend fun getPendingSyncExpenses(): List<Expense> {
        return expenseDao.getPendingSyncExpenses().map { it.toDomain() }
    }

    override suspend fun markAsSynced(id: Long, remoteRowId: String?) {
        expenseDao.markAsSynced(id, remoteRowId)
    }

    override suspend fun markAsSyncError(id: Long) {
        expenseDao.markAsSyncError(id)
    }

    override suspend fun deleteAllExpenses() {
        expenseDao.deleteAllExpenses()
        try {
            com.misgastos.app.widget.MisGastosWidgetReceiver.updateWidgets(com.misgastos.app.MisGastosApplication.appContext)
        } catch (_: Exception) {}
    }
}
