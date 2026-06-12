package com.nextpeyk.mobileapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nextpeyk.mobileapp.ui.screens.delivery.DeliveryConfirmScreen
import com.nextpeyk.mobileapp.ui.screens.delivery.DeliveryDetailScreen
import com.nextpeyk.mobileapp.ui.screens.delivery.ReturnReasonScreen
import com.nextpeyk.mobileapp.ui.screens.home.HomeScreen
import com.nextpeyk.mobileapp.ui.screens.loading.LoadingScreen
import com.nextpeyk.mobileapp.ui.screens.login.AuthState
import com.nextpeyk.mobileapp.ui.screens.login.AuthViewModel
import com.nextpeyk.mobileapp.ui.screens.login.LoginScreen
import com.nextpeyk.mobileapp.ui.screens.parcel.ParcelDetailsScreen
import com.nextpeyk.mobileapp.ui.screens.stats.StatsDashboardScreen

@Composable
fun AppNavGraph() {
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Loading.route) {

        composable(Screen.Loading.route) {
            LoadingScreen(
                authState = authState,
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Loading.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Loading.route) { inclusive = true }
                    }
                },
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    // Show loading before Home after login
                    navController.navigate(Screen.Loading.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }

        composable(
            route = Screen.ParcelDetails.route,
            arguments = listOf(navArgument(Screen.ParcelDetails.ARG) { type = NavType.IntType }),
        ) { back ->
            ParcelDetailsScreen(
                shipmentIndex = back.arguments?.getInt(Screen.ParcelDetails.ARG) ?: 0,
                onBack = { navController.popBackStack() },
            )
        }

        composable(
            route = Screen.DeliveryDetail.route,
            arguments = listOf(navArgument(Screen.DeliveryDetail.ARG) { type = NavType.IntType }),
        ) { back ->
            val index = back.arguments?.getInt(Screen.DeliveryDetail.ARG) ?: 0
            DeliveryDetailScreen(
                shipmentIndex = index,
                onBack = { navController.popBackStack() },
                onDeliver = { navController.navigate(Screen.DeliveryConfirm.createRoute(index)) },
                onReturn = { navController.navigate(Screen.ReturnReason.createRoute(index)) },
            )
        }

        composable(
            route = Screen.DeliveryConfirm.route,
            arguments = listOf(navArgument(Screen.DeliveryConfirm.ARG) { type = NavType.IntType }),
        ) { back ->
            DeliveryConfirmScreen(
                shipmentIndex = back.arguments?.getInt(Screen.DeliveryConfirm.ARG) ?: 0,
                onBack = { navController.popBackStack() },
                onComplete = { navController.popBackStack(Screen.Home.route, inclusive = false) },
            )
        }

        composable(
            route = Screen.ReturnReason.route,
            arguments = listOf(navArgument(Screen.ReturnReason.ARG) { type = NavType.IntType }),
        ) { back ->
            ReturnReasonScreen(
                shipmentIndex = back.arguments?.getInt(Screen.ReturnReason.ARG) ?: 0,
                onBack = { navController.popBackStack() },
                onComplete = { navController.popBackStack(Screen.Home.route, inclusive = false) },
            )
        }

        composable(Screen.StatsDashboard.route) {
            StatsDashboardScreen(onBack = { navController.popBackStack() })
        }
    }
}
