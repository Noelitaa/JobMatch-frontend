package com.moviles.jobmatch.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.moviles.jobmatch.data.AuthSession
import com.moviles.jobmatch.data.repository.JobRepository
import com.moviles.jobmatch.ui.components.JobMatchBottomBar
import com.moviles.jobmatch.ui.screens.company.CompanyDashboardScreen
import com.moviles.jobmatch.ui.screens.company.CompanyProfileScreen
import com.moviles.jobmatch.ui.screens.company.CreateJobScreen
import com.moviles.jobmatch.ui.screens.job.ApplicationsScreen
import com.moviles.jobmatch.ui.screens.job.JobDetailScreen
import com.moviles.jobmatch.ui.screens.job.JobsScreen
import com.moviles.jobmatch.ui.screens.job.JobsViewModel
import com.moviles.jobmatch.ui.screens.login.LoginScreen
import com.moviles.jobmatch.ui.screens.register.RegisterScreen
import com.moviles.jobmatch.ui.screens.splash.SplashScreen
import com.moviles.jobmatch.ui.screens.profile.StudentProfileScreen

@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomBarRoute = when {
        currentRoute == AppDestinations.COMPANY_DASHBOARD -> AppDestinations.SEARCH_COMPANY
        else -> currentRoute
    }

    val showBottomBar = currentRoute != AppDestinations.SPLASH &&
            currentRoute != AppDestinations.LOGIN &&
            currentRoute != AppDestinations.REGISTER &&
            currentRoute?.startsWith(AppDestinations.JOB_DETAIL) != true &&
            currentRoute?.startsWith(AppDestinations.APPLICATIONS) != true

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                JobMatchBottomBar(
                    currentRoute = bottomBarRoute ?: AppDestinations.SEARCH_COMPANY,
                    onItemSelected = { route ->
                        val targetRoute = when (route) {
                            AppDestinations.SEARCH_COMPANY -> if (AuthSession.isCompany)
                                AppDestinations.COMPANY_DASHBOARD
                            else
                                AppDestinations.SEARCH_COMPANY
                            AppDestinations.PROFILE -> if (AuthSession.isCompany) {
                                val companyId = AuthSession.currentUser?.userId ?: ""
                                AppDestinations.companyProfileRoute(companyId)
                            } else route
                            else -> route
                        }
                        if (currentRoute != targetRoute) {
                            navController.navigate(targetRoute) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = AppDestinations.SPLASH,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(route = AppDestinations.SPLASH) {
                SplashScreen(navController = navController)
            }

            composable(route = AppDestinations.LOGIN) {
                LoginScreen(
                    onLoginSuccess = {
                        val destination = if (AuthSession.isCompany)
                            AppDestinations.COMPANY_DASHBOARD
                        else
                            AppDestinations.JOBS_EXPLORE
                        navController.navigate(destination) {
                            popUpTo(AppDestinations.LOGIN) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate(AppDestinations.REGISTER)
                    }
                )
            }

            composable(route = AppDestinations.REGISTER) {
                RegisterScreen(onNavigateToLogin = { navController.popBackStack() })
            }

            composable(route = AppDestinations.COMPANY_DASHBOARD) {
                val companyId = AuthSession.currentUser?.userId ?: ""
                CompanyDashboardScreen(
                    onJobClick = { jobId ->
                        navController.navigate(AppDestinations.jobDetailRoute(jobId))
                    },
                    onCreateJob = {
                        navController.navigate(AppDestinations.createJobRoute(companyId))
                    }
                )
            }

            composable(route = AppDestinations.SEARCH_COMPANY) {
                PlaceholderScreen("Inicio")
            }

            composable(route = AppDestinations.JOBS_EXPLORE) {
                if (AuthSession.isCompany) {
                    PlaceholderScreen("Trabajos")
                } else {
                    val apiService = com.moviles.jobmatch.data.remote.RetrofitClient.apiService
                    val repository = JobRepository(apiService)
                    val jobsViewModel: JobsViewModel = viewModel(
                        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                                return JobsViewModel(repository) as T
                            }
                        }
                    )
                    JobsScreen(
                        viewModel = jobsViewModel,
                        onJobClick = { jobId ->
                            navController.navigate(AppDestinations.jobDetailRoute(jobId))
                        }
                    )
                }
            }

            composable(route = AppDestinations.MY_JOBS) {
                PlaceholderScreen("Mis Trabajos")
            }

            composable(route = AppDestinations.ALERTS) {
                PlaceholderScreen("Alertas")
            }

            composable(route = AppDestinations.PROFILE) {
                StudentProfileScreen()
            }

            composable(route = AppDestinations.STUDENT_PROFILE) {
                StudentProfileScreen()
            }

            composable(
                route = AppDestinations.CREATE_JOB,
                arguments = listOf(navArgument("companyId") { type = NavType.StringType })
            ) { backStackEntry ->
                val companyId = backStackEntry.arguments?.getString("companyId").orEmpty()
                CreateJobScreen(
                    companyId = companyId,
                    onBackPressed = { navController.popBackStack() },
                    onJobCreated = { navController.popBackStack() }
                )
            }

            composable(
                route = "${AppDestinations.COMPANY_PROFILE}/{companyId}",
                arguments = listOf(navArgument("companyId") { type = NavType.StringType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("companyId").orEmpty()
                CompanyProfileScreen(
                    companyId = id,
                    onBackPressed = { navController.popBackStack() }
                )
            }

            composable(
                route = "${AppDestinations.JOB_DETAIL}/{jobId}",
                arguments = listOf(navArgument("jobId") { type = NavType.IntType })
            ) { backStackEntry ->
                val jobId = backStackEntry.arguments?.getInt("jobId") ?: 0
                JobDetailScreen(
                    jobId = jobId,
                    onBackPressed = { navController.popBackStack() },
                    onViewApplicants = { id, title ->
                        navController.navigate(AppDestinations.applicationsRoute(id, title))
                    },
                    onCompanyClick = { companyId ->
                        navController.navigate(AppDestinations.companyProfileRoute(companyId))
                    }
                )
            }

            composable(
                route = "${AppDestinations.APPLICATIONS}/{jobId}/{jobTitle}",
                arguments = listOf(
                    navArgument("jobId") { type = NavType.IntType },
                    navArgument("jobTitle") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val jobId = backStackEntry.arguments?.getInt("jobId") ?: 0
                val jobTitle = backStackEntry.arguments?.getString("jobTitle").orEmpty()
                ApplicationsScreen(
                    jobId = jobId,
                    jobTitle = jobTitle,
                    onBackPressed = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
fun PlaceholderScreen(title: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            "Pantalla de $title\n(En desarrollo por compañeros)",
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
