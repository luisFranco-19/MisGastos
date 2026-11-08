package com.misgastos.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.misgastos.app.data.local.entity.BudgetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: BudgetEntity): Long

    @Query("SELECT * FROM budgets WHERE year = :year AND month = :month LIMIT 1")
    fun getBudget(year: Int, month: Int): Flow<BudgetEntity?>

    @Query("SELECT * FROM budgets WHERE year = :year AND month = :month LIMIT 1")
    suspend fun getBudgetOnce(year: Int, month: Int): BudgetEntity?

    @Query("DELETE FROM budgets")
    suspend fun deleteAllBudgets()
}
