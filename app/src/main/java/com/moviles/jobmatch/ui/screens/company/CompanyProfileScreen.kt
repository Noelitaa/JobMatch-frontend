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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moviles.jobmatch.data.Company
import com.moviles.jobmatch.ui.components.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyProfileScreen(
    companyId: String,
    onBackPressed: () -> Unit = {},
    onSettingsPressed: () -> Unit = {},
    viewModel: CompanyProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(companyId) {
        viewModel.loadCompanyProfile(companyId)
    }

    Scaffold(
        topBar = {
            JobMatchTopBar(
                title = "Perfil de Empresa",
                onBackPressed = onBackPressed,
                onSettingsPressed = onSettingsPressed
            )
        }
    ) { paddingValues ->
        when (uiState) {
            is CompanyUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is CompanyUiState.Success -> {
                val company = (uiState as CompanyUiState.Success).company
                CompanyProfileContent(
                    company = company,
                    modifier = Modifier.padding(paddingValues)
                )
            }

            is CompanyUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = (uiState as CompanyUiState.Error).message,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.loadCompanyProfile(companyId) }) {
                            Text("Reintentar")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CompanyProfileContent(
    company: Company,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
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
                containerColor = MaterialTheme.colorScheme.surface
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

