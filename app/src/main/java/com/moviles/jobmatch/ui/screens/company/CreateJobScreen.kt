package com.moviles.jobmatch.ui.screens.company

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

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
            is CreateJobUiState.Success -> {
                onJobCreated()
                viewModel.resetState()
            }
            is CreateJobUiState.Error -> {
                errorMessage = (uiState as CreateJobUiState.Error).message
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Publicar Trabajo") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.Close, contentDescription = "Volver")
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Información Básica
            Text("Información Básica", style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary)

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título del puesto") },
                placeholder = { Text("Ej. Auxiliar de Eventos, Repartidor...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción completa") },
                placeholder = { Text("Describe las tareas y responsabilidades...") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                maxLines = 5
            )

            // Pago y Modalidad
            Text("Pago y Modalidad", style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                paymentTypes.forEach { type ->
                    FilterChip(
                        selected = paymentType == type,
                        onClick = { paymentType = type },
                        label = { Text(type) }
                    )
                }
            }

            OutlinedTextField(
                value = payment,
                onValueChange = { payment = it },
                label = { Text("Monto estimado (₡)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                prefix = { Text("₡ ") }
            )

            // Fecha y Horario
            Text("Fecha y Horario", style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary)

            OutlinedTextField(
                value = workDate,
                onValueChange = { workDate = it },
                label = { Text("Fecha (YYYY-MM-DD)") },
                placeholder = { Text("Ej. 2026-06-15") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = startTime,
                    onValueChange = { startTime = it },
                    label = { Text("Hora inicio") },
                    placeholder = { Text("08:00") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = endTime,
                    onValueChange = { endTime = it },
                    label = { Text("Hora fin") },
                    placeholder = { Text("17:00") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            // Requisitos / Skills
            Text("Requisitos específicos", style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = skillInput,
                    onValueChange = { skillInput = it },
                    label = { Text("Añadir requisito") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                IconButton(
                    onClick = {
                        if (skillInput.isNotBlank()) {
                            skills = skills + skillInput.trim()
                            skillInput = ""
                        }
                    }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar")
                }
            }

            skills.forEach { skill ->
                InputChip(
                    selected = false,
                    onClick = {},
                    label = { Text(skill) },
                    trailingIcon = {
                        IconButton(onClick = { skills = skills - skill }) {
                            Icon(Icons.Default.Close, contentDescription = "Eliminar",
                                modifier = Modifier.size(16.dp))
                        }
                    }
                )
            }

            // Error
            errorMessage?.let {
                Text(it, color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(8.dp))

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
                enabled = uiState !is CreateJobUiState.Loading
            ) {
                if (uiState is CreateJobUiState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Publicar Vacante")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}