package com.moviles.jobmatch.ui.screens.job

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moviles.jobmatch.ui.theme.DarkBlue
import com.moviles.jobmatch.ui.utils.formatApplicationDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationDetailScreen(
    applicationId: Int,
    jobId: Int,
    jobTitle: String,
    studentId: String,
    studentName: String,
    studentEmail: String,
    status: String,
    createdAt: String,
    onBackPressed: () -> Unit = {},
    onViewStudentProfile: (String) -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Postulación", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF5F7FA)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Status banner (color-coded by state)
            val (bgColor, textColor, label) = when (status.lowercase()) {
                "accepted" -> Triple(Color(0xFFE8F5E9), Color(0xFF2E7D32), "Aceptado")
                "rejected" -> Triple(Color(0xFFFFEBEE), Color(0xFFC62828), "Rechazado")
                else -> Triple(Color(0xFFFFF8E1), Color(0xFFF57F17), "Pendiente")
            }
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = bgColor),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(Icons.Outlined.Info, null, tint = textColor, modifier = Modifier.size(20.dp))
                    Text(
                        text = "Estado: $label",
                        fontWeight = FontWeight.SemiBold,
                        color = textColor,
                        fontSize = 15.sp
                    )
                }
            }

            // Application details
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        "Información de la Postulación",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = Color(0xFF8A9BB0)
                    )
                    DetailRow(icon = Icons.Outlined.Tag, label = "ID Postulación", value = "#$applicationId")
                    DetailRow(icon = Icons.Outlined.Work, label = "ID Trabajo", value = "#$jobId")
                    DetailRow(icon = Icons.Outlined.WorkOutline, label = "Puesto", value = jobTitle)
                    DetailRow(icon = Icons.Outlined.Badge, label = "ID Estudiante", value = studentId)
                    DetailRow(
                        icon = Icons.Outlined.CalendarToday,
                        label = "Fecha de postulación",
                        value = formatApplicationDate(createdAt)
                    )
                }
            }

            // Student data
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        "Estudiante",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = Color(0xFF8A9BB0)
                    )
                    DetailRow(icon = Icons.Outlined.Person, label = "Nombre", value = studentName)
                    DetailRow(icon = Icons.Outlined.Email, label = "Correo", value = studentEmail)
                }
            }

            // View full profile button
            Button(
                onClick = { onViewStudentProfile(studentId) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkBlue)
            ) {
                Icon(Icons.Outlined.Person, null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ver perfil completo del estudiante", fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun DetailRow(icon: ImageVector, label: String, value: String) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(icon, null, tint = DarkBlue, modifier = Modifier.size(18.dp).padding(top = 2.dp))
        Column {
            Text(label, fontSize = 11.sp, color = Color(0xFF8A9BB0))
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1A1A2E))
        }
    }
}
