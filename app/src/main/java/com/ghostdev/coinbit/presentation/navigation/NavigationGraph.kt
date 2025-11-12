package com.ghostdev.coinbit.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ghostdev.coinbit.presentation.coindetail.CoinDetailScreen
import com.ghostdev.coinbit.presentation.coinlist.CoinListScreen
import com.ghostdev.coinbit.presentation.favorites.FavoritesScreen

@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.CoinList.route
    ) {
        composable(route = Screen.CoinList.route) {
            CoinListScreen(navController = navController)
        }
        
        composable(route = Screen.Favorites.route) {
            FavoritesScreen(navController = navController)
        }
        
        composable(
            route = Screen.CoinDetail.route,
            arguments = listOf(
                navArgument("coinId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val coinId = backStackEntry.arguments?.getString("coinId") ?: return@composable
            CoinDetailScreen(
                coinId = coinId,
                navController = navController
            )
        }
    }
}
