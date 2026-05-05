package com.moviles.jobmatch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.moviles.jobmatch.ui.screens.company.CompanyProfileScreen
import com.moviles.jobmatch.ui.screens.search.SearchCompanyScreen
import com.moviles.jobmatch.ui.theme.JobMatchTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JobMatchTheme {
                // Estado para controlar qué pantalla se muestra
                var selectedCompanyId by remember { mutableStateOf<String?>(null) }

                if (selectedCompanyId != null) {
                    // Mostrar perfil de la empresa seleccionada
                    CompanyProfileScreen(
                        companyId = selectedCompanyId!!,
                        onBackPressed = { selectedCompanyId = null }
                    )
                } else {
                    // Mostrar pantalla de búsqueda de empresas
                    SearchCompanyScreen(
                        onCompanySelected = { companyId ->
                            selectedCompanyId = companyId
                        },
                        onBackPressed = { /* Salir de la app */ }
                    )
                }
            }
        }
    }
}