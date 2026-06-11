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
    val formState by viewModel.formState.collectAsStateWithLifecycle()

    LaunchedEffect(jobId) {
        viewModel.loadJob(jobId)
    }

    LaunchedEffect(uiState) {
        if (uiState is EditJobUiState.SaveSuccess) onJobUpdated()
    }

    val paymentTypes = listOf("Hora", "Turno", "Proyecto")
    val isLoading = uiState is EditJobUiState.Loading

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
        when {
            uiState is EditJobUiState.Loading && formState.title.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator(color = DarkBlue) }
            }
            uiState is EditJobUiState.Error && formState.title.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            (uiState as EditJobUiState.Error).message,
                            color = Color.Gray,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.loadJob(jobId) },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkBlue)
                        ) { Text("Reintentar") }
                    }
                }
            }
            else -> {
                EditJobForm(
                    padding = padding,
                    formState = formState,
                    paymentTypes = paymentTypes,
                    isLoading = isLoading,
                    onTitleChange = viewModel::updateTitle,
                    onDescriptionChange = viewModel::updateDescription,
                    onPaymentChange = viewModel::updatePayment,
                    onPaymentTypeChange = viewModel::updatePaymentType,
                    onWorkDateChange = viewModel::updateWorkDate,
                    onStartTimeChange = viewModel::updateStartTime,
                    onEndTimeChange = viewModel::updateEndTime,
                    onSkillInputChange = viewModel::updateSkillInput,
                    onAddSkill = viewModel::addSkill,
                    onRemoveSkill = viewModel::removeSkill,
                    onSave = { viewModel.saveJob(jobId) }
                )
            }
        }
    }
}

@Composable
private fun EditJobForm(
    padding: PaddingValues,
    formState: EditFormState,
    paymentTypes: List<String>,
    isLoading: Boolean,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onPaymentChange: (String) -> Unit,
    onPaymentTypeChange: (String) -> Unit,
    onWorkDateChange: (String) -> Unit,
    onStartTimeChange: (String) -> Unit,
    onEndTimeChange: (String) -> Unit,
    onSkillInputChange: (String) -> Unit,
    onAddSkill: () -> Unit,
    onRemoveSkill: (Int) -> Unit,
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
        EditSectionHeader(
            icon = { Icon(Icons.Outlined.BusinessCenter, null, tint = DarkBlue, modifier = Modifier.size(22.dp)) },
            title = "Información Básica"
        )

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Título del puesto", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            OutlinedTextField(
                value = formState.title,
                onValueChange = onTitleChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )
            Text("Descripción completa", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            OutlinedTextField(
                value = formState.description,
                onValueChange = onDescriptionChange,
                modifier = Modifier.fillMaxWidth().height(120.dp),
                maxLines = 5,
                shape = RoundedCornerShape(8.dp)
            )
        }

        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

        EditSectionHeader(
            icon = { Icon(Icons.Outlined.AttachMoney, null, tint = DarkBlue, modifier = Modifier.size(22.dp)) },
            title = "Pago y Modalidad"
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            paymentTypes.forEach { type ->
                val selected = formState.paymentType == type
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
            value = formState.payment,
            onValueChange = onPaymentChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Monto estimado (₡)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            prefix = { Text("₡ ", fontWeight = FontWeight.Medium) },
            shape = RoundedCornerShape(8.dp)
        )

        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

        EditSectionHeader(
            icon = { Icon(Icons.Outlined.CalendarMonth, null, tint = DarkBlue, modifier = Modifier.size(22.dp)) },
            title = "Fecha"
        )

        OutlinedTextField(
            value = formState.workDate,
            onValueChange = onWorkDateChange,
            label = { Text("Fecha (YYYY-MM-DD)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(8.dp)
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = formState.startTime,
                onValueChange = onStartTimeChange,
                label = { Text("Hora inicio") },
                placeholder = { Text("08:00") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )
            OutlinedTextField(
                value = formState.endTime,
                onValueChange = onEndTimeChange,
                label = { Text("Hora fin") },
                placeholder = { Text("17:00") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )
        }

        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

        EditSectionHeader(
            icon = { Icon(Icons.Outlined.Assignment, null, tint = DarkBlue, modifier = Modifier.size(22.dp)) },
            title = "Requisitos específicos"
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = formState.skillInput,
                onValueChange = onSkillInputChange,
                placeholder = { Text("Añadir requisito...", color = Color.Gray) },
                modifier = Modifier.weight(1f),
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )
            Button(
                onClick = onAddSkill,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkBlue),
                contentPadding = PaddingValues(12.dp),
                modifier = Modifier.size(52.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar", tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            formState.skills.forEachIndexed { index, skill ->
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
                                .clickable { onRemoveSkill(index) },
                            tint = Color.Gray
                        )
                    }
                }
            }
        }

        formState.errorMessage?.let {
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
