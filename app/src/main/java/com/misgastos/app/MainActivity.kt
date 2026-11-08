package com.misgastos.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.misgastos.app.presentation.navigation.BottomNavBar
import com.misgastos.app.presentation.navigation.NavGraph
import com.misgastos.app.presentation.navigation.Screen
import com.misgastos.app.presentation.theme.MisGastosTheme
import com.misgastos.app.presentation.theme.ThemeMode

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as MisGastosApplication
        val preferencesManager = app.container.preferencesManager

        setContent {
            // Lee el tema persistido en DataStore — soporta LIGHT, DARK y SYSTEM
            val themeMode by preferencesManager.themeMode.collectAsStateWithLifecycle(
                initialValue = ThemeMode.SYSTEM
            )

            val startDestination = when (intent?.data?.toString()) {
                "misgastos://register" -> Screen.RegisterExpense.route
                "misgastos://expenses", "misgastos://expenses_today" -> Screen.Expenses.route
                "misgastos://statistics" -> Screen.Statistics.route
                else -> Screen.Splash.route
            }

            MisGastosTheme(themeMode = themeMode) {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val showBottomBar = currentRoute in listOf(
                    Screen.Home.route,
                    Screen.Statistics.route,
                    Screen.Expenses.route,
                    Screen.Settings.route
                )

                Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            BottomNavBar(navController = navController)
                        }
                    }
                ) { paddingValues ->
                    NavGraph(
                        navController = navController,
                        startDestination = startDestination,
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            }
        }
    }
}
