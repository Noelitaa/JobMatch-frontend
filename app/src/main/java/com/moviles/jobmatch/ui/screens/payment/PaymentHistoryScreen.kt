package com.moviles.jobmatch.ui.screens.payment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.CallReceived
import androidx.compose.material.icons.filled.CallMade
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moviles.jobmatch.data.AuthSession
import com.moviles.jobmatch.data.remote.model.PaymentResponse
import com.moviles.jobmatch.data.repository.AppContainer
import com.moviles.jobmatch.ui.components.DatePickerField
import com.moviles.jobmatch.ui.components.SectionHeader
import com.moviles.jobmatch.ui.theme.DarkBlue
import com.moviles.jobmatch.ui.utils.formatApplicationDate
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentHistoryScreen(
    onBackPressed: () -> Unit = {}
) {
    val viewModel: PaymentHistoryViewModel = viewModel(
        factory = PaymentHistoryViewModel.Factory(AppContainer.paymentRepository)
    )
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showDateFilter by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Historial de Pagos", fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
                },
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
        when {
            state.isLoading -> {
                Box(
                    Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator(color = DarkBlue) }
            }

            state.errorMessage != null -> {
                Box(
                    Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            state.errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.load() },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkBlue)
                        ) { Text("Reintentar") }
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    item {
                        BalanceCard(
                            state = state,
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    item {
                        FilterRow(
                            activeFilter = state.filter,
                            hasDateFilter = state.startDate != null || state.endDate != null,
                            onFilterChange = viewModel::setFilter,
                            onDateFilterClick = { showDateFilter = true },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                    }

                    if (state.filtered.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Sin pagos registrados",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF9AA5B4)
                                )
                            }
                        }
                    } else {
                        val grouped = state.filtered.groupByDate()
                        grouped.forEach { (label, payments) ->
                            item {
                                SectionHeader(
                                    title = label,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                                )
                            }
                            items(payments, key = { it.idPayment }) { payment ->
                                PaymentItem(
                                    payment = payment,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDateFilter) {
        DateFilterDialog(
            initialStart = state.startDate,
            initialEnd = state.endDate,
            onApply = { start, end ->
                viewModel.applyDateRange(start, end)
                showDateFilter = false
            },
            onClear = {
                viewModel.clearDateRange()
                showDateFilter = false
            },
            onDismiss = { showDateFilter = false }
        )
    }
}

@Composable
private fun BalanceCard(state: PaymentHistoryUiState, modifier: Modifier = Modifier) {
    val isCompany = AuthSession.isCompany
    val mainAmount = if (isCompany) state.totalMade else state.totalReceived
    val label = if (isCompany) "TOTAL PAGADO" else "TOTAL RECIBIDO"

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.horizontalGradient(listOf(DarkBlue, Color(0xFF1976D2))))
            .padding(20.dp)
    ) {
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.8f),
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = formatColones(mainAmount),
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            if (!isCompany && state.totalMade > 0) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "Pagado: ${formatColones(state.totalMade)}",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterRow(
    activeFilter: PaymentFilter,
    hasDateFilter: Boolean,
    onFilterChange: (PaymentFilter) -> Unit,
    onDateFilterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        listOf(
            PaymentFilter.ALL to "Todos",
            PaymentFilter.RECEIVED to "Ingresos",
            PaymentFilter.MADE to "Egresos"
        ).forEach { (filter, label) ->
            FilterChip(
                selected = activeFilter == filter,
                onClick = { onFilterChange(filter) },
                label = { Text(label, style = MaterialTheme.typography.bodySmall) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = DarkBlue,
                    selectedLabelColor = Color.White
                )
            )
        }

        Spacer(Modifier.weight(1f))

        IconButton(onClick = onDateFilterClick, modifier = Modifier.size(36.dp)) {
            Icon(
                Icons.Default.FilterList,
                contentDescription = "Filtrar por fecha",
                tint = if (hasDateFilter) DarkBlue else Color(0xFF9AA5B4)
            )
        }
    }
}

