package com.moviles.jobmatch.ui.screens.availability

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moviles.jobmatch.data.repository.AppContainer
import com.moviles.jobmatch.ui.components.JobMatchButton
import com.moviles.jobmatch.ui.components.JobMatchTopBar
import com.moviles.jobmatch.ui.components.SectionHeader
import com.moviles.jobmatch.ui.theme.DarkBlue

private val DAY_LABELS = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")

private val BgColor        = Color(0xFFF5F7FA)
private val CardWhite      = Color.White
private val CellSelected   = DarkBlue
private val CellUnselected = Color(0xFFEEF0F3)
private val TextMuted      = Color(0xFF9AA5B4)
private val TextDark       = Color(0xFF1A1A2E)

@Composable
fun AvailabilityScreen(
    onBackPressed: () -> Unit = {},
    onSaved: () -> Unit = {}
) {
    val vm: AvailabilityViewModel = viewModel(
        factory = AvailabilityViewModelFactory(AppContainer.studentRepository)
    )
    val state by vm.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.saveSuccess) {
        if (state.saveSuccess) onSaved()
    }

    Scaffold(
        topBar = {
            JobMatchTopBar(
                title = "Mi Disponibilidad",
                showBackButton = true,
                onBackPressed = onBackPressed
            )
        },
        containerColor = BgColor,
        contentWindowInsets = WindowInsets(0)
    ) { padding ->
        when {
            state.isLoading -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = DarkBlue)
                }
            }

            state.errorMessage != null -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(24.dp)
                    )
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    WeeklyScheduleCard(
                        grid = state.grid,
                        onToggle = { day, block -> vm.toggleCell(day, block) }
                    )

                    SummarySection(blockActiveDays = state.blockActiveDays)


                    JobMatchButton(
                        text = "Guardar disponibilidad",
                        onClick = { vm.saveAvailability() },
                        enabled = !state.isSaving,
                        isLoading = state.isSaving,
                        leadingIcon = Icons.Default.Check,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    )

                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun WeeklyScheduleCard(
    grid: List<List<Boolean>>,
    onToggle: (dayIndex: Int, blockIndex: Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = null,
                        tint = DarkBlue,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "Horario Semanal",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = TextDark
                    )
                }
                Text(
                    "Toca para activar/desactivar",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontSize = 10.sp
                )
            }

            Spacer(Modifier.height(14.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Spacer(Modifier.width(56.dp))
                DAY_LABELS.forEach { day ->
                    Text(
                        text = day,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontWeight = FontWeight.Medium,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            TimeBlock.entries.forEachIndexed { blockIndex, block ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = block.label,
                        modifier = Modifier.width(56.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF5A6A7A),
                        fontWeight = FontWeight.Medium,
                        fontSize = 11.sp
                    )

                    for (dayIndex in 0..6) {
                        val selected = grid.getOrNull(dayIndex)?.getOrNull(blockIndex) ?: false
                        GridCell(
                            selected = selected,
                            onClick = { onToggle(dayIndex, blockIndex) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GridCell(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(2.dp)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(if (selected) CellSelected else CellUnselected)
            .then(
                if (!selected) Modifier.border(1.dp, Color(0xFFDDE1E7), RoundedCornerShape(8.dp))
                else Modifier
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Icon(
                Icons.Default.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}


@Composable
private fun SummarySection(blockActiveDays: List<Int>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionHeader(title = "Resumen de franjas")
        TimeBlock.entries.forEachIndexed { index, block ->
            SummaryRow(
                block = block,
                activeDays = blockActiveDays.getOrElse(index) { 0 }
            )
        }
    }
}

@Composable
private fun SummaryRow(block: TimeBlock, activeDays: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF0F4FF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Schedule,
                    contentDescription = null,
                    tint = DarkBlue,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    block.label,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = TextDark
                )
                Text(
                    block.range,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }

            Text(
                text = if (activeDays == 0) "Sin días activos"
                else "$activeDays ${if (activeDays == 1) "día activo" else "días activos"}",
                style = MaterialTheme.typography.bodySmall,
                color = if (activeDays == 0) TextMuted else DarkBlue,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
