package com.moviles.jobmatch.ui.screens.job

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.moviles.jobmatch.ui.components.JobCard
import com.moviles.jobmatch.ui.components.JobMatchTextField
import com.moviles.jobmatch.ui.theme.DarkBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobsScreen(
    viewModel: JobsViewModel,
    onJobClick: (Int) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val categories = listOf("Todos", "Eventos", "Logística", "Oficina", "Más")

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFFDFDFD))) {
        CenterAlignedTopAppBar(
            title = { Text("Explorar", fontWeight = FontWeight.Bold) },
            actions = { IconButton(onClick = {}) { Icon(Icons.Default.Settings, null) } },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
        )

        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            JobMatchTextField(
                value = uiState.searchText,
                onValueChange = { viewModel.updateSearchText(it) },
                placeholder = "Puesto, empresa o palabra clave",
                leadingIcon = Icons.Default.Search,
                label = ""
            )
        }

        Text("Categorías Populares", Modifier.padding(16.dp, 8.dp), fontWeight = FontWeight.Bold)

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { cat ->
                FilterChip(
                    selected = cat == uiState.selectedCategory,
                    onClick = { viewModel.updateSelectedCategory(cat) },
                    label = { Text(cat) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = DarkBlue,
                        selectedLabelColor = Color.White
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = cat == uiState.selectedCategory,
                        borderColor = Color(0xFFEEEEEE),
                        borderWidth = 1.dp,
                        selectedBorderColor = DarkBlue,
                        disabledBorderColor = Color.LightGray,
                        disabledSelectedBorderColor = Color.LightGray
                    )
                )
            }
        }

        // TEXTO DE "MOSTRANDO X TRABAJOS" (Como en el prototipo)
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Mostrando ", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            Text(text = "${uiState.jobsCount} trabajos ", style = MaterialTheme.typography.bodyMedium, color = DarkBlue, fontWeight = FontWeight.Bold)
            Text(text = "disponibles", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        }

        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = DarkBlue)
            }
        } else {
            LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 16.dp)) {
                items(uiState.jobs) { job ->
                    JobCard(job = job, onSeeMoreClick = { onJobClick(job.idJob) })
                }
            }
        }
    }
}