package com.moviles.jobmatch.ui.screens.profile

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moviles.jobmatch.data.remote.model.AvailabilityResponse
import com.moviles.jobmatch.data.repository.AppContainer
import com.moviles.jobmatch.ui.components.DayAvailabilitySelector
import com.moviles.jobmatch.ui.components.InfoRow
import com.moviles.jobmatch.ui.components.JobMatchTopBar
import com.moviles.jobmatch.ui.components.SectionHeader
import com.moviles.jobmatch.ui.components.SkillChip
import com.moviles.jobmatch.ui.components.StatCard
import com.moviles.jobmatch.ui.components.StudentProfileHeader
import com.moviles.jobmatch.ui.theme.DarkBlue

@Composable
fun StudentProfileScreen(
    onSettingsClick: () -> Unit = {}
) {
    val viewModel: StudentProfileViewModel = viewModel(
        factory = StudentProfileViewModelFactory(AppContainer.studentRepository)
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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

                    // --- Sobre mí ---
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
                                skills.forEach { skill -> SkillChip(skill) }
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
                            onActionClick = {}
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
                            onActionClick = {}
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Sin experiencia registrada",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF9AA5B4)
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
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
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
