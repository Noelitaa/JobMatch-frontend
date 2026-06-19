package com.moviles.jobmatch.ui.screens.company

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moviles.jobmatch.data.AuthSession
import com.moviles.jobmatch.data.Company
import com.moviles.jobmatch.data.remote.model.ContractDetailResponse
import com.moviles.jobmatch.data.remote.model.ContractListResponse
import com.moviles.jobmatch.data.repository.AppContainer
import com.moviles.jobmatch.ui.components.*
import com.moviles.jobmatch.ui.components.RatingDialog
import com.moviles.jobmatch.ui.screens.profile.DeleteAccountViewModel
import com.moviles.jobmatch.ui.theme.DarkBlue
import com.moviles.jobmatch.ui.utils.formatApplicationDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyProfileScreen(
    companyId: String,
    onBackPressed: () -> Unit = {},
    onSettingsPressed: () -> Unit = {},
    onPaymentHistory: () -> Unit = {},
    onAccountDeleted: () -> Unit = {},
    onMakePayment: (Int, String, String, String, Double) -> Unit = { _, _, _, _, _ -> },
    viewModel: CompanyProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val deleteViewModel: DeleteAccountViewModel = viewModel(
        factory = DeleteAccountViewModel.Factory(AppContainer.userRepository)
    )
    val deleteUiState by deleteViewModel.uiState.collectAsStateWithLifecycle()

    val isOwnProfile = companyId == AuthSession.currentUser?.userId
    var showDeleteDialog by remember { mutableStateOf(false) }
    var ratingContractId by remember { mutableStateOf<Int?>(null) }

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
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.errorMessage != null -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = uiState.errorMessage!!,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.loadCompanyProfile(companyId) }) {
                            Text("Reintentar")
                        }
                    }
                }
            }

            uiState.company != null -> {
                CompanyProfileContent(
                    company = uiState.company!!,
                    uiState = uiState,
                    isOwnProfile = isOwnProfile,
                    onPaymentHistory = onPaymentHistory,
                    onDeleteClick = { showDeleteDialog = true },
                    onExpandContract = { viewModel.loadContractDetail(it) },
                    onMakePayment = onMakePayment,
                    onRate = { ratingContractId = it },
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }

    if (showDeleteDialog) {
        DeleteAccountDialog(
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

    val activeRatingId = ratingContractId
    if (activeRatingId != null) {
        LaunchedEffect(activeRatingId in uiState.ratingSuccessIds) {
            if (activeRatingId in uiState.ratingSuccessIds) {
                ratingContractId = null
            }
        }
        RatingDialog(
            title = "Calificar al estudiante",
            description = "¿Cómo fue la participación del estudiante?",
            isLoading = activeRatingId in uiState.ratingLoadingIds,
            errorMessage = uiState.ratingErrors[activeRatingId],
            onConfirm = { stars, comment ->
                viewModel.submitRating(activeRatingId, stars, comment)
            },
            onDismiss = {
                ratingContractId = null
                viewModel.clearRatingError(activeRatingId)
            }
        )
    }
}

@Composable
fun CompanyProfileContent(
    company: Company,
    uiState: CompanyProfileUiState,
    modifier: Modifier = Modifier,
    isOwnProfile: Boolean = false,
    onPaymentHistory: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onExpandContract: (Int) -> Unit = {},
    onMakePayment: (Int, String, String, String, Double) -> Unit = { _, _, _, _, _ -> },
    onRate: (Int) -> Unit = {}
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

        // --- Contratos Gestionados ---
        if (isOwnProfile) {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                SectionHeader(title = "Contratos Gestionados")
                Spacer(modifier = Modifier.height(8.dp))
                when {
                    uiState.isLoadingContracts -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = DarkBlue,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                    uiState.contractsErrorMessage != null -> {
                        Text(
                            text = uiState.contractsErrorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    uiState.contracts.isEmpty() -> {
                        Text(
                            text = "Sin contratos registrados",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF9AA5B4)
                        )
                    }
                    else -> {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            uiState.contracts.forEach { contract ->
                                CompanyContractCard(
                                    contract = contract,
                                    detail = uiState.contractDetails[contract.idContract],
                                    isLoadingDetail = contract.idContract in uiState.loadingContractIds,
                                    detailError = uiState.contractDetailErrors[contract.idContract],
                                    onExpand = { onExpandContract(contract.idContract) },
                                    onMakePayment = onMakePayment
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            val activeContracts = uiState.contracts.filter { it.status.equals("active", ignoreCase = true) }
            if (activeContracts.isNotEmpty()) {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    SectionHeader(title = "Calificaciones")
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            activeContracts.forEachIndexed { index, contract ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = contract.jobTitle,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF1A1A1A)
                                        )
                                        Text(
                                            text = contract.studentName,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF5A6A7A)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    if (contract.idContract in uiState.ratingSuccessIds) {
                                        Text(
                                            text = "Ya calificado",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF2E7D32),
                                            fontWeight = FontWeight.Medium
                                        )
                                    } else {
                                        Surface(
                                            shape = RoundedCornerShape(20.dp),
                                            color = Color(0xFFFFC107),
                                            modifier = Modifier.clickable { onRate(contract.idContract) }
                                        ) {
                                            Text(
                                                text = "Calificar",
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Medium,
                                                color = Color(0xFF1A1A1A),
                                                fontSize = 11.sp
                                            )
                                        }
                                    }
                                }
                                if (index < activeContracts.lastIndex) {
                                    HorizontalDivider(color = Color(0xFFF0F0F0))
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        if (isOwnProfile) {
            OutlinedButton(
                onClick = onPaymentHistory,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, DarkBlue),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkBlue)
            ) {
                Icon(
                    Icons.Default.Receipt,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Historial de Pagos",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

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
private fun CompanyContractCard(
    contract: ContractListResponse,
    detail: ContractDetailResponse?,
    isLoadingDetail: Boolean,
    detailError: String?,
    onExpand: () -> Unit,
    onMakePayment: (Int, String, String, String, Double) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val chevronRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "chevron"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        expanded = !expanded
                        if (expanded) onExpand()
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = contract.jobTitle,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1A1A1A)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = contract.studentName,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF5A6A7A)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ContractStatusBadge(contract.status)
                        Text(
                            text = formatApplicationDate(contract.createdAt),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF9AA5B4)
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Contraer" else "Expandir",
                    modifier = Modifier
                        .size(24.dp)
                        .rotate(chevronRotation),
                    tint = Color(0xFF9AA5B4)
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = Color(0xFFF0F0F0)
                    )
                    when {
                        isLoadingDetail -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = DarkBlue,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        detailError != null -> {
                            Text(
                                text = detailError,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        detail != null -> {
                            ContractDetailSection(
                                detail = detail,
                                onMakePayment = onMakePayment
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ContractDetailSection(
    detail: ContractDetailResponse,
    onMakePayment: (Int, String, String, String, Double) -> Unit
) {
    val data = detail.parsedContractData
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        if (!data?.workType.isNullOrEmpty()) {
            ContractDetailRow(label = "Tipo de trabajo", value = data!!.workType!!)
        }
        if (!data?.studentEmail.isNullOrEmpty()) {
            ContractDetailRow(label = "Correo del estudiante", value = data!!.studentEmail!!)
        }
        if (!data?.studentUniversity.isNullOrEmpty()) {
            ContractDetailRow(label = "Universidad", value = data!!.studentUniversity!!)
        }
        if (!data?.studentCareer.isNullOrEmpty()) {
            ContractDetailRow(label = "Carrera", value = data!!.studentCareer!!)
        }
        if (!data?.startDate.isNullOrEmpty()) {
            ContractDetailRow(label = "Fecha de inicio", value = data!!.startDate!!)
        }
        if (!data?.endDate.isNullOrEmpty()) {
            ContractDetailRow(label = "Fecha de fin", value = data!!.endDate!!)
        }
        if (!data?.compensation.isNullOrEmpty()) {
            ContractDetailRow(label = "Compensación", value = data!!.compensation!!)
        }
        if (!data?.clauses.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Cláusulas",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF5A6A7A)
            )
            data!!.clauses!!.forEachIndexed { index, clause ->
                Text(
                    text = "• $clause",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF5A6A7A),
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
        detail.acceptedAt?.let {
            Spacer(modifier = Modifier.height(2.dp))
            ContractDetailRow(
                label = "Aceptado el",
                value = formatApplicationDate(it)
            )
        }

        if (detail.status.lowercase() == "active") {
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = {
                    val compensationValue = data?.compensation?.filter { it.isDigit() || it == '.' }?.toDoubleOrNull() ?: 0.0
                    onMakePayment(
                        detail.idJob,
                        detail.idStudent,
                        detail.jobTitle,
                        detail.idContract.toString(),
                        compensationValue
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkBlue)
            ) {
                Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Crear Pago")
            }
        }
    }
}

@Composable
private fun ContractDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF9AA5B4),
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1A1A1A),
            modifier = Modifier.weight(1.5f)
        )
    }
}

@Composable
private fun ContractStatusBadge(status: String) {
    val (bgColor, textColor, label) = when (status.lowercase()) {
        "active" -> Triple(Color(0xFFE8F5E9), Color(0xFF2E7D32), "Activo")
        "pending" -> Triple(Color(0xFFFFF8E1), Color(0xFFF57F17), "Pendiente")
        else -> Triple(Color(0xFFF5F5F5), Color(0xFF757575), status.replaceFirstChar { it.uppercase() })
    }
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = bgColor
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}
