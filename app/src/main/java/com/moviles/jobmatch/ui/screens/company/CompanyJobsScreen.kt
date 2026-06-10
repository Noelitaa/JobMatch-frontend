package com.moviles.jobmatch.ui.screens.company

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moviles.jobmatch.data.Job
import com.moviles.jobmatch.ui.theme.DarkBlue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyJobsScreen(
    onCreateJob: () -> Unit = {},
    onEditJob: (Int) -> Unit = {},
    onJobClick: (Int, String) -> Unit = { _, _ -> },
    viewModel: CompanyDashboardViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadMyJobs()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Administrar Trabajos",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color(0xFF1A1A2E)
                    )
                },
                actions = {
                    IconButton(onClick = onCreateJob) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Publicar",
                            tint = DarkBlue
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF4F6FA)
    ) { padding ->
        when {
            uiState.isLoading -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = DarkBlue)
                }
            }
            uiState.errorMessage != null -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(uiState.errorMessage!!, color = MaterialTheme.colorScheme.error)
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        JobsStatsRow(jobCount = uiState.jobs.size)
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Tus Publicaciones",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color(0xFF1A1A2E)
                            )
                            Text(
                                "${uiState.jobs.size} total",
                                fontSize = 13.sp,
                                color = Color(0xFF8A9BB0)
                            )
                        }
                    }

                    if (uiState.jobs.isEmpty()) {
                        item {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 48.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        Icons.Outlined.WorkOff,
                                        contentDescription = null,
                                        tint = Color(0xFFBBBBBB),
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Text(
                                        "No tienes trabajos publicados",
                                        color = Color.Gray,
                                        fontSize = 14.sp
                                    )
                                    Button(
                                        onClick = onCreateJob,
                                        colors = ButtonDefaults.buttonColors(containerColor = DarkBlue),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Publicar Vacante")
                                    }
                                }
                            }
                        }
                    } else {
                        items(uiState.jobs) { job ->
                            ManageJobCard(
                                job = job,
                                onEdit = { onEditJob(job.idJob) },
                                onViewApplicants = { onJobClick(job.idJob, job.title) }
                            )
                        }
                    }

                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
private fun JobsStatsRow(jobCount: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "$jobCount",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 32.sp,
                color = DarkBlue
            )
            Text(
                "ACTIVOS",
                fontSize = 11.sp,
                color = Color(0xFF8A9BB0),
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
private fun ManageJobCard(
    job: Job,
    onEdit: () -> Unit,
    onViewApplicants: () -> Unit
) {
    var visible by remember { mutableStateOf(true) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        job.title,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = Color(0xFF1A1A2E),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        "Trabajo el ${formatJobDate(job.workDate)}",
                        fontSize = 12.sp,
                        color = Color(0xFF8A9BB0)
                    )
                }
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Outlined.MoreVert,
                        contentDescription = "Opciones",
                        tint = Color(0xFF8A9BB0),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Outlined.CreditCard,
                        contentDescription = null,
                        tint = Color(0xFF8A9BB0),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        "₡${"%,.0f".format(job.payment)}",
                        fontSize = 13.sp,
                        color = Color(0xFF3A4A5A),
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Outlined.Schedule,
                        contentDescription = null,
                        tint = Color(0xFF8A9BB0),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        formatPaymentType(job.paymentType),
                        fontSize = 13.sp,
                        color = Color(0xFF3A4A5A)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    repeat(3) { i ->
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .offset(x = (-5 * i).dp)
                                .clip(CircleShape)
                                .background(
                                    listOf(
                                        Color(0xFFBBDEFB),
                                        Color(0xFFFFF9C4),
                                        Color(0xFFC8E6C9)
                                    )[i]
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                listOf("A", "B", "C")[i],
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkBlue
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Candidatos", fontSize = 12.sp, color = Color(0xFF8A9BB0))
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = when (job.status.lowercase()) {
                        "open", "active" -> Color(0xFFE8F5E9)
                        "in-progress" -> Color(0xFFE3F2FD)
                        else -> Color(0xFFF5F5F5)
                    }
                ) {
                    Text(
                        when (job.status.lowercase()) {
                            "open", "active" -> "Activo"
                            "in-progress" -> "En Progreso"
                            else -> "Pausado"
                        },
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                        fontSize = 11.sp,
                        color = when (job.status.lowercase()) {
                            "open", "active" -> Color(0xFF2E7D32)
                            "in-progress" -> Color(0xFF1565C0)
                            else -> Color(0xFF757575)
                        },
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFF0F0F0))
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Switch(
                        checked = visible,
                        onCheckedChange = { visible = it },
                        modifier = Modifier.height(24.dp),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = DarkBlue,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color(0xFFCCCCCC)
                        )
                    )
                    Text(
                        if (visible) "Visible" else "Oculta",
                        fontSize = 12.sp,
                        color = if (visible) Color(0xFF3A4A5A) else Color(0xFF8A9BB0)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Outlined.Edit,
                            contentDescription = "Editar",
                            tint = Color(0xFF8A9BB0),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Button(
                        onClick = onViewApplicants,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DarkBlue),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text(
                            "Ver Postulantes",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

private fun formatJobDate(dateStr: String): String {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = sdf.parse(dateStr) ?: return dateStr
        val today = Date()
        val diffMs = date.time - today.time
        val diffDays = (diffMs / (1000 * 60 * 60 * 24)).toInt()
        when {
            diffDays == 0 -> "hoy"
            diffDays == 1 -> "mañana"
            diffDays == -1 -> "ayer"
            diffDays > 0 -> "en $diffDays días"
            else -> SimpleDateFormat("dd 'de' MMM", Locale("es")).format(date)
        }
    } catch (e: Exception) {
        dateStr
    }
}

private fun formatPaymentType(type: String): String = when (type.lowercase()) {
    "fixed", "fijo" -> "Fijo"
    "hourly", "hora", "por hora" -> "Por Hora"
    "daily", "diario" -> "Diario"
    "weekly", "semanal" -> "Semanal"
    else -> type.replaceFirstChar { it.uppercase() }
}
