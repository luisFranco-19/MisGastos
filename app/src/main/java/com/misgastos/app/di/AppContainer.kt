package com.misgastos.app.di

import android.content.Context
import com.misgastos.app.data.local.PreferencesManager
import com.misgastos.app.data.local.database.MisGastosDatabase
import com.misgastos.app.data.repository.BudgetRepositoryImpl
import com.misgastos.app.data.repository.CategoryRepositoryImpl
import com.misgastos.app.data.repository.ExpenseRepositoryImpl
import com.misgastos.app.domain.repository.BudgetRepository
import com.misgastos.app.domain.repository.CategoryRepository
import com.misgastos.app.domain.repository.ExpenseRepository
import com.misgastos.app.domain.usecase.DeleteExpenseUseCase
import com.misgastos.app.domain.usecase.GetBudgetUseCase
import com.misgastos.app.domain.usecase.GetExpensesUseCase
import com.misgastos.app.domain.usecase.GetMonthlyTotalUseCase
import com.misgastos.app.domain.usecase.GetStatisticsUseCase
import com.misgastos.app.domain.usecase.RegisterExpenseUseCase
import com.misgastos.app.domain.usecase.SetBudgetUseCase
import com.misgastos.app.domain.usecase.UpdateExpenseUseCase

class AppContainer(context: Context) {

    // Database — lazy init, no se toca hasta primera consulta
    private val database by lazy { MisGastosDatabase.getInstance(context) }

    // DAOs
    private val expenseDao by lazy { database.expenseDao() }
    private val categoryDao by lazy { database.categoryDao() }
    private val budgetDao by lazy { database.budgetDao() }

    // Preferences
    val preferencesManager by lazy { PreferencesManager(context) }

    // Repositories
    val expenseRepository: ExpenseRepository by lazy { ExpenseRepositoryImpl(expenseDao) }
    val categoryRepository: CategoryRepository by lazy { CategoryRepositoryImpl(categoryDao) }
    val budgetRepository: BudgetRepository by lazy { BudgetRepositoryImpl(budgetDao) }

    // Use Cases
    val registerExpenseUseCase by lazy { RegisterExpenseUseCase(expenseRepository) }
    val updateExpenseUseCase by lazy { UpdateExpenseUseCase(expenseRepository) }
    val deleteExpenseUseCase by lazy { DeleteExpenseUseCase(expenseRepository) }
    val getExpensesUseCase by lazy { GetExpensesUseCase(expenseRepository) }
    val getMonthlyTotalUseCase by lazy { GetMonthlyTotalUseCase(expenseRepository) }
    val getStatisticsUseCase by lazy { GetStatisticsUseCase(expenseRepository) }
    val getBudgetUseCase by lazy { GetBudgetUseCase(budgetRepository) }
    val setBudgetUseCase by lazy { SetBudgetUseCase(budgetRepository) }
}
