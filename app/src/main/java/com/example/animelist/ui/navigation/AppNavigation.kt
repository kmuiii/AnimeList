package com.example.animelist.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.animelist.ui.detail.AnimeDetailScreen
import com.example.animelist.ui.home.HomeScreen

// sealed class untuk mendefinisikan rute/tujuan navigasi dalam aplikasi
sealed class Screen(val route: String) {
    // rute layar beranda
    object Home : Screen("home")
    // rute layar detail anime dengan parameter animeId
    object Detail : Screen("detail/{animeId}") {
        fun createRoute(animeId: String) = "detail/$animeId"
    }
}

// composable utama pengatur navigasi antar layar menggunakan NavHost Jetpack Compose
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onAnimeClick = { animeId ->
                    navController.navigate(Screen.Detail.createRoute(animeId))
                }
            )
        }

        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument("animeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val animeId = backStackEntry.arguments?.getString("animeId") ?: ""
            AnimeDetailScreen(
                animeId = animeId,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
