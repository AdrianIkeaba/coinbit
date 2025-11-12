package com.ghostdev.coinbit.presentation.navigation

sealed class Screen(val route: String) {
    data object CoinList : Screen("coin_list")
    data object Favorites : Screen("favorites")
    data object CoinDetail : Screen("coin_detail/{coinId}") {
        fun createRoute(coinId: String) = "coin_detail/$coinId"
    }
}
