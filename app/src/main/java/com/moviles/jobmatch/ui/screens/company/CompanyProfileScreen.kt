package com.moviles.jobmatch.ui.screens.company

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moviles.jobmatch.data.AuthSession
import com.moviles.jobmatch.data.Company
import com.moviles.jobmatch.data.repository.AppContainer
import com.moviles.jobmatch.ui.components.*
import com.moviles.jobmatch.ui.screens.profile.DeleteAccountViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyProfileScreen(
    companyId: String,
    onBackPressed: () -> Unit = {},
    onSettingsPressed: () -> Unit = {},
    onAccountDeleted: () -> Unit = {},
    viewModel: CompanyProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val deleteViewModel: DeleteAccountViewModel = viewModel(
        factory = DeleteAccountViewModel.Factory(AppContainer.userRepository)
    )
    val deleteUiState by deleteViewModel.uiState.collectAsStateWithLifecycle()

    val isOwnProfile = companyId == AuthSession.currentUser?.userId
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(companyId) {
        viewModel.loadCompanyProfile(companyId)
    }

    LaunchedEffect(deleteUiState.isDeleted) {
        if (deleteUiState.isDeleted) onAccountDeleted()
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
                    isOwnProfile = isOwnProfile,
                    onDeleteClick = { showDeleteDialog = true },
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

    if (showDeleteDialog) {
        CompanyDeleteAccountDialog(
            isLoading = deleteUiState.isLoading,
            errorMessage = deleteUiState.errorMessage,
            onConfirm = { password ->
                deleteViewModel.deleteAccount(password)
            },
            onDismiss = {
                showDeleteDialog = false
                deleteViewModel.clearError()
            }
        )
    }
}

@Composable
fun CompanyProfileContent(
    company: Company,
    modifier: Modifier = Modifier,
    isOwnProfile: Boolean = false,
    onDeleteClick: () -> Unit = {}
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

        if (isOwnProfile) {
            OutlinedButton(
                onClick = onDeleteClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFE53935)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFE53935))
            ) {
                Text(
                    text = "Eliminar cuenta",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun CompanyDeleteAccountDialog(
    isLoading: Boolean,
    errorMessage: String?,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        title = { Text("Eliminar cuenta", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Esta acción es permanente y no se puede deshacer. " +
                    "Ingresa tu contraseña para confirmar.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF5A6A7A)
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    singleLine = true,
                    visualTransformation = if (passwordVisible)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible)
                                    Icons.Default.VisibilityOff
                                else
                                    Icons.Default.Visibility,
                                contentDescription = null
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally).size(24.dp),
                        color = Color(0xFFE53935)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(password) },
                enabled = password.isNotBlank() && !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
            ) {
                Text("Eliminar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isLoading) {
                Text("Cancelar")
            }
        }
    )
}

