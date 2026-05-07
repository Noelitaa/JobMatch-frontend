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
import com.moviles.jobmatch.ui.screens.search.SearchCompanyScreen
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
                    navController.navigate(AppDestinations.SEARCH_COMPANY) {
                        popUpTo(AppDestinations.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(AppDestinations.REGISTER)
                },
                onNavigateToForgotPassword = { /* TODO: ruta recuperar contraseña */ }
            )
        }

        composable(route = AppDestinations.REGISTER) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.navigate(AppDestinations.LOGIN) {
                        popUpTo(AppDestinations.SPLASH) { inclusive = false }
                    }
                }
            )
        }

        composable(route = AppDestinations.SEARCH_COMPANY) {
            SearchCompanyScreen(
                onCompanySelected = { companyId ->
                    navController.navigate(AppDestinations.companyProfileRoute(companyId))
                },
                onBackPressed = {
                    android.os.Process.killProcess(android.os.Process.myPid())
                }
            )
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