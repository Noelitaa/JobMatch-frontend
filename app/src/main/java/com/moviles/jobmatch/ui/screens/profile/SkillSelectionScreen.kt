package com.moviles.jobmatch.ui.screens.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moviles.jobmatch.data.remote.model.StudentSkillResponse
import com.moviles.jobmatch.data.repository.AppContainer
import com.moviles.jobmatch.ui.components.JobMatchButton
import com.moviles.jobmatch.ui.components.JobMatchTopBar
import com.moviles.jobmatch.ui.components.SectionHeader
import com.moviles.jobmatch.ui.theme.DarkBlue
import com.moviles.jobmatch.ui.theme.JobMatchTheme
import com.moviles.jobmatch.ui.theme.LightBlue

@Composable
fun SkillSelectionScreen(
    studentId: String,
    currentSkills: List<StudentSkillResponse>,
    onBackPressed: () -> Unit = {},
    onSaveSuccess: () -> Unit = {}
) {
    val viewModel: SkillSelectionViewModel = viewModel(
        factory = SkillSelectionViewModelFactory(AppContainer.skillRepository, currentSkills)
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            snackbarHostState.showSnackbar(
                message = "Habilidades guardadas",
                duration = SnackbarDuration.Short
            )
            onSaveSuccess()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        if (uiState.errorMessage != null && uiState.skills.isNotEmpty()) {
            snackbarHostState.showSnackbar(
                message = uiState.errorMessage!!,
                duration = SnackbarDuration.Short
            )
        }
    }

    LaunchedEffect(uiState.errorRemoveMessage) {
        if (uiState.errorRemoveMessage != null) {
            snackbarHostState.showSnackbar(
                message = uiState.errorRemoveMessage!!,
                duration = SnackbarDuration.Short
            )
        }
    }

    SkillSelectionContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onBackPressed = onBackPressed,
        onToggleSkill = { viewModel.toggleSkill(it) },
        onSave = { viewModel.saveSkills(studentId) },
        onRetry = { viewModel.loadSkills() },
        onRemoveSkill = { skill ->
            viewModel.removeSkill(studentId, skill.skillId, skill.skillName)
        }
    )
}

@Composable
private fun SkillSelectionContent(
    uiState: SkillSelectionUiState,
    snackbarHostState: SnackbarHostState,
    onBackPressed: () -> Unit,
    onToggleSkill: (String) -> Unit,
    onSave: () -> Unit,
    onRetry: () -> Unit,
    onRemoveSkill: (StudentSkillResponse) -> Unit
) {
    val expandedMap = remember { mutableStateMapOf<Char, Boolean>() }

    Scaffold(
        topBar = {
            JobMatchTopBar(
                title = "Agregar skills",
                showBackButton = true,
                onBackPressed = onBackPressed,
                onSettingsPressed = {}
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFFF5F7FA),
        contentWindowInsets = WindowInsets(0)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // --- Habilidades actuales ---
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    SectionHeader(title = "Habilidades actuales")
                    Spacer(modifier = Modifier.height(8.dp))
                    if (uiState.currentSkills.isEmpty()) {
                        Text(
                            text = "Sin habilidades registradas",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF9AA5B4)
                        )
                    } else {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            FlowRow(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                uiState.currentSkills.forEach { skill ->
                                    CurrentSkillChip(
                                        text = skill.skillName,
                                        isRemoving = uiState.removingSkill == skill.skillName,
                                        onRemove = { onRemoveSkill(skill) }
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- Agregar habilidades ---
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    SectionHeader(title = "Agregar habilidades")
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        when {
                            uiState.isLoading -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(color = DarkBlue)
                                }
                            }

                            uiState.skills.isEmpty() && uiState.errorMessage != null -> {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(
                                        text = uiState.errorMessage!!,
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodyMedium,
                                        textAlign = TextAlign.Center
                                    )
                                    Button(
                                        onClick = onRetry,
                                        colors = ButtonDefaults.buttonColors(containerColor = DarkBlue),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text("Reintentar")
                                    }
                                }
                            }

                            else -> {
                                val currentNames = uiState.currentSkills.map { it.skillName }.toSet()
                                val availableSkills = uiState.skills.filter { it !in currentNames }
                                if (availableSkills.isEmpty()) {
                                    Text(
                                        text = "Ya tienes todas las habilidades disponibles",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFF9AA5B4),
                                        modifier = Modifier.padding(12.dp)
                                    )
                                } else {
                                    val grouped = availableSkills
                                        .groupBy { it.first().uppercaseChar() }
                                        .toSortedMap()
                                    Column {
                                        grouped.forEach { (letter, skills) ->
                                            val isExpanded = expandedMap[letter] ?: false
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable { expandedMap[letter] = !isExpanded }
                                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = letter.toString(),
                                                    style = MaterialTheme.typography.titleSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = DarkBlue,
                                                    modifier = Modifier.weight(1f)
                                                )
                                                Icon(
                                                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                                    contentDescription = if (isExpanded) "Colapsar" else "Expandir",
                                                    tint = DarkBlue,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                            if (isExpanded) {
                                                FlowRow(
                                                    modifier = Modifier.padding(
                                                        start = 12.dp,
                                                        end = 12.dp,
                                                        bottom = 10.dp
                                                    ),
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    skills.forEach { skill ->
                                                        SelectableSkillChip(
                                                            text = skill,
                                                            isSelected = skill in uiState.selectedSkills,
                                                            onClick = { onToggleSkill(skill) }
                                                        )
                                                    }
                                                }
                                            }
                                            HorizontalDivider(color = Color(0xFFEEEEEE))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Fixed bottom button
            Surface(
                shadowElevation = 8.dp,
                color = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .navigationBarsPadding()
                ) {
                    JobMatchButton(
                        text = if (uiState.isSaving) "Guardando..." else "Guardar",
                        onClick = onSave,
                        enabled = uiState.selectedSkills.isNotEmpty() && !uiState.isSaving
                    )
                }
            }
        }
    }
}

@Composable
private fun CurrentSkillChip(
    text: String,
    isRemoving: Boolean,
    onRemove: () -> Unit
) {
    Surface(
        shape = CircleShape,
        color = DarkBlue
    ) {
        Row(
            modifier = Modifier.padding(start = 12.dp, end = 8.dp, top = 6.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
            if (isRemoving) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(14.dp),
                    strokeWidth = 1.5.dp
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Eliminar $text",
                    tint = Color.White,
                    modifier = Modifier
                        .size(14.dp)
                        .clickable(role = Role.Button, onClick = onRemove)
                )
            }
        }
    }
}

@Composable
private fun SelectableSkillChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) DarkBlue else LightBlue
    val contentColor = if (isSelected) Color.White else DarkBlue

    Surface(
        shape = CircleShape,
        color = backgroundColor,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = contentColor,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SkillSelectionScreenPreview() {
    val currentSkills = listOf(
        StudentSkillResponse("1", "JavaScript"),
        StudentSkillResponse("2", "Kotlin")
    )
    val previewUiState = SkillSelectionUiState(
        isLoading = false,
        skills = listOf(
            "Angular", "AWS",
            "Docker", "Django",
            "Figma", "Firebase",
            "Git", "GraphQL",
            "Java", "JavaScript",
            "Kotlin", "Kubernetes",
            "Node.js",
            "Python", "PostgreSQL",
            "React", "Redis",
            "Spring Boot", "SQL",
            "TypeScript"
        ),
        currentSkills = currentSkills,
        selectedSkills = setOf("Docker", "Python", "React"),
        errorMessage = null,
        isSaving = false,
        saveSuccess = false,
        removingSkill = "JavaScript"
    )
    val snackbarHostState = remember { SnackbarHostState() }

    JobMatchTheme {
        SkillSelectionContent(
            uiState = previewUiState,
            snackbarHostState = snackbarHostState,
            onBackPressed = {},
            onToggleSkill = {},
            onSave = {},
            onRetry = {},
            onRemoveSkill = {}
        )
    }
}
