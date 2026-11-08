package com.misgastos.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.misgastos.app.presentation.screens.expenses.ExpenseDetailScreen
import com.misgastos.app.presentation.screens.expenses.ExpensesScreen
import com.misgastos.app.presentation.screens.home.HomeScreen
import com.misgastos.app.presentation.screens.onboarding.CurrencyOnboardingScreen
import com.misgastos.app.presentation.screens.register.RegisterExpenseScreen
import com.misgastos.app.presentation.screens.settings.SettingsScreen
import com.misgastos.app.presentation.screens.statistics.StatisticsScreen
import com.misgastos.app.presentation.screens.splash.SplashScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Home.route,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Splash.route) {
            SplashScreen { isInitialCurrencySet ->
                val destination = if (isInitialCurrencySet) {
                    Screen.Home.route
                } else {
                    Screen.CurrencyOnboarding.route
                }

                navController.navigate(destination) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }

        composable(Screen.CurrencyOnboarding.route) {
            CurrencyOnboardingScreen(
                onCurrencyConfigured = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.CurrencyOnboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onRegisterExpense = {
                    navController.navigate(Screen.RegisterExpense.route)
                },
                onExpenseClick = { expenseId ->
                    navController.navigate(Screen.ExpenseDetail.createRoute(expenseId))
                },
                onNavigateToExpenses = {
                    navController.navigate(Screen.Expenses.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        composable(Screen.Statistics.route) {
            StatisticsScreen()
        }

        composable(
            route = Screen.Expenses.route,
            deepLinks = listOf(
                navDeepLink { uriPattern = "misgastos://expenses" },
                navDeepLink { uriPattern = "misgastos://expenses_today" }
            )
        ) {
            ExpensesScreen(
                onExpenseClick = { expenseId ->
                    navController.navigate(Screen.ExpenseDetail.createRoute(expenseId))
                },
                onEditExpense = { expenseId ->
                    navController.navigate(Screen.EditExpense.createRoute(expenseId))
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen()
        }

        composable(
            route = Screen.RegisterExpense.route,
            deepLinks = listOf(
                navDeepLink { uriPattern = "misgastos://register" }
            )
        ) {
            RegisterExpenseScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.EditExpense.route,
            arguments = listOf(
                navArgument("expenseId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val expenseId = backStackEntry.arguments?.getLong("expenseId") ?: return@composable
            RegisterExpenseScreen(
                onNavigateBack = { navController.popBackStack() },
                expenseId = expenseId
            )
        }

        composable(
            route = Screen.ExpenseDetail.route,
            arguments = listOf(
                navArgument("expenseId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val expenseId = backStackEntry.arguments?.getLong("expenseId") ?: return@composable
            ExpenseDetailScreen(
                expenseId = expenseId,
                onNavigateBack = { navController.popBackStack() },
                onEditExpense = { id ->
                    navController.navigate(Screen.EditExpense.createRoute(id))
                }
            )
        }
    }
}
