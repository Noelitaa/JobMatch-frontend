package com.moviles.jobmatch.ui.screens.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moviles.jobmatch.ui.components.CompanyCard
import com.moviles.jobmatch.ui.components.JobMatchTopBar
import com.moviles.jobmatch.data.AuthSession

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchCompanyScreen(
    onCompanySelected: (String) -> Unit,
    onBackPressed: () -> Unit = {},
    onJobCreated: () -> Unit = {},
    viewModel: SearchCompanyViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            JobMatchTopBar(
                title = "Buscar Empresa",
                onBackPressed = onBackPressed,
                showBackButton = true
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = uiState.searchText,
                    onValueChange = { viewModel.updateSearchText(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Escribe el nombre de la empresa...") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                )
                val isCompany = AuthSession.isCompany
                Spacer(modifier = Modifier.height(12.dp))
                if (isCompany) {
                    Button(
                        onClick = onJobCreated,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Crear Trabajo")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { viewModel.searchExactCompany { companyId ->
                        onCompanySelected(companyId)
                    } },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = uiState.searchText.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = "Buscar por nombre exacto",
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                uiState.errorMessage?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Text(
                text = "O selecciona una empresa de la lista:",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.filteredCompanies) { company ->
                    CompanyCard(
                        company = company,
                        onClick = { viewModel.selectCompany(company.id, onCompanySelected) }
                    )
                }

                if (uiState.filteredCompanies.isEmpty() && uiState.searchText.isNotBlank()) {
                    item {
                        Text(
                            text = "No hay empresas que coincidan con '${uiState.searchText}'",
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}