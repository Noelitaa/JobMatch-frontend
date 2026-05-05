package com.moviles.jobmatch.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.moviles.jobmatch.ui.screens.company.CompanyProfileScreen
import com.moviles.jobmatch.ui.screens.search.SearchCompanyScreen

@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppDestinations.SEARCH_COMPANY,
        modifier = modifier
    ) {
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