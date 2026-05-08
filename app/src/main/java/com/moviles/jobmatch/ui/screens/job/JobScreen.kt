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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moviles.jobmatch.data.Job
import com.moviles.jobmatch.ui.components.JobCard
import com.moviles.jobmatch.ui.components.JobMatchTextField
import com.moviles.jobmatch.ui.theme.DarkBlue

private val fakeJobs = listOf(
    Job(1, "Asistente de Eventos (Finde)", "Producciones Atlas", 95, "6h diarias", 1.2, "Pago por hora", 2500),
    Job(2, "Repartidor Universitario", "QuickDrop CR", 88, "Flexible", 0.5, "Pago por hora", 1800),
    Job(3, "Apoyo Administrativo", "Bufete Solano", 72, "4h diarias", 3.5, "Pago por hora", 3200),
    Job(4, "Promotor de Marca Tech", "Innovación Digital", 91, "8h (Sábado)", 2.1, "Pago por hora", 2800)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobsScreen(
    onJobClick: (Int) -> Unit = {}
) {
    var selectedCategory by remember { mutableStateOf("Todos") }
    val categories = listOf("Todos", "Eventos", "Logística", "Oficina", "Más")


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDFDFD))
    ) {
        CenterAlignedTopAppBar(
            title = {
                Text("Explorar", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
            },
            actions = {
                IconButton(onClick = { /* Ajustes */ }) {
                    Icon(Icons.Default.Settings, contentDescription = null, tint = Color.Black)
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
        )

        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            JobMatchTextField(
                value = "",
                onValueChange = {},
                placeholder = "Puesto, empresa o palabra clave",
                leadingIcon = Icons.Default.Search,
                label = ""
            )
        }

        Text(
            text = "Categorías Populares",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                val isSelected = category == selectedCategory
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedCategory = category },
                    label = { Text(category) },
                    shape = RoundedCornerShape(20.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = DarkBlue,
                        selectedLabelColor = Color.White
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = Color(0xFFE0E0E0),
                        borderWidth = 1.dp,
                        selectedBorderColor = Color.Transparent
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Mostrando ${fakeJobs.size} trabajos disponibles",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            Surface(
                color = Color(0xFFE3F2FD),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                    Icon(Icons.Default.LocationOn, null, tint = DarkBlue, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Cerca de Heredia", color = DarkBlue, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(fakeJobs) { job ->
                JobCard(
                    job = job,
                    onSeeMoreClick = { onJobClick(job.id) }
                )
            }
        }
    }
}