package com.misgastos.app.domain.repository

import com.misgastos.app.domain.model.Expense
import com.misgastos.app.domain.model.SyncStatus
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface ExpenseRepository {
    suspend fun insertExpense(expense: Expense): Long
    suspend fun updateExpense(expense: Expense)
    suspend fun deleteExpense(id: Long)
    suspend fun getExpenseById(id: Long): Expense?
    fun getAllExpenses(): Flow<List<Expense>>
    fun getExpensesByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<Expense>>
    fun getExpensesByCategory(category: String): Flow<List<Expense>>
    fun getExpensesByPaymentMethod(paymentMethod: String): Flow<List<Expense>>
    fun getTotalByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<Double>
    fun getCountByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<Int>
    fun getRecentExpenses(limit: Int): Flow<List<Expense>>
    fun searchExpenses(query: String): Flow<List<Expense>>
    fun getPendingSyncCount(): Flow<Int>
    suspend fun getPendingSyncExpenses(): List<Expense>
    suspend fun markAsSynced(id: Long, remoteRowId: String?)
    suspend fun markAsSyncError(id: Long)
    suspend fun deleteAllExpenses()
}
