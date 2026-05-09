package com.moviles.jobmatch.ui.screens.company

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.clickable
val JobMatchBlue = Color(0xFF2196F3)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateJobScreen(
    companyId: String,
    onBackPressed: () -> Unit = {},
    onJobCreated: () -> Unit = {},
    viewModel: CreateJobViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var payment by remember { mutableStateOf("") }
    var paymentType by remember { mutableStateOf("Hora") }
    var workDate by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }
    var skillInput by remember { mutableStateOf("") }
    var skills by remember { mutableStateOf(listOf<String>()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val paymentTypes = listOf("Hora", "Turno", "Proyecto")

    LaunchedEffect(uiState) {
        when (uiState) {
            is CreateJobUiState.Success -> { onJobCreated(); viewModel.resetState() }
            is CreateJobUiState.Error -> errorMessage = (uiState as CreateJobUiState.Error).message
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Publicar Trabajo", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.Close, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Outlined.Notifications, contentDescription = "Notificaciones")
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Outlined.MoreVert, contentDescription = "Más opciones")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // Sección Información Básica
            SectionHeader(icon = Icons.Outlined.BusinessCenter, title = "Información Básica")

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Título del puesto", style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium)
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("Ej. Auxiliar de Eventos, Repartidor...",
                        color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )

                Text("Descripción completa", style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium)
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("Describe las tareas y responsabilidades del estudiante...",
                        color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    maxLines = 5,
                    shape = RoundedCornerShape(8.dp)
                )
            }

            Divider(color = Color.LightGray.copy(alpha = 0.5f))

            // Sección Pago y Modalidad
            SectionHeader(icon = Icons.Outlined.AttachMoney, title = "Pago y Modalidad")

            // Selector de tipo de pago
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                paymentTypes.forEach { type ->
                    val selected = paymentType == type
                    OutlinedButton(
                        onClick = { paymentType = type },
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (selected) Color.White else Color.Transparent,
                            contentColor = if (selected) JobMatchBlue else Color.Gray
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (selected) 2.dp else 1.dp,
                            color = if (selected) JobMatchBlue else Color.LightGray
                        )
                    ) {
                        Text(type, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal)
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Monto estimado (₡)", style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium)
                OutlinedTextField(
                    value = payment,
                    onValueChange = { payment = it },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    prefix = { Text("₡ ", fontWeight = FontWeight.Medium) },
                    shape = RoundedCornerShape(8.dp)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Info, contentDescription = null,
                        modifier = Modifier.size(14.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("El pago promedio para este tipo es de ₡3,500/hora.",
                        style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }

            Divider(color = Color.LightGray.copy(alpha = 0.5f))

            // Sección Fecha y Ubicación
            SectionHeader(icon = Icons.Outlined.CalendarMonth, title = "Fecha")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Duración", style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium)
                    OutlinedTextField(
                        value = workDate,
                        onValueChange = { workDate = it },
                        placeholder = { Text("Ej. 2026-06-15", color = Color.Gray) },
                        leadingIcon = {
                            Icon(Icons.Outlined.Schedule, contentDescription = null,
                                tint = Color.Gray, modifier = Modifier.size(18.dp))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Horarios Disponibles", style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium)
                OutlinedTextField(
                    value = if (startTime.isNotBlank() && endTime.isNotBlank())
                        "Lunes a Viernes, $startTime - $endTime"
                    else "",
                    onValueChange = {},
                    placeholder = { Text("Lunes a Viernes, 8:00 AM - 12:00 PM", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    readOnly = true
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = startTime,
                    onValueChange = { startTime = it },
                    label = { Text("Hora inicio") },
                    placeholder = { Text("08:00") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )
                OutlinedTextField(
                    value = endTime,
                    onValueChange = { endTime = it },
                    label = { Text("Hora fin") },
                    placeholder = { Text("17:00") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )
            }

            Divider(color = Color.LightGray.copy(alpha = 0.5f))

            // Sección Requisitos
            SectionHeader(icon = Icons.Outlined.Assignment, title = "Requisitos específicos")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = skillInput,
                    onValueChange = { skillInput = it },
                    placeholder = { Text("Añadir requisito...", color = Color.Gray) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )
                Button(
                    onClick = {
                        if (skillInput.isNotBlank()) {
                            skills = skills + skillInput.trim()
                            skillInput = ""
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = JobMatchBlue),
                    contentPadding = PaddingValues(12.dp),
                    modifier = Modifier.size(52.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar",
                        tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }

            // Chips de requisitos
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                skills.forEachIndexed { index, skill ->
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White,
                        modifier = Modifier.border(1.dp, Color.LightGray, RoundedCornerShape(20.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(skill, style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Eliminar",
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable {
                                        skills = skills.toMutableList().also { it.removeAt(index) }
                                    },
                                tint = Color.Gray
                            )
                        }
                    }
                }
            }

            Divider(color = Color.LightGray.copy(alpha = 0.5f))

            // Costo total
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFE3F2FD),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("COSTO TOTAL DEL ANUNCIO",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray, fontWeight = FontWeight.Bold)
                        Row(verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Gratis", style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold, color = Color.Black)
                            Text("₡5,000", style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray,
                                textDecoration = TextDecoration.LineThrough)
                        }
                    }
                    Text("Promoción Beta",
                        style = MaterialTheme.typography.labelMedium,
                        color = JobMatchBlue, fontWeight = FontWeight.SemiBold)
                }
            }

            errorMessage?.let {
                Text(it, color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall)
            }

            // Botón publicar
            Button(
                onClick = {
                    errorMessage = null
                    when {
                        title.isBlank() -> errorMessage = "El título es requerido"
                        description.isBlank() -> errorMessage = "La descripción es requerida"
                        workDate.isBlank() -> errorMessage = "La fecha es requerida"
                        startTime.isBlank() -> errorMessage = "La hora de inicio es requerida"
                        endTime.isBlank() -> errorMessage = "La hora de fin es requerida"
                        payment.isBlank() -> errorMessage = "El monto es requerido"
                        else -> viewModel.createJob(
                            companyId = companyId,
                            title = title,
                            description = description,
                            payment = payment.toDoubleOrNull() ?: 0.0,
                            paymentType = paymentType,
                            workDate = workDate,
                            startTime = "$startTime:00",
                            endTime = "$endTime:00",
                            skills = skills
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = JobMatchBlue),
                enabled = uiState !is CreateJobUiState.Loading
            ) {
                if (uiState is CreateJobUiState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp),
                        color = Color.White)
                } else {
                    Text("Publicar Vacante", fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp, color = Color.White)
                }
            }

            Text(
                "Al publicar, aceptas nuestros términos de servicio y la política de micro-contratación digital.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}

@Composable
fun SectionHeader(icon: ImageVector, title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(icon, contentDescription = null,
            tint = JobMatchBlue, modifier = Modifier.size(22.dp))
        Text(title, style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold, color = Color.Black)
    }
}