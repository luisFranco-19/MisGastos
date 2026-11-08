package com.misgastos.app.presentation.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object CurrencyOnboarding : Screen("currency_onboarding")
    data object Home : Screen("home")
    data object Statistics : Screen("statistics")
    data object Expenses : Screen("expenses")
    data object Settings : Screen("settings")
    data object RegisterExpense : Screen("register_expense")
    data object EditExpense : Screen("edit_expense/{expenseId}") {
        fun createRoute(expenseId: Long) = "edit_expense/$expenseId"
    }
    data object ExpenseDetail : Screen("expense_detail/{expenseId}") {
        fun createRoute(expenseId: Long) = "expense_detail/$expenseId"
    }
}
