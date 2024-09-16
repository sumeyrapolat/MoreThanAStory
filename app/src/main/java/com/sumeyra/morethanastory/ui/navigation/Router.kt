package com.sumeyra.morethanastory.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sumeyra.morethanastory.model.repository.AuthRepository
import com.sumeyra.morethanastory.ui.screens.LoginScreen
import com.sumeyra.morethanastory.ui.screens.SignUpScreen
import com.sumeyra.morethanastory.viewmodel.AuthViewModel

@Composable
fun Router(navController: NavHostController, authRepository: AuthRepository) {
    val startDestination = if (authRepository.checkUserLoggedIn()) {
        "feed"
    } else {
        "signup"
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable("signup") {
            val signUpViewModel = hiltViewModel<AuthViewModel>()
            SignUpScreen(navController, signUpViewModel)
        }
        composable("login") {
            val signInViewModel = hiltViewModel<AuthViewModel>()
            LoginScreen(navController, signInViewModel)
        }
//
//        composable("feed") {
//            FeedScreen(navController)
//        }
//
//        composable("favorites") {
//            FavoriteScreen(navController)
//        }
//
//        composable(
//            "newsDetail/{newsItemId}",
//            arguments = listOf(
//                navArgument("newsItemId") { type = NavType.StringType }
//            )
//        ) { backStackEntry ->
//            val newsItemId = backStackEntry.arguments?.getString("newsItemId")
//            NewsDetailScreen(navController = navController, newsItemId = newsItemId ?: "")
//        }
//
//        composable("category") {
//            CategoryScreen(navController)
//        }
    }
}
