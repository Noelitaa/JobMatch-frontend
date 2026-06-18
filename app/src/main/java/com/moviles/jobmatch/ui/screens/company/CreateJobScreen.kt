package com.moviles.jobmatch.ui.screens.company

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moviles.jobmatch.ui.components.SectionHeader

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

    // Common fields
    var jobType by remember { mutableStateOf("fixed-time") }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var payment by remember { mutableStateOf("") }
    var paymentType by remember { mutableStateOf("Hora") }

    // Fixed-time fields
    var workDate by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }

    // Autonomous fields
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var deliverableInput by remember { mutableStateOf("") }
    var deliverables by remember { mutableStateOf(listOf<String>()) }

    // Skills (common to both types)
    var skillInput by remember { mutableStateOf("") }
    var skills by remember { mutableStateOf(listOf<String>()) }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    val paymentTypes = listOf("Hora", "Turno", "Proyecto")
    val isAutonomous = jobType == "autonomous"

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

            // --- Tipo de trabajo ---
            SectionHeader(title = "Tipo de trabajo")

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("fixed-time" to "Trabajo Fijo", "autonomous" to "Autónomo").forEach { (value, label) ->
                    val selected = jobType == value
                    OutlinedButton(
                        onClick = { jobType = value },
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (selected) Color.White else Color.Transparent,
                            contentColor = if (selected) JobMatchBlue else Color.Gray
                        ),
                        border = BorderStroke(
                            width = if (selected) 2.dp else 1.dp,
                            color = if (selected) JobMatchBlue else Color.LightGray
                        )
                    ) {
                        Text(label, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal)
                    }
                }
            }

            if (isAutonomous) {
                Text(
                    "El estudiante trabaja de forma independiente dentro de un rango de fechas y entrega resultados concretos.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            } else {
                Text(
                    "El estudiante trabaja en una fecha y horario específico.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

            // --- Información Básica ---
            SectionHeader(title = "Información Básica")

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Título del puesto", style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium)
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("Ej. Auxiliar de Eventos, Repartidor...", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )

                Text("Descripción completa", style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium)
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("Describe las tareas y responsabilidades del estudiante...", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    maxLines = 5,
                    shape = RoundedCornerShape(8.dp)
                )
            }

            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

            // --- Pago y Modalidad ---
            SectionHeader(title = "Pago y Modalidad")

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
                        border = BorderStroke(
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
            }

            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

            // --- Fecha / Horario (condicional por tipo) ---
            if (isAutonomous) {
                SectionHeader(title = "Vigencia del contrato")

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = startDate,
                        onValueChange = { startDate = it },
                        label = { Text("Fecha inicio") },
                        placeholder = { Text("2026-06-01") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = endDate,
                        onValueChange = { endDate = it },
                        label = { Text("Fecha fin") },
                        placeholder = { Text("2026-08-01") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

                SectionHeader(title = "Entregables")

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = deliverableInput,
                        onValueChange = { deliverableInput = it },
                        placeholder = { Text("Ej. Informe final, diseño de logo...", color = Color.Gray) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                    Button(
                        onClick = {
                            if (deliverableInput.isNotBlank()) {
                                deliverables = deliverables + deliverableInput.trim()
                                deliverableInput = ""
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = JobMatchBlue),
                        contentPadding = PaddingValues(12.dp),
                        modifier = Modifier.size(52.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar", tint = Color.White,
                            modifier = Modifier.size(20.dp))
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    deliverables.forEachIndexed { index, item ->
                        ChipItem(text = item, onRemove = {
                            deliverables = deliverables.toMutableList().also { it.removeAt(index) }
                        })
                    }
                }

            } else {
                SectionHeader(title = "Fecha y Horario")

                OutlinedTextField(
                    value = workDate,
                    onValueChange = { workDate = it },
                    label = { Text("Fecha del trabajo") },
                    placeholder = { Text("2026-06-15") },
                    leadingIcon = {
                        Icon(Icons.Outlined.CalendarToday, contentDescription = null,
                            tint = Color.Gray, modifier = Modifier.size(18.dp))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )

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
            }

            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

            // --- Requisitos (ambos tipos) ---
            SectionHeader(title = "Habilidades requeridas")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = skillInput,
                    onValueChange = { skillInput = it },
                    placeholder = { Text("Ej. Excel, Atención al cliente...", color = Color.Gray) },
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
                    Icon(Icons.Default.Add, contentDescription = "Agregar", tint = Color.White,
                        modifier = Modifier.size(20.dp))
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                skills.forEachIndexed { index, skill ->
                    ChipItem(text = skill, onRemove = {
                        skills = skills.toMutableList().also { it.removeAt(index) }
                    })
                }
            }

            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

            // --- Costo total ---
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
                                color = Color.Gray, textDecoration = TextDecoration.LineThrough)
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

            // --- Botón publicar ---
            Button(
                onClick = {
                    errorMessage = null
                    val amount = payment.toDoubleOrNull()
                    when {
                        title.isBlank() -> errorMessage = "El título es requerido"
                        description.isBlank() -> errorMessage = "La descripción es requerida"
                        amount == null || amount <= 0 -> errorMessage = "Ingresa un monto válido"
                        isAutonomous && startDate.isBlank() -> errorMessage = "La fecha de inicio es requerida"
                        isAutonomous && endDate.isBlank() -> errorMessage = "La fecha de fin es requerida"
                        !isAutonomous && workDate.isBlank() -> errorMessage = "La fecha es requerida"
                        !isAutonomous && startTime.isBlank() -> errorMessage = "La hora de inicio es requerida"
                        !isAutonomous && endTime.isBlank() -> errorMessage = "La hora de fin es requerida"
                        else -> if (isAutonomous) {
                            viewModel.createAutonomousJob(
                                title = title,
                                description = description,
                                payment = amount!!,
                                paymentType = paymentType,
                                startDate = startDate,
                                endDate = endDate,
                                deliverables = deliverables,
                                skillsRequired = skills
                            )
                        } else {
                            viewModel.createFixedTimeJob(
                                title = title,
                                description = description,
                                payment = amount!!,
                                paymentType = paymentType,
                                workDate = workDate,
                                startTime = "$startTime:00",
                                endTime = "$endTime:00",
                                skillsRequired = skills
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = JobMatchBlue),
                enabled = uiState !is CreateJobUiState.Loading
            ) {
                if (uiState is CreateJobUiState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
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
private fun ChipItem(text: String, onRemove: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        modifier = Modifier.border(1.dp, Color.LightGray, RoundedCornerShape(20.dp))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                Icons.Default.Close,
                contentDescription = "Eliminar",
                modifier = Modifier.size(16.dp).clickable { onRemove() },
                tint = Color.Gray
            )
        }
    }
}
