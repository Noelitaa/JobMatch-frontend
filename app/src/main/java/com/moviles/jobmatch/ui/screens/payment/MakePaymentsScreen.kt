package com.moviles.jobmatch.ui.screens.payment

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moviles.jobmatch.data.repository.AppContainer
import com.moviles.jobmatch.ui.components.JobMatchButton
import com.moviles.jobmatch.ui.components.JobMatchTextField
import com.moviles.jobmatch.ui.components.JobMatchTopBar
import com.moviles.jobmatch.ui.theme.DarkBlue
import com.moviles.jobmatch.ui.utils.formatAmountSimple
import com.moviles.jobmatch.ui.utils.formatColones

// ── Color palette ──────────────────────────────────────────────────────────────
private val BgColor      = Color(0xFFF5F7FA)
private val TextMuted    = Color(0xFF9AA5B4)
private val TextDark     = Color(0xFF1A1A2E)

@Composable
fun MakePaymentScreen(
    jobId: Int,
    studentId: String,
    jobTitle: String,
    contractNumber: String,
    amount: Double,
    onBackPressed: () -> Unit = {},
    onPaymentSuccess: () -> Unit = {}
) {
    val vm: MakePaymentViewModel = viewModel(
        factory = MakePaymentViewModelFactory(AppContainer.paymentRepository)
    )
    val state by vm.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(jobId) { vm.initWithJob(jobId, studentId, jobTitle, amount, contractNumber) }

    if (state.paymentSuccess) {
        LaunchedEffect(Unit) { onPaymentSuccess() }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            vm.clearError()
        }
    }

    Scaffold(
        topBar = { JobMatchTopBar(title = "Realizar Pago", showBackButton = true, onBackPressed = onBackPressed) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = BgColor
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(12.dp))
            ServiceSummaryCard(state.jobTitle, contractNumber, state.amount, state.subtotal, state.commission, Modifier.padding(horizontal = 16.dp))
            Spacer(Modifier.height(20.dp))
            PaymentMethodHeader()
            Spacer(Modifier.height(10.dp))
            PaymentMethodSelector(state.selectedMethod, vm::onMethodSelected)
            Spacer(Modifier.height(16.dp))
            PaymentForm(state.amountString, vm::onAmountChanged, state.receiptUrl, vm::onReceiptUrlChanged, state.fieldErrors["amount"], state.fieldErrors["receiptUrl"], state.selectedMethod.label)
            Spacer(Modifier.height(16.dp))
            ProtectedPaymentBanner(Modifier.padding(horizontal = 16.dp))
            Spacer(Modifier.height(20.dp))
            PaymentConfirmSection(state.isLoading, state.amount, vm::submitPayment)
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ServiceSummaryCard(jobTitle: String, contractNumber: String, total: Double, subtotal: Double, commission: Double, modifier: Modifier) {
    Card(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = DarkBlue)) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(jobTitle, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    Text("Contrato: #$contractNumber", color = Color.White.copy(alpha = 0.75f), fontSize = 12.sp)
                }
            }
            Spacer(Modifier.height(16.dp))
            Text(formatColones(total), color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.Bold)
            Text("Total en Colones Costarricenses", color = Color.White.copy(alpha = 0.75f), fontSize = 12.sp)
            Spacer(Modifier.height(14.dp))
            Divider(color = Color.White.copy(alpha = 0.3f), thickness = 1.dp)
            Spacer(Modifier.height(12.dp))
            SummaryLine("Pago neto al estudiante", formatColones(subtotal))
            Spacer(Modifier.height(6.dp))
            SummaryLine("Comisión JobMatch (6%)", formatColones(commission))
        }
    }
}

