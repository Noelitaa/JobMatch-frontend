package com.moviles.jobmatch.ui.screens.job

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moviles.jobmatch.ui.theme.DarkBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditJobScreen(
    jobId: Int,
    onBackPressed: () -> Unit = {},
    onJobUpdated: () -> Unit = {},
    viewModel: EditJobViewModel = viewModel()
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
    var initialized by remember { mutableStateOf(false) }

    val paymentTypes = listOf("Hora", "Turno", "Proyecto")

    LaunchedEffect(jobId) {
        viewModel.loadJob(jobId)
    }

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is EditJobUiState.JobLoaded -> {
                if (!initialized) {
                    val job = state.job
                    title = job.title
                    description = job.description
                    payment = job.payment.toBigDecimal().stripTrailingZeros().toPlainString()
                    paymentType = when (job.paymentType.lowercase()) {
                        "hora", "por hora" -> "Hora"
                        "turno" -> "Turno"
                        "proyecto" -> "Proyecto"
                        else -> job.paymentType
                    }
                    workDate = job.workDate.take(10)
                    startTime = job.startTime.take(5)
                    endTime = job.endTime.take(5)
                    skills = job.deliverables
                        ?.split(",")
                        ?.map { it.trim() }
                        ?.filter { it.isNotEmpty() }
                        ?: emptyList()
                    initialized = true
                }
            }
            is EditJobUiState.SaveSuccess -> onJobUpdated()
            is EditJobUiState.Error -> errorMessage = state.message
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Oferta", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        when (uiState) {
            is EditJobUiState.Loading -> {
                if (!initialized) {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(padding),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator(color = DarkBlue) }
                } else {
                    EditJobForm(
                        padding = padding,
                        title = title, onTitleChange = { title = it },
                        description = description, onDescriptionChange = { description = it },
                        payment = payment, onPaymentChange = { payment = it },
                        paymentType = paymentType, onPaymentTypeChange = { paymentType = it },
                        paymentTypes = paymentTypes,
                        workDate = workDate, onWorkDateChange = { workDate = it },
                        startTime = startTime, onStartTimeChange = { startTime = it },
                        endTime = endTime, onEndTimeChange = { endTime = it },
                        skillInput = skillInput, onSkillInputChange = { skillInput = it },
                        skills = skills, onSkillsChange = { skills = it },
                        errorMessage = errorMessage,
                        isLoading = true,
                        onSave = {}
                    )
                }
            }
            else -> {
                EditJobForm(
                    padding = padding,
                    title = title, onTitleChange = { title = it },
                    description = description, onDescriptionChange = { description = it },
                    payment = payment, onPaymentChange = { payment = it },
                    paymentType = paymentType, onPaymentTypeChange = { paymentType = it },
                    paymentTypes = paymentTypes,
                    workDate = workDate, onWorkDateChange = { workDate = it },
                    startTime = startTime, onStartTimeChange = { startTime = it },
                    endTime = endTime, onEndTimeChange = { endTime = it },
                    skillInput = skillInput, onSkillInputChange = { skillInput = it },
                    skills = skills, onSkillsChange = { skills = it },
                    errorMessage = errorMessage,
                    isLoading = false,
                    onSave = {
                        errorMessage = null
                        when {
                            title.isBlank() -> errorMessage = "El título es requerido"
                            description.isBlank() -> errorMessage = "La descripción es requerida"
                            workDate.isBlank() -> errorMessage = "La fecha es requerida"
                            startTime.isBlank() -> errorMessage = "La hora de inicio es requerida"
                            endTime.isBlank() -> errorMessage = "La hora de fin es requerida"
                            payment.isBlank() -> errorMessage = "El monto es requerido"
                            else -> viewModel.saveJob(
                                jobId = jobId,
                                title = title,
                                description = description,
                                payment = payment.toDoubleOrNull() ?: 0.0,
                                paymentType = paymentType,
                                workDate = workDate,
                                startTime = startTime,
                                endTime = endTime,
                                deliverables = skills
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun EditJobForm(
    padding: PaddingValues,
    title: String, onTitleChange: (String) -> Unit,
    description: String, onDescriptionChange: (String) -> Unit,
    payment: String, onPaymentChange: (String) -> Unit,
    paymentType: String, onPaymentTypeChange: (String) -> Unit,
    paymentTypes: List<String>,
    workDate: String, onWorkDateChange: (String) -> Unit,
    startTime: String, onStartTimeChange: (String) -> Unit,
    endTime: String, onEndTimeChange: (String) -> Unit,
    skillInput: String, onSkillInputChange: (String) -> Unit,
    skills: List<String>, onSkillsChange: (List<String>) -> Unit,
    errorMessage: String?,
    isLoading: Boolean,
    onSave: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Información Básica
        EditSectionHeader(icon = { Icon(Icons.Outlined.BusinessCenter, null, tint = DarkBlue, modifier = Modifier.size(22.dp)) }, title = "Información Básica")

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Título del puesto", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            OutlinedTextField(
                value = title, onValueChange = onTitleChange,
                modifier = Modifier.fillMaxWidth(), singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )
            Text("Descripción completa", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            OutlinedTextField(
                value = description, onValueChange = onDescriptionChange,
                modifier = Modifier.fillMaxWidth().height(120.dp),
                maxLines = 5, shape = RoundedCornerShape(8.dp)
            )
        }

        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

        // Pago y Modalidad
        EditSectionHeader(icon = { Icon(Icons.Outlined.AttachMoney, null, tint = DarkBlue, modifier = Modifier.size(22.dp)) }, title = "Pago y Modalidad")

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            paymentTypes.forEach { type ->
                val selected = paymentType == type
                OutlinedButton(
                    onClick = { onPaymentTypeChange(type) },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (selected) Color.White else Color.Transparent,
                        contentColor = if (selected) DarkBlue else Color.Gray
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (selected) 2.dp else 1.dp,
                        color = if (selected) DarkBlue else Color.LightGray
                    )
                ) {
                    Text(type, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal)
                }
            }
        }

        OutlinedTextField(
            value = payment, onValueChange = onPaymentChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Monto estimado (₡)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            prefix = { Text("₡ ", fontWeight = FontWeight.Medium) },
            shape = RoundedCornerShape(8.dp)
        )

        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

        // Fecha y Horario
        EditSectionHeader(icon = { Icon(Icons.Outlined.CalendarMonth, null, tint = DarkBlue, modifier = Modifier.size(22.dp)) }, title = "Fecha")

        OutlinedTextField(
            value = workDate, onValueChange = onWorkDateChange,
            label = { Text("Fecha (YYYY-MM-DD)") },
            modifier = Modifier.fillMaxWidth(), singleLine = true,
            shape = RoundedCornerShape(8.dp)
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = startTime, onValueChange = onStartTimeChange,
                label = { Text("Hora inicio") },
                placeholder = { Text("08:00") },
                modifier = Modifier.weight(1f), singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )
            OutlinedTextField(
                value = endTime, onValueChange = onEndTimeChange,
                label = { Text("Hora fin") },
                placeholder = { Text("17:00") },
                modifier = Modifier.weight(1f), singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )
        }

        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

        // Requisitos
        EditSectionHeader(icon = { Icon(Icons.Outlined.Assignment, null, tint = DarkBlue, modifier = Modifier.size(22.dp)) }, title = "Requisitos específicos")

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = skillInput, onValueChange = onSkillInputChange,
                placeholder = { Text("Añadir requisito...", color = Color.Gray) },
                modifier = Modifier.weight(1f), singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )
            Button(
                onClick = {
                    if (skillInput.isNotBlank()) {
                        onSkillsChange(skills + skillInput.trim())
                        onSkillInputChange("")
                    }
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkBlue),
                contentPadding = PaddingValues(12.dp),
                modifier = Modifier.size(52.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar", tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            skills.forEachIndexed { index, skill ->
                Surface(
                    shape = RoundedCornerShape(20.dp), color = Color.White,
                    modifier = Modifier.border(1.dp, Color.LightGray, RoundedCornerShape(20.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(skill, style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            Icons.Default.Close, contentDescription = "Eliminar",
                            modifier = Modifier.size(16.dp).clickable {
                                onSkillsChange(skills.toMutableList().also { it.removeAt(index) })
                            },
                            tint = Color.Gray
                        )
                    }
                }
            }
        }

        errorMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Button(
            onClick = onSave,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DarkBlue),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
            } else {
                Text("Guardar Cambios", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun EditSectionHeader(icon: @Composable () -> Unit, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        icon()
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = Color.Black)
    }
}
