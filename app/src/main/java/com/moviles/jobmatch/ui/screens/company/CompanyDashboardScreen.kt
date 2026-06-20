package com.moviles.jobmatch.ui.screens.company

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.BusinessCenter
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
import com.moviles.jobmatch.data.AuthSession
import com.moviles.jobmatch.data.Job
import com.moviles.jobmatch.ui.theme.DarkBlue
import com.moviles.jobmatch.ui.utils.formatJobDate
import com.moviles.jobmatch.ui.utils.formatPaymentType


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyDashboardScreen(
    onJobClick: (Int) -> Unit = {},
    onCreateJob: () -> Unit = {},
    viewModel: CompanyDashboardViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val companyName = AuthSession.currentUser?.fullName ?: "Mi Empresa"

    LaunchedEffect(Unit) {
        viewModel.loadMyJobs()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    Box(
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.BusinessCenter,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                title = {
                    Text(
                        "Dashboard",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color(0xFF1A1A2E)
                    )
                },
                actions = {
                    // Notification bell removed as per UI requirements
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF4F6FA)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                CompanyHeaderCard(companyName = companyName)
            }

            item {
                StatsGrid(jobs = uiState.jobs)
            }

            item {
                Button(
                    onClick = onCreateJob,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkBlue)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Publicar Nueva Vacante", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Gestión de Publicaciones",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF1A1A2E)
                    )
                    Text(
                        "Ver todas",
                        fontSize = 13.sp,
                        color = DarkBlue,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            if (uiState.isLoading) {
                item {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = DarkBlue)
                    }
                }
            } else if (uiState.errorMessage != null) {
                item {
                    Text(
                        uiState.errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            } else if (uiState.jobs.isEmpty()) {
                item { EmptyJobsCard(onCreateJob = onCreateJob) }
            } else {
                items(uiState.jobs) { job ->
                    DashboardJobCard(job = job, onManage = { onJobClick(job.idJob) })
                }
                item { TipCard() }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun CompanyHeaderCard(companyName: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(DarkBlue),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = companyName.take(2).uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    companyName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF1A1A2E),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun StatsGrid(jobs: List<Job>) {
    StatCard(
        modifier = Modifier.fillMaxWidth(),
        label = "Puestos Activos",
        value = "${jobs.size}",
        badge = null,
        valueColor = Color(0xFF1A1A2E)
    )
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    badge: String?,
    valueColor: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (badge != null) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFFFECEC)
                ) {
                    Text(
                        badge,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        fontSize = 10.sp,
                        color = Color(0xFFD32F2F),
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
            } else {
                Spacer(modifier = Modifier.height(4.dp))
            }
            Text(
                value,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 22.sp,
                color = valueColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(label, fontSize = 11.sp, color = Color(0xFF8A9BB0), fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun DashboardJobCard(job: Job, onManage: () -> Unit) {
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
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        StatusBadge(job.status)
                        Text(
                            when (job.type) {
                                "autonomous" -> "· ${formatJobDate(job.startDate ?: "")}"
                                else -> "· ${formatJobDate(job.workDate ?: "")}"
                            },
                            fontSize = 12.sp,
                            color = Color(0xFF8A9BB0)
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "₡${"%,.0f".format(job.payment)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = DarkBlue
                    )
                    Text(
                        formatPaymentType(job.paymentType),
                        fontSize = 11.sp,
                        color = Color(0xFF8A9BB0)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFF0F0F0))
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onManage,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        "Gestionar",
                        color = DarkBlue,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                    Text(" >", color = DarkBlue, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(status: String) {
    val label = when (status.lowercase()) {
        "open", "active" -> "Activo"
        "in-progress" -> "En Progreso"
        "paused", "closed" -> "Pausado"
        else -> status.replaceFirstChar { it.uppercase() }
    }
    val bgColor = when (status.lowercase()) {
        "open", "active" -> Color(0xFFE8F5E9)
        "in-progress" -> Color(0xFFE3F2FD)
        else -> Color(0xFFF5F5F5)
    }
    val textColor = when (status.lowercase()) {
        "open", "active" -> Color(0xFF2E7D32)
        "in-progress" -> Color(0xFF1565C0)
        else -> Color(0xFF757575)
    }
    Surface(shape = RoundedCornerShape(20.dp), color = bgColor) {
        Text(
            label,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            fontSize = 11.sp,
            color = textColor,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun TipCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F7FF)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("📈", fontSize = 24.sp)
            Column {
                Text(
                    "¡Tu visibilidad está activa!",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = Color(0xFF1A1A2E)
                )
                Text(
                    "Tus ofertas son visibles para estudiantes cercanos.",
                    fontSize = 12.sp,
                    color = Color(0xFF5A6A7A),
                    lineHeight = 17.sp
                )
            }
        }
    }
}

@Composable
private fun EmptyJobsCard(onCreateJob: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(32.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                Icons.Outlined.BusinessCenter,
                contentDescription = null,
                tint = Color(0xFFBBBBBB),
                modifier = Modifier.size(48.dp)
            )
            Text(
                "Aún no tienes ofertas publicadas",
                fontWeight = FontWeight.Medium,
                color = Color.Gray,
                fontSize = 14.sp
            )
            Button(
                onClick = onCreateJob,
                colors = ButtonDefaults.buttonColors(containerColor = DarkBlue),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Publicar Primera Vacante")
            }
        }
    }
}


