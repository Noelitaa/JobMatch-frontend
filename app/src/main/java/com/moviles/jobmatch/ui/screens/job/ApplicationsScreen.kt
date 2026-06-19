package com.moviles.jobmatch.ui.screens.job

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moviles.jobmatch.data.remote.model.ApplicationResponse
import com.moviles.jobmatch.ui.components.SkillChip
import com.moviles.jobmatch.ui.theme.DarkBlue
import com.moviles.jobmatch.ui.utils.formatApplicationDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationsScreen(
    jobId: Int,
    jobTitle: String,
    onBackPressed: () -> Unit = {},
    onStudentClick: (String) -> Unit = {},
    onViewApplicationDetail: (ApplicationResponse, Int) -> Unit = { _, _ -> },
    viewModel: ApplicationsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(jobId) {
        viewModel.loadApplications(jobId)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Postulantes", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                        if (jobTitle.isNotBlank()) {
                            Text(jobTitle, fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8F9FB)
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = DarkBlue)
                }
            }

            uiState.errorMessage != null -> {
                Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                        Text(uiState.errorMessage!!, color = Color.Gray, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.loadApplications(jobId) },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkBlue)
                        ) { Text("Reintentar") }
                    }
                }
            }

            uiState.applications.isEmpty() -> {
                Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    Text("No hay postulantes aún.", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = "${uiState.applications.size} postulante(s)",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                    items(uiState.applications) { application ->
                        ApplicantCard(
                            application = application,
                            isUpdating = uiState.updatingId == application.idApplication,
                            onAccept = { viewModel.updateStatus(application.idApplication, "accepted") },
                            onReject = { viewModel.updateStatus(application.idApplication, "rejected") },
                            onStudentClick = { onStudentClick(application.idStudent) },
                            onViewDetail = { onViewApplicationDetail(application, jobId) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ApplicantCard(
    application: ApplicationResponse,
    isUpdating: Boolean,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onStudentClick: () -> Unit = {},
    onViewDetail: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable(onClick = onStudentClick)
                    .padding(vertical = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE3F2FD)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = application.studentName.take(2).uppercase(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = DarkBlue
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = application.studentName,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = DarkBlue
                    )
                    Text(
                        text = application.studentEmail,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                StatusChip(status = application.status)
            }

            if (!application.studentUniversity.isNullOrBlank() || !application.studentCareer.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = Color(0xFFF0F0F0))
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    application.studentUniversity?.let {
                        SkillChip(
                            text = it,
                            backgroundColor = Color(0xFFF5F5F5),
                            textColor = Color(0xFF5A6A7A),
                            shape = RoundedCornerShape(8.dp),
                            fontSize = 11.sp
                        )
                    }
                    application.studentCareer?.let {
                        SkillChip(
                            text = it,
                            backgroundColor = Color(0xFFF5F5F5),
                            textColor = Color(0xFF5A6A7A),
                            shape = RoundedCornerShape(8.dp),
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    Icons.Outlined.CalendarToday,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
                    tint = Color.Gray
                )
                Text(
                    text = "Postulado el ${formatApplicationDate(application.createdAt)}",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = onViewDetail,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkBlue),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBlue)
            ) {
                Text("Ver detalle de la postulación", fontSize = 13.sp)
            }

            if (application.status.lowercase() == "pending") {
                Spacer(modifier = Modifier.height(14.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onReject,
                        modifier = Modifier.weight(1f),
                        enabled = !isUpdating,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFE53935)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE53935))
                    ) {
                        if (isUpdating) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = Color(0xFFE53935))
                        } else {
                            Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Rechazar", fontSize = 13.sp)
                        }
                    }

                    Button(
                        onClick = onAccept,
                        modifier = Modifier.weight(1f),
                        enabled = !isUpdating,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DarkBlue)
                    ) {
                        if (isUpdating) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = Color.White)
                        } else {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Aceptar", fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusChip(status: String) {
    val (bgColor, textColor, label) = when (status.lowercase()) {
        "accepted" -> Triple(Color(0xFFE8F5E9), Color(0xFF2E7D32), "Aceptado")
        "rejected" -> Triple(Color(0xFFFFEBEE), Color(0xFFC62828), "Rechazado")
        else -> Triple(Color(0xFFFFF8E1), Color(0xFFF57F17), "Pendiente")
    }
    Surface(color = bgColor, shape = RoundedCornerShape(20.dp)) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            fontSize = 11.sp,
            color = textColor,
            fontWeight = FontWeight.SemiBold
        )
    }
}