@Composable
private fun PaymentItem(payment: PaymentResponse, modifier: Modifier = Modifier) {
    val isReceived = payment.type == "received"
    val amountColor = if (isReceived) Color(0xFF2E7D32) else Color(0xFFC62828)
    val amountPrefix = if (isReceived) "+" else "-"
    val icon = if (isReceived) Icons.Default.CallReceived else Icons.Default.CallMade
    val iconBg = if (isReceived) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
    val iconTint = if (isReceived) Color(0xFF2E7D32) else Color(0xFFC62828)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = payment.concept ?: "Pago de trabajo",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A2332)
                )
                Text(
                    text = "REF: JM-${String.format("%05d", payment.idContract)} · ${formatPaymentMethod(payment.paymentMethod)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF9AA5B4)
                )
            }

            Spacer(Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$amountPrefix${formatColones(payment.amount)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = amountColor
                )
                Text(
                    text = formatApplicationDate(payment.date),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF9AA5B4)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateFilterDialog(
    initialStart: String?,
    initialEnd: String?,
    onApply: (String?, String?) -> Unit,
    onClear: () -> Unit,
    onDismiss: () -> Unit
) {
    var startDate by remember { mutableStateOf(initialStart ?: "") }
    var endDate by remember { mutableStateOf(initialEnd ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filtrar por fecha", fontWeight = FontWeight.SemiBold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                DatePickerField(
                    label = "Desde",
                    value = startDate,
                    onDateSelected = { startDate = it },
                    modifier = Modifier.fillMaxWidth()
                )
                DatePickerField(
                    label = "Hasta",
                    value = endDate,
                    onDateSelected = { endDate = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onApply(
                    startDate.ifBlank { null },
                    endDate.ifBlank { null }
                )
            }) { Text("Aplicar") }
        },
        dismissButton = {
            Row {
                TextButton(onClick = onClear) { Text("Limpiar") }
                TextButton(onClick = onDismiss) { Text("Cancelar") }
            }
        }
    )
}

// Groups payments by date label: "HOY", "AYER", or "MES AÑO"
private fun List<PaymentResponse>.groupByDate(): LinkedHashMap<String, List<PaymentResponse>> {
    val result = LinkedHashMap<String, List<PaymentResponse>>()
    val today = java.util.Calendar.getInstance()
    val yesterday = java.util.Calendar.getInstance().apply { add(java.util.Calendar.DAY_OF_YEAR, -1) }

    forEach { payment ->
        val label = try {
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = sdf.parse(payment.date) ?: return@forEach
            val cal = java.util.Calendar.getInstance().apply { time = date }
            when {
                cal.get(java.util.Calendar.YEAR) == today.get(java.util.Calendar.YEAR) &&
                        cal.get(java.util.Calendar.DAY_OF_YEAR) == today.get(java.util.Calendar.DAY_OF_YEAR) -> "HOY"
                cal.get(java.util.Calendar.YEAR) == yesterday.get(java.util.Calendar.YEAR) &&
                        cal.get(java.util.Calendar.DAY_OF_YEAR) == yesterday.get(java.util.Calendar.DAY_OF_YEAR) -> "AYER"
                else -> java.text.SimpleDateFormat("MMMM yyyy", Locale("es")).format(date)
                    .replaceFirstChar { it.uppercase() }
            }
        } catch (e: Exception) { payment.date }

        result[label] = (result[label] ?: emptyList()) + payment
    }
    return result
}

private fun formatColones(amount: Double): String {
    val format = NumberFormat.getNumberInstance(Locale("es", "CR"))
    format.maximumFractionDigits = 0
    return "₡${format.format(amount)}"
}

private fun formatPaymentMethod(method: String): String = when (method.lowercase()) {
    "transfer" -> "Transferencia"
    "cash" -> "Efectivo"
    "sinpe" -> "SINPE Móvil"
    else -> method.replaceFirstChar { it.uppercase() }
}
