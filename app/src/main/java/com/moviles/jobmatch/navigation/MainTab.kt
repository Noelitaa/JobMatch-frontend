package com.moviles.jobmatch.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.moviles.jobmatch.ui.components.JobMatchBottomBar
import com.moviles.jobmatch.ui.screens.job.JobsScreen
import com.moviles.jobmatch.ui.screens.search.SearchCompanyScreen

@Composable
fun MainTab(navController: NavHostController) {
    // 1. Estado local para controlar qué pestaña ver.
    // Empezamos en "inicio" para que se vean las empresas de tus compañeros primero.
    var currentTab by remember { mutableStateOf("inicio") }

    Scaffold(
        bottomBar = {
            // Aquí usamos el componente que ya existe en tu proyecto
            JobMatchBottomBar(
                currentRoute = currentTab,
                onItemSelected = { route ->
                    // Cuando se toca un botón del bottom bar, cambiamos el estado
                    currentTab = route
                }
            )
        }
    ) { paddingValues ->
        // 2. El Box ocupa el espacio restante y respeta el padding del BottomBar
        Box(modifier = Modifier.padding(paddingValues)) {
            when (currentTab) {
                "inicio" -> {
                    // Pantalla de tus compañeros (Búsqueda/Listado de empresas)
                    SearchCompanyScreen(
                        onCompanySelected = { companyId ->
                            navController.navigate(AppDestinations.companyProfileRoute(companyId))
                        },
                        onBackPressed = { /* Manejar si es necesario */ }
                    )
                }
                "explorar" -> {
                    // TU PANTALLA de trabajos con los datos quemados
                    JobsScreen(
                        onJobClick = { jobId ->
                            navController.navigate(AppDestinations.companyProfileRoute(jobId.toString()))
                        }
                    )
                }
                // Aquí puedes agregar "mis_trabajos", "perfil", etc., cuando estén listas
                else -> {
                    // Pantalla por defecto o vacía para las pestañas no implementadas aún
                }
            }
        }
    }
}