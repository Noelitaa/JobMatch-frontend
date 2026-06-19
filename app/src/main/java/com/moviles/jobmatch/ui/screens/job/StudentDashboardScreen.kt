package com.moviles.jobmatch.ui.screens.job

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.WorkOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moviles.jobmatch.ui.components.JobCard
import com.moviles.jobmatch.ui.theme.DarkBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDashboardScreen(
    onJobClick: (Int) -> Unit = {},
    viewModel: StudentDashboardViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Panel Estudiante",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color(0xFF1A1A2E)
                    )
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(
                            Icons.Outlined.Notifications,
                            contentDescription = "Notificaciones",
                            tint = Color(0xFF555555)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF4F6FA)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            item {
                StudentGreetingCard(
                    name = uiState.studentName,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            item {
                Text(
                    "Recomendados para ti",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF1A1A2E)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            when {
                uiState.isLoading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = DarkBlue)
                        }
                    }
                }

                uiState.errorMessage != null -> {
                    item {
                        RecommendedErrorCard(
                            message = uiState.errorMessage!!,
                            onRetry = viewModel::loadRecommended,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }

                uiState.recommendedJobs.isEmpty() -> {
                    item {
                        RecommendedEmptyCard(modifier = Modifier.padding(horizontal = 16.dp))
                    }
                }

                else -> {
                    items(uiState.recommendedJobs) { job ->
                        JobCard(
                            job = job,
                            onSeeMoreClick = { onJobClick(job.idJob) }
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun StudentGreetingCard(name: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkBlue),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "¡Hola, ${name.substringBefore(" ")}!",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Estos trabajos se adaptan a tu disponibilidad y habilidades.",
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.80f),
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun RecommendedEmptyCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
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
                Icons.Outlined.WorkOff,
                contentDescription = null,
                tint = Color(0xFFBBBBBB),
                modifier = Modifier.size(48.dp)
            )
            Text(
                "Sin recomendaciones por ahora",
                fontWeight = FontWeight.Medium,
                color = Color.Gray,
                fontSize = 14.sp
            )
            Text(
                "Actualiza tu disponibilidad y habilidades para recibir ofertas personalizadas.",
                fontSize = 12.sp,
                color = Color(0xFF8A9BB0),
                lineHeight = 17.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
private fun RecommendedErrorCard(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                message,
                color = MaterialTheme.colorScheme.error,
                fontSize = 13.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = DarkBlue),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Reintentar")
            }
        }
    }
}
