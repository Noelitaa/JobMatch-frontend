package com.moviles.jobmatch.ui.screens.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import com.moviles.jobmatch.data.remote.model.ContractDetailResponse
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moviles.jobmatch.data.remote.model.AvailabilityResponse
import com.moviles.jobmatch.data.remote.model.ContractListResponse
import com.moviles.jobmatch.data.repository.AppContainer
import com.moviles.jobmatch.navigation.AppDestinations
import com.moviles.jobmatch.ui.components.DayAvailabilitySelector
import com.moviles.jobmatch.ui.components.DeleteAccountDialog
import com.moviles.jobmatch.ui.components.InfoRow
import com.moviles.jobmatch.ui.components.RatingDialog
import com.moviles.jobmatch.ui.components.ReceivedRatingsSection
import com.moviles.jobmatch.ui.components.JobMatchTopBar
import com.moviles.jobmatch.ui.components.SectionHeader
import com.moviles.jobmatch.ui.components.SkillChip
import com.moviles.jobmatch.ui.components.StatCard
import com.moviles.jobmatch.ui.components.StudentProfileHeader
import com.moviles.jobmatch.ui.theme.DarkBlue
import com.moviles.jobmatch.ui.utils.formatApplicationDate

@Composable
fun StudentProfileScreen(
    onSettingsClick: () -> Unit = {},
    onEditAvailability: () -> Unit = {},
    onPaymentHistory: () -> Unit = {},
    onAccountDeleted: () -> Unit = {}
) {
    val viewModel: StudentProfileViewModel = viewModel(
        factory = StudentProfileViewModelFactory(
            AppContainer.studentRepository,
            AppContainer.contractRepository,
            AppContainer.ratingRepository
        )
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val deleteViewModel: DeleteAccountViewModel = viewModel(
        factory = DeleteAccountViewModel.Factory(AppContainer.userRepository)
    )
    val deleteUiState by deleteViewModel.uiState.collectAsStateWithLifecycle()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var ratingContractId by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(deleteUiState.isDeleted) {
        if (deleteUiState.isDeleted) onAccountDeleted()
    }

    Scaffold(
        topBar = {
            JobMatchTopBar(
                title = "Mi Perfil",
                showBackButton = false,
                onSettingsPressed = onSettingsClick
            )
        },
        containerColor = Color(0xFFF5F7FA),
        contentWindowInsets = WindowInsets(0)
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = DarkBlue)
                }
            }

            uiState.errorMessage != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(24.dp)
                    )
                }
            }

            else -> {
                val student = uiState.student
                val availabilityDays = student?.availability.toSelectedDays()
                val skills = student?.skills ?: emptyList()
                val contracts = uiState.contracts

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    // --- Header ---
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        StudentProfileHeader(
                            name = student?.user?.fullName ?: "",
                            career = student?.career ?: "",
                            university = student?.university ?: "",
                            avatarUrl = student?.user?.avatar,
                            isVerified = true,
                            rating = student?.averageRating ?: 0f,
                            jobCount = 0
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // --- Estadísticas ---
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatCard(
                            icon = Icons.Default.Timer,
                            value = "--",
                            label = "PUNTUALIDAD",
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            icon = Icons.Default.TrendingUp,
                            value = "--",
                            label = "GANANCIAS",
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            icon = Icons.Default.EmojiEvents,
                            value = "--",
                            label = "INSIGNIAS",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))


                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        SectionHeader(title = "Sobre mí")
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Text(
                                text = student?.user?.bio ?: "Sin descripción",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF5A6A7A),
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // --- Habilidades ---
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        SectionHeader(
                            title = "Habilidades",
                            actionText = "Ver todas",
                            onActionClick = {}
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        if (skills.isNotEmpty()) {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                skills.forEach { skill -> SkillChip(skill.skillName) }
                            }
                        } else {
                            Text(
                                text = "Sin habilidades registradas",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF9AA5B4)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // --- Disponibilidad ---
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        SectionHeader(
                            title = "Disponibilidad",
                            actionText = "Editar",
                            onActionClick = onEditAvailability
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                DayAvailabilitySelector(
                                    selectedDays = availabilityDays,
                                    onDayToggle = {},
                                    readOnly = true
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // --- Experiencia Reciente ---
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        SectionHeader(
                            title = "Experiencia Reciente",
                            actionText = "Historial",
                            onActionClick = onPaymentHistory
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Sin experiencia registrada",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF9AA5B4)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // --- Mis Contratos ---
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        SectionHeader(title = "Mis Contratos")
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
                            contracts.isEmpty() -> {
                                Text(
                                    text = "Sin contratos registrados",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF9AA5B4)
                                )
                            }
                            else -> {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    contracts.forEach { contract ->
                                        ContractCard(
                                            contract = contract,
                                            detail = uiState.contractDetails[contract.idContract],
                                            isLoadingDetail = contract.idContract in uiState.loadingContractIds,
                                            detailError = uiState.contractDetailErrors[contract.idContract],
                                            isAccepting = contract.idContract in uiState.acceptingContractIds,
                                            acceptError = uiState.contractAcceptErrors[contract.idContract],
                                            onExpand = { viewModel.loadContractDetail(contract.idContract) },
                                            onAccept = { viewModel.acceptContract(contract.idContract) }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // --- Calificaciones ---
                    val activeContracts = contracts.filter { it.status.equals("active", ignoreCase = true) }
                    if (activeContracts.isNotEmpty()) {
                        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                            SectionHeader(title = "Calificaciones")
                            Spacer(modifier = Modifier.height(8.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
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
                                                    color = Color(0xFF1A2332)
                                                )
                                                Text(
                                                    text = contract.companyName,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = Color(0xFF5A6A7A)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            if (contract.idContract in uiState.ratingSuccessIds) {
                                                Text(
                                                    text = "Ya calificado",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = Color(0xFF388E3C),
                                                    fontWeight = FontWeight.Medium
                                                )
                                            } else {
                                                Surface(
                                                    shape = RoundedCornerShape(20.dp),
                                                    color = Color(0xFFFFC107),
                                                    modifier = Modifier.clickable { ratingContractId = contract.idContract }
                                                ) {
                                                    Text(
                                                        text = "Calificar",
                                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                                        style = MaterialTheme.typography.labelSmall,
                                                        fontWeight = FontWeight.Medium,
                                                        color = Color(0xFF1A2332),
                                                        fontSize = 11.sp
                                                    )
                                                }
                                            }
                                        }
                                        if (index < activeContracts.lastIndex) {
                                            HorizontalDivider(color = Color(0xFFF0F2F5))
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // --- Calificaciones recibidas ---
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        ReceivedRatingsSection(
                            isLoading = uiState.isLoadingRatings,
                            error = uiState.ratingsError,
                            ratings = uiState.receivedRatings
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // --- Footer ---
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        student?.user?.phone?.let { phone ->
                            InfoRow(
                                icon = Icons.Default.Phone,
                                text = phone
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                        OutlinedButton(
                            onClick = {},
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, DarkBlue),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkBlue)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Añadir Certificación o Título",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = { showDeleteDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
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

                    Spacer(modifier = Modifier.height(24.dp))
                }
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
            title = "Calificar a la empresa",
            description = "¿Cómo fue tu experiencia con la empresa?",
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
private fun ContractCard(
    contract: ContractListResponse,
    detail: ContractDetailResponse?,
    isLoadingDetail: Boolean,
    detailError: String?,
    isAccepting: Boolean,
    acceptError: String?,
    onExpand: () -> Unit,
    onAccept: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var termsAccepted by remember { mutableStateOf(false) }

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
        Column {
            // --- Main row (always visible) ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        expanded = !expanded
                        if (expanded) onExpand()
                    }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = contract.jobTitle,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1A2332)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = contract.companyName,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF5A6A7A)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formatApplicationDate(contract.createdAt),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF9AA5B4)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                ContractStatusBadge(status = contract.status)
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Contraer" else "Expandir",
                    modifier = Modifier
                        .size(20.dp)
                        .rotate(chevronRotation),
                    tint = Color(0xFF9AA5B4)
                )
            }

            // --- Expandable section ---
            AnimatedVisibility(visible = expanded) {
                Column {
                    HorizontalDivider(color = Color(0xFFF0F2F5), thickness = 1.dp)

                    when {
                        isLoadingDetail -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = DarkBlue,
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.dp
                                )
                            }
                        }

                        detailError != null -> {
                            Text(
                                text = detailError,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(16.dp)
                            )
                        }

                        detail != null -> {
                            val parsedData = detail.parsedContractData

                            Column(modifier = Modifier.padding(16.dp)) {

                                // Header
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Contrato #${detail.idContract}",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color(0xFF9AA5B4)
                                    )
                                    parsedData?.workType?.let { type ->
                                        Surface(
                                            shape = RoundedCornerShape(20.dp),
                                            color = Color(0xFFEFF3FF)
                                        ) {
                                            Text(
                                                text = when (type.lowercase()) {
                                                    "fixed-time" -> "Tiempo fijo"
                                                    "autonomous" -> "Autónomo"
                                                    else -> type
                                                },
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = DarkBlue,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                                ContractDetailRow(label = "Creado", value = formatApplicationDate(detail.createdAt))
                                detail.acceptedAt?.let {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    ContractDetailRow(label = "Aceptado", value = formatApplicationDate(it))
                                }

                                // Contract period
                                if (parsedData?.startDate != null || parsedData?.endDate != null) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    val rangeText = when {
                                        parsedData.startDate != null && parsedData.endDate != null ->
                                            "${formatApplicationDate(parsedData.startDate)} → ${formatApplicationDate(parsedData.endDate)}"
                                        parsedData.startDate != null -> "Desde ${formatApplicationDate(parsedData.startDate)}"
                                        else -> "Hasta ${formatApplicationDate(parsedData.endDate!!)}"
                                    }
                                    ContractDetailRow(label = "Vigencia", value = rangeText)
                                }

                                // Compensation
                                parsedData?.compensation?.let { comp ->
                                    Spacer(modifier = Modifier.height(4.dp))
                                    ContractDetailRow(label = "Pago", value = comp)
                                }

                                Spacer(modifier = Modifier.height(14.dp))
                                HorizontalDivider(color = Color(0xFFF0F2F5))
                                Spacer(modifier = Modifier.height(14.dp))

                                // Company
                                Text(
                                    text = "Empresa",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = DarkBlue
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                ContractDetailRow(label = "Nombre", value = detail.companyName)
                                parsedData?.companyEmail?.let {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    ContractDetailRow(label = "Email", value = it)
                                }
                                parsedData?.companyOwnerName?.let {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    ContractDetailRow(label = "Contacto", value = it)
                                }

                                Spacer(modifier = Modifier.height(14.dp))
                                HorizontalDivider(color = Color(0xFFF0F2F5))
                                Spacer(modifier = Modifier.height(14.dp))

                                // Student
                                Text(
                                    text = "Estudiante",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = DarkBlue
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                ContractDetailRow(label = "Nombre", value = detail.studentName)
                                Spacer(modifier = Modifier.height(4.dp))
                                ContractDetailRow(label = "Email", value = detail.studentEmail)
                                parsedData?.studentUniversity?.let {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    ContractDetailRow(label = "Universidad", value = it)
                                }
                                parsedData?.studentCareer?.let {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    ContractDetailRow(label = "Carrera", value = it)
                                }

                                // Clauses
                                val clauses = parsedData?.clauses
                                if (!clauses.isNullOrEmpty()) {
                                    Spacer(modifier = Modifier.height(14.dp))
                                    HorizontalDivider(color = Color(0xFFF0F2F5))
                                    Spacer(modifier = Modifier.height(14.dp))
                                    Text(
                                        text = "Términos y condiciones",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = DarkBlue
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    clauses.forEachIndexed { index, clause ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(bottom = 6.dp),
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Text(
                                                text = "${index + 1}.",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = DarkBlue,
                                                fontWeight = FontWeight.SemiBold,
                                                modifier = Modifier.width(20.dp)
                                            )
                                            Text(
                                                text = clause,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color(0xFF1A2332),
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                    }
                                }

                                // Accept contract
                                if (contract.status.equals("pending", ignoreCase = true)) {
                                    Spacer(modifier = Modifier.height(14.dp))
                                    HorizontalDivider(color = Color(0xFFF0F2F5))
                                    Spacer(modifier = Modifier.height(14.dp))

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable(enabled = !isAccepting) {
                                                termsAccepted = !termsAccepted
                                            }
                                    ) {
                                        Checkbox(
                                            checked = termsAccepted,
                                            onCheckedChange = { termsAccepted = it },
                                            enabled = !isAccepting
                                        )
                                        Text(
                                            text = "He leído y acepto los términos del contrato.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF1A2332)
                                        )
                                    }

                                    if (acceptError != null) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = acceptError,
                                            color = MaterialTheme.colorScheme.error,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = onAccept,
                                        enabled = termsAccepted && !isAccepting,
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = DarkBlue)
                                    ) {
                                        if (isAccepting) {
                                            CircularProgressIndicator(
                                                color = Color.White,
                                                modifier = Modifier.size(18.dp),
                                                strokeWidth = 2.dp
                                            )
                                        } else {
                                            Text("Aceptar contrato")
                                        }
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ContractDetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF9AA5B4),
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(90.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF1A2332),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ContractStatusBadge(status: String) {
    val (backgroundColor, textColor, label) = when (status.lowercase()) {
        "pending" -> Triple(Color(0xFFFFF3E0), Color(0xFFF57C00), "Pendiente")
        "accepted", "active" -> Triple(Color(0xFFE8F5E9), Color(0xFF388E3C), "Activo")
        "completed" -> Triple(Color(0xFFE3F2FD), Color(0xFF1565C0), "Completado")
        "cancelled" -> Triple(Color(0xFFFFEBEE), Color(0xFFC62828), "Cancelado")
        else -> Triple(Color(0xFFF5F5F5), Color(0xFF757575), status)
    }
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = textColor,
            fontSize = 11.sp
        )
    }
}

private fun AvailabilityResponse?.toSelectedDays(): List<Boolean> {
    if (this == null) return List(7) { false }
    return listOf(
        monday.isNotEmpty(),
        tuesday.isNotEmpty(),
        wednesday.isNotEmpty(),
        thursday.isNotEmpty(),
        friday.isNotEmpty(),
        saturday.isNotEmpty(),
        sunday.isNotEmpty()
    )
}
