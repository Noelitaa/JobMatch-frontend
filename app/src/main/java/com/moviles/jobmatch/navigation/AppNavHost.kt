package com.moviles.jobmatch.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.moviles.jobmatch.ui.screens.company.CompanyProfileScreen
import com.moviles.jobmatch.ui.screens.login.LoginScreen
import com.moviles.jobmatch.ui.screens.register.RegisterScreen
import com.moviles.jobmatch.ui.screens.splash.SplashScreen

@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppDestinations.SPLASH,
        modifier = modifier
    ) {
        composable(route = AppDestinations.SPLASH) {
            SplashScreen(navController = navController)
        }

        composable(route = AppDestinations.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(AppDestinations.MAIN_TABS) {
                        popUpTo(AppDestinations.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(AppDestinations.REGISTER)
                },
                onNavigateToForgotPassword = { /* TODO: implementar */ }
            )
        }

        composable(route = AppDestinations.REGISTER) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.navigate(AppDestinations.LOGIN)
                }
            )
        }

        composable(route = AppDestinations.MAIN_TABS) {
            MainTab(navController = navController)
        }


        composable(
            route = AppDestinations.COMPANY_PROFILE,
            arguments = listOf(
                navArgument("companyId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val companyId = backStackEntry.arguments?.getString("companyId").orEmpty()
            CompanyProfileScreen(
                companyId = companyId,
                onBackPressed = {
                    navController.popBackStack()
                }
            )
        }
    }
}