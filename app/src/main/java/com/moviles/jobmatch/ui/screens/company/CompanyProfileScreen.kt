package com.moviles.jobmatch.ui.screens.company

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moviles.jobmatch.data.Company
import com.moviles.jobmatch.data.MockCompanyData
import com.moviles.jobmatch.ui.components.*
import com.moviles.jobmatch.ui.theme.JobMatchTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyProfileScreen(
    company: Company = MockCompanyData.mockCompany,
    onBackPressed: () -> Unit = {},
    onSettingsPressed: () -> Unit = {}
) {
    var selectedRoute by remember { mutableStateOf("perfil") }

    Scaffold(
        topBar = {
            JobMatchTopBar(
                title = "Perfil de Empresa",
                onBackPressed = onBackPressed,
                onSettingsPressed = onSettingsPressed
            )
        },
        bottomBar = {
            JobMatchBottomBar(
                currentRoute = selectedRoute,
                onItemSelected = { route ->
                    selectedRoute = route
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.background)
        ) {
            ProfileHeader(
                initials = company.companyName.take(2).uppercase(),
                name = company.companyName,
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Información de Contacto",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CompanyInfoRow(
                        icon = Icons.Default.Email,
                        label = "Correo",
                        value = company.email
                    )
                    company.phone?.let {
                        CompanyInfoRow(
                            icon = Icons.Default.Phone,
                            label = "Teléfono",
                            value = it
                        )
                    }
                }
            }

            if (!company.description.isNullOrEmpty()) {
                CompanyDetailCard(
                    title = "Sobre la empresa",
                    content = company.description,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCompanyProfileScreen() {
    JobMatchTheme {
        CompanyProfileScreen()
    }
}