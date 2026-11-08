package com.misgastos.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.misgastos.app.data.local.entity.ExpenseEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface ExpenseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity): Long

    @Update
    suspend fun updateExpense(expense: ExpenseEntity)

    @Query("UPDATE expenses SET isDeleted = 1, syncStatus = 'PENDING', updatedAt = :updatedAt WHERE id = :id")
    suspend fun softDeleteExpense(id: Long, updatedAt: Long)

    @Query("DELETE FROM expenses WHERE id = :id")
    suspend fun hardDeleteExpense(id: Long)

    @Query("SELECT * FROM expenses WHERE id = :id")
    suspend fun getExpenseById(id: Long): ExpenseEntity?

    @Query("SELECT * FROM expenses WHERE isDeleted = 0 ORDER BY date DESC, createdAt DESC")
    fun getAllExpenses(): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE isDeleted = 0 AND date = :date ORDER BY createdAt DESC")
    fun getExpensesByDate(date: LocalDate): Flow<List<ExpenseEntity>>

    @Query("""
        SELECT * FROM expenses 
        WHERE isDeleted = 0 
        AND date >= :startDate 
        AND date <= :endDate 
        ORDER BY date DESC, createdAt DESC
    """)
    fun getExpensesByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<ExpenseEntity>>

    @Query("""
        SELECT * FROM expenses 
        WHERE isDeleted = 0 
        AND category = :category 
        ORDER BY date DESC, createdAt DESC
    """)
    fun getExpensesByCategory(category: String): Flow<List<ExpenseEntity>>

    @Query("""
        SELECT * FROM expenses 
        WHERE isDeleted = 0 
        AND paymentMethod = :paymentMethod 
        ORDER BY date DESC, createdAt DESC
    """)
    fun getExpensesByPaymentMethod(paymentMethod: String): Flow<List<ExpenseEntity>>

    @Query("""
        SELECT COALESCE(SUM(amount), 0) FROM expenses 
        WHERE isDeleted = 0 
        AND date >= :startDate 
        AND date <= :endDate
    """)
    fun getTotalByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<Double>

    @Query("""
        SELECT COUNT(*) FROM expenses 
        WHERE isDeleted = 0 
        AND date >= :startDate 
        AND date <= :endDate
    """)
    fun getCountByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<Int>

    @Query("SELECT * FROM expenses WHERE syncStatus = 'PENDING' OR syncStatus = 'ERROR'")
    suspend fun getPendingSyncExpenses(): List<ExpenseEntity>

    @Query("SELECT COUNT(*) FROM expenses WHERE syncStatus = 'PENDING' OR syncStatus = 'ERROR'")
    fun getPendingSyncCount(): Flow<Int>

    @Query("UPDATE expenses SET syncStatus = 'SYNCED', remoteRowId = :remoteRowId WHERE id = :id")
    suspend fun markAsSynced(id: Long, remoteRowId: String?)

    @Query("UPDATE expenses SET syncStatus = 'ERROR' WHERE id = :id")
    suspend fun markAsSyncError(id: Long)

    @Query("""
        SELECT * FROM expenses 
        WHERE isDeleted = 0 
        ORDER BY date DESC, createdAt DESC 
        LIMIT :limit
    """)
    fun getRecentExpenses(limit: Int): Flow<List<ExpenseEntity>>

    @Query("""
        SELECT * FROM expenses 
        WHERE isDeleted = 0 
        AND (category LIKE '%' || :query || '%' 
             OR description LIKE '%' || :query || '%' 
             OR paymentMethod LIKE '%' || :query || '%')
        ORDER BY date DESC, createdAt DESC
    """)
    fun searchExpenses(query: String): Flow<List<ExpenseEntity>>

    @Query("DELETE FROM expenses")
    suspend fun deleteAllExpenses()
}