@Composable
private fun SummaryLine(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp)
        Text(value, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun PaymentMethodHeader() {
    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text("Método de Pago", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.Shield, null, tint = DarkBlue, modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(4.dp))
            Text("Seguro", color = DarkBlue, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun PaymentMethodSelector(selectedMethod: PaymentMethod, onMethodSelected: (PaymentMethod) -> Unit) {
    Column(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        PaymentMethod.entries.forEach { method ->
            PaymentMethodCard(method, selectedMethod == method, onClick = { onMethodSelected(method) })
        }
    }
}

@Composable
private fun PaymentMethodCard(method: PaymentMethod, selected: Boolean, onClick: () -> Unit) {
    val icon = when (method) {
        PaymentMethod.SINPE -> Icons.Default.Phone
        PaymentMethod.CASH -> Icons.Default.Check
        PaymentMethod.BANK_TRANSFER -> Icons.Default.AccountBalance
    }
    Card(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).border(width = if (selected) 2.dp else 1.dp, color = if (selected) DarkBlue else Color(0xFFDDE1E7), shape = RoundedCornerShape(14.dp)).clickable { onClick() },
        shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(42.dp).clip(RoundedCornerShape(10.dp)).background(if (selected) DarkBlue else Color(0xFFF0F4FF)), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = if (selected) Color.White else DarkBlue, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(method.label, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TextDark)
                Text(method.subtitle, fontSize = 12.sp, color = TextMuted)
            }
            RadioButton(selected = selected, onClick = onClick, colors = RadioButtonDefaults.colors(selectedColor = DarkBlue))
        }
    }
}

@Composable
private fun PaymentForm(amount: String, onAmountChange: (String) -> Unit, receipt: String, onReceiptChange: (String) -> Unit, amountError: String?, receiptError: String?, method: String) {
    FormSection("Detalles del Pago ($method)") {
        JobMatchTextField(
            value = amount,
            onValueChange = onAmountChange,
            placeholder = "0",
            label = "Monto a pagar (₡)",
            leadingIcon = Icons.Default.AttachMoney,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = amountError != null
        )
        if (amountError != null) Text(amountError, color = MaterialTheme.colorScheme.error, fontSize = 11.sp, modifier = Modifier.padding(start = 4.dp, top = 2.dp))
        Spacer(Modifier.height(12.dp))
        JobMatchTextField(
            value = receipt,
            onValueChange = onReceiptChange,
            placeholder = "Número de transacción o URL",
            label = "Referencia / Comprobante",
            leadingIcon = Icons.Default.Receipt,
            isError = receiptError != null
        )
        if (receiptError != null) Text(receiptError, color = MaterialTheme.colorScheme.error, fontSize = 11.sp, modifier = Modifier.padding(start = 4.dp, top = 2.dp))
    }
}

@Composable
private fun ProtectedPaymentBanner(modifier: Modifier) {
    Card(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FFF4))) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
            Icon(Icons.Default.Check, null, tint = Color(0xFF2ECC71), modifier = Modifier.size(20.dp).clip(RoundedCornerShape(50)).background(Color(0xFF2ECC71).copy(alpha = 0.15f)).padding(2.dp))
            Spacer(Modifier.width(10.dp))
            Column {
                Text("Pago Protegido", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Color(0xFF1A472A))
                Text("Tu dinero se mantiene en custodia hasta que el trabajo sea completado y verificado.", fontSize = 12.sp, color = Color(0xFF2D6A4F), lineHeight = 16.sp)
            }
        }
    }
}

@Composable
private fun PaymentConfirmSection(isLoading: Boolean, amount: Double, onConfirm: () -> Unit) {
    Column(Modifier.padding(horizontal = 16.dp)) {
        JobMatchButton(text = if (isLoading) "Procesando…" else "Confirmar y Pagar ${formatColones(amount)}", onClick = onConfirm, enabled = !isLoading)
        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Lock, null, tint = TextMuted, modifier = Modifier.size(12.dp))
            Spacer(Modifier.width(4.dp))
            Text("TRANSACCIÓN SEGURA SSL", fontSize = 10.sp, color = TextMuted, letterSpacing = 0.8.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun FormSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.padding(horizontal = 16.dp)) {
        Text(title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = TextDark)
        Spacer(Modifier.height(10.dp))
        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(1.dp)) {
            Column(Modifier.padding(16.dp)) { content() }
        }
    }
}
