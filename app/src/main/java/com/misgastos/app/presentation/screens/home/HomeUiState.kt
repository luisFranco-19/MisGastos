package com.misgastos.app.presentation.screens.home

import com.misgastos.app.domain.model.Expense

data class CategoryHighlight(
    val name: String,
    val icon: String,
    val totalAmount: Double,
    val count: Int
)

data class HomeUiState(
    val monthlyTotal: Double = 0.0,
    val monthlyCount: Int = 0,
    val todayExpenses: List<Expense> = emptyList(),
    val yesterdayExpenses: List<Expense> = emptyList(),
    val recentExpenses: List<Expense> = emptyList(),
    val categoryHighlights: List<CategoryHighlight> = emptyList(),
    val budgetAmount: Double? = null,
    val currency: String = "$",
    val isLoading: Boolean = true
)
