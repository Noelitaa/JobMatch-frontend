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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.moviles.jobmatch.data.AuthSession
import com.moviles.jobmatch.data.repository.AppContainer
import com.moviles.jobmatch.ui.components.JobMatchBottomBar
import com.moviles.jobmatch.ui.screens.company.CompanyDashboardScreen
import com.moviles.jobmatch.ui.screens.company.CompanyJobsScreen
import com.moviles.jobmatch.ui.screens.company.CompanyProfileScreen
import com.moviles.jobmatch.ui.screens.company.CreateJobScreen
import com.moviles.jobmatch.ui.screens.job.ApplicationDetailScreen
import com.moviles.jobmatch.ui.screens.job.ApplicationsScreen
import com.moviles.jobmatch.ui.screens.job.EditJobScreen
import com.moviles.jobmatch.ui.screens.profile.StudentPublicProfileScreen
import com.moviles.jobmatch.ui.screens.job.JobDetailScreen
import com.moviles.jobmatch.ui.screens.job.JobsScreen
import com.moviles.jobmatch.ui.screens.job.JobsViewModel
import com.moviles.jobmatch.ui.screens.job.StudentDashboardScreen
import com.moviles.jobmatch.ui.screens.login.LoginScreen
import com.moviles.jobmatch.ui.screens.register.RegisterScreen
import com.moviles.jobmatch.ui.screens.splash.SplashScreen
import com.moviles.jobmatch.ui.screens.profile.StudentProfileScreen
import com.moviles.jobmatch.ui.screens.availability.AvailabilityScreen
import com.moviles.jobmatch.ui.screens.payment.MakePaymentScreen
import com.moviles.jobmatch.ui.screens.payment.PaymentHistoryScreen

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
            currentRoute != AppDestinations.PAYMENT_HISTORY &&
            currentRoute?.startsWith(AppDestinations.JOB_DETAIL) != true &&
            currentRoute?.startsWith(AppDestinations.EDIT_JOB) != true &&
            currentRoute?.startsWith(AppDestinations.APPLICATIONS) != true &&
            currentRoute?.startsWith(AppDestinations.STUDENT_PUBLIC_PROFILE) != true &&
            currentRoute?.startsWith(AppDestinations.APPLICATION_DETAIL) != true

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
                            AppDestinations.SEARCH_COMPANY
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
                if (AuthSession.isCompany) {
                    PlaceholderScreen("Inicio")
                } else {
                    StudentDashboardScreen(
                        onJobClick = { jobId ->
                            navController.navigate(AppDestinations.jobDetailRoute(jobId))
                        }
                    )
                }
            }

            composable(route = AppDestinations.JOBS_EXPLORE) {
                if (AuthSession.isCompany) {
                    val companyId = AuthSession.currentUser?.userId ?: ""
                    CompanyJobsScreen(
                        onCreateJob = {
                            navController.navigate(AppDestinations.createJobRoute(companyId))
                        },
                        onEditJob = { id ->
                            navController.navigate(AppDestinations.editJobRoute(id))
                        },
                        onJobClick = { id, title ->
                            navController.navigate(AppDestinations.applicationsRoute(id, title))
                        }
                    )
                } else {
                    val jobsViewModel: JobsViewModel = viewModel(
                        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                                return JobsViewModel(AppContainer.jobRepository) as T
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
                    onBackPressed = { navController.popBackStack() },
                    onLogout = {
                        AuthSession.clear()
                        navController.navigate(AppDestinations.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onPaymentHistory = { navController.navigate(AppDestinations.PAYMENT_HISTORY) },
                    onAccountDeleted = {
                        navController.navigate(AppDestinations.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onMakePayment = { jobId, studentId, jobTitle, contractNumber, amount ->
                        navController.navigate(
                            AppDestinations.makePaymentRoute(
                                jobId, studentId, jobTitle, contractNumber, amount
                            )
                        )
                    }
                )
            }

            composable(
                route = "${AppDestinations.JOB_DETAIL}/{jobId}",
                arguments = listOf(navArgument("jobId") { type = NavType.IntType })
            ) { backStackEntry ->
                val jobId = backStackEntry.arguments?.getInt("jobId") ?: 0
                val refreshKey by backStackEntry.savedStateHandle
                    .getStateFlow("refresh_key", 0)
                    .collectAsStateWithLifecycle()
                JobDetailScreen(
                    jobId = jobId,
                    refreshKey = refreshKey,
                    onBackPressed = { navController.popBackStack() },
                    onViewApplicants = { id, title ->
                        navController.navigate(AppDestinations.applicationsRoute(id, title))
                    },
                    onEditJob = { id ->
                        navController.navigate(AppDestinations.editJobRoute(id))
                    },
                    onCompanyClick = { companyId ->
                        navController.navigate(AppDestinations.companyProfileRoute(companyId))
                    }
                )
            }

            composable(
                route = "${AppDestinations.EDIT_JOB}/{jobId}",
                arguments = listOf(navArgument("jobId") { type = NavType.IntType })
            ) { backStackEntry ->
                val jobId = backStackEntry.arguments?.getInt("jobId") ?: 0
                EditJobScreen(
                    jobId = jobId,
                    onBackPressed = { navController.popBackStack() },
                    onJobUpdated = {
                        val prev = navController.previousBackStackEntry?.savedStateHandle
                        prev?.set("refresh_key", (prev.get<Int>("refresh_key") ?: 0) + 1)
                        navController.popBackStack()
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
                    onBackPressed = { navController.popBackStack() },
                    onStudentClick = { studentId ->
                        navController.navigate(AppDestinations.studentPublicProfileRoute(studentId))
                    },
                    onViewApplicationDetail = { application, jId ->
                        navController.navigate(
                            AppDestinations.applicationDetailRoute(
                                applicationId = application.idApplication,
                                jobId = jId,
                                jobTitle = application.jobTitle,
                                studentId = application.idStudent,
                                studentName = application.studentName,
                                studentEmail = application.studentEmail,
                                status = application.status,
                                createdAt = application.createdAt
                            )
                        )
                    }
                )
            }

            composable(
                route = "${AppDestinations.MAKE_PAYMENT}/{jobId}/{studentId}/{jobTitle}/{contractNumber}/{amount}",
                arguments = listOf(
                    navArgument("jobId")          { type = NavType.IntType },
                    navArgument("studentId")      { type = NavType.StringType },
                    navArgument("jobTitle")       { type = NavType.StringType },
                    navArgument("contractNumber") { type = NavType.StringType },
                    navArgument("amount")         { type = NavType.FloatType }
                )
            ) { backStackEntry ->
                val jobId          = backStackEntry.arguments?.getInt("jobId") ?: 0
                val studentId      = backStackEntry.arguments?.getString("studentId").orEmpty()
                val jobTitle       = backStackEntry.arguments?.getString("jobTitle").orEmpty()
                val contractNumber = backStackEntry.arguments?.getString("contractNumber").orEmpty()
                val amount         = backStackEntry.arguments?.getFloat("amount")?.toDouble() ?: 0.0
                MakePaymentScreen(
                    jobId          = jobId,
                    studentId      = studentId,
                    jobTitle       = jobTitle,
                    contractNumber = contractNumber,
                    amount         = amount,
                    onBackPressed  = { navController.popBackStack() },
                    onPaymentSuccess = { navController.popBackStack() }
                )
            }

            composable(
                route = "${AppDestinations.APPLICATION_DETAIL}/{applicationId}/{jobId}/{jobTitle}/{studentId}/{studentName}/{studentEmail}/{status}/{createdAt}",
                arguments = listOf(
                    navArgument("applicationId") { type = NavType.IntType },
                    navArgument("jobId") { type = NavType.IntType },
                    navArgument("jobTitle") { type = NavType.StringType },
                    navArgument("studentId") { type = NavType.StringType },
                    navArgument("studentName") { type = NavType.StringType },
                    navArgument("studentEmail") { type = NavType.StringType },
                    navArgument("status") { type = NavType.StringType },
                    navArgument("createdAt") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val args = backStackEntry.arguments ?: return@composable
                ApplicationDetailScreen(
                    applicationId = args.getInt("applicationId"),
                    jobId = args.getInt("jobId"),
                    jobTitle = args.getString("jobTitle").orEmpty(),
                    studentId = args.getString("studentId").orEmpty(),
                    studentName = args.getString("studentName").orEmpty(),
                    studentEmail = args.getString("studentEmail").orEmpty(),
                    status = args.getString("status").orEmpty(),
                    createdAt = args.getString("createdAt").orEmpty(),
                    onBackPressed = { navController.popBackStack() },
                    onViewStudentProfile = { studentId ->
                        navController.navigate(AppDestinations.studentPublicProfileRoute(studentId))
                    }
                )
            }

            composable(
                route = "${AppDestinations.STUDENT_PUBLIC_PROFILE}/{studentId}",
                arguments = listOf(navArgument("studentId") { type = NavType.StringType })
            ) { backStackEntry ->
                val studentId = backStackEntry.arguments?.getString("studentId").orEmpty()
                StudentPublicProfileScreen(
                    studentId = studentId,
                    onBackPressed = { navController.popBackStack() }
                )
            }

            composable(route = AppDestinations.PROFILE) {
                StudentProfileScreen(
                    onLogout = {
                        AuthSession.clear()
                        navController.navigate(AppDestinations.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onEditAvailability = {
                        navController.navigate(AppDestinations.AVAILABILITY)
                    },
                    onPaymentHistory = {
                        navController.navigate(AppDestinations.PAYMENT_HISTORY)
                    },
                    onAccountDeleted = {
                        navController.navigate(AppDestinations.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable(route = AppDestinations.STUDENT_PROFILE) {
                StudentProfileScreen(
                    onLogout = {
                        AuthSession.clear()
                        navController.navigate(AppDestinations.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onEditAvailability = {
                        navController.navigate(AppDestinations.AVAILABILITY)
                    },
                    onPaymentHistory = {
                        navController.navigate(AppDestinations.PAYMENT_HISTORY)
                    },
                    onAccountDeleted = {
                        navController.navigate(AppDestinations.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable(route = AppDestinations.AVAILABILITY) {
                AvailabilityScreen(
                    onBackPressed = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() }
                )
            }

            composable(route = AppDestinations.PAYMENT_HISTORY) {
                PaymentHistoryScreen(
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