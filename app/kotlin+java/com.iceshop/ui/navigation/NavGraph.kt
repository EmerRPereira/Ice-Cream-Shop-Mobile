package com.iceshop.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.iceshop.ui.screens.*

// ⬇️ DEFINIÇÃO DA CLASSE SCREEN
sealed class Screen(val route: String) {
    object Menu : Screen("menu")
    object Cart : Screen("cart")
    object OrderHistory : Screen("order_history")
    object OrderSuccess : Screen("order_success")
}

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Menu.route
    ) {
        composable(Screen.Menu.route) {
            MenuScreen(navController = navController)
        }
        composable(Screen.Cart.route) {
            CartScreen(navController = navController)
        }
        composable(Screen.OrderHistory.route) {
            OrderHistoryScreen(navController = navController)
        }
        composable(Screen.OrderSuccess.route) {
            OrderSuccessScreen(navController = navController)
        }
    }
}
