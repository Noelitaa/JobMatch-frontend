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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.AccountBalance
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moviles.jobmatch.data.repository.AppContainer
import com.moviles.jobmatch.ui.components.JobMatchButton
import com.moviles.jobmatch.ui.components.JobMatchTopBar
import com.moviles.jobmatch.ui.theme.DarkBlue

// ── Color palette ──────────────────────────────────────────────────────────────
private val BgColor      = Color(0xFFF5F7FA)
private val CardBlue     = DarkBlue
private val TextMuted    = Color(0xFF9AA5B4)
private val TextDark     = Color(0xFF1A1A2E)
private val SelectedBorder = DarkBlue
private val UnselectedBorder = Color(0xFFDDE1E7)
private val SuccessGreen = Color(0xFF2ECC71)

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

    // Seed job data once
    LaunchedEffect(jobId) {
        vm.initWithJob(jobId, studentId, jobTitle, amount, contractNumber)
    }

    // Navigate away on success
    if (state.paymentSuccess) {
        LaunchedEffect(Unit) { onPaymentSuccess() }
    }

    // Error snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            vm.clearError()
        }
    }

    Scaffold(
        topBar = {
            JobMatchTopBar(
                title = "Realizar Pago",
                showBackButton = true,
                onBackPressed = onBackPressed
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = BgColor,
        contentWindowInsets = WindowInsets(0)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(12.dp))

            // ── Service summary card ───────────────────────────────────────────
            ServiceSummaryCard(
                jobTitle = state.jobTitle,
                contractNumber = contractNumber,
                total = state.amount,
                subtotal = state.subtotal,
                commission = state.commission,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(Modifier.height(20.dp))

            // ── Payment method section header ──────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Método de Pago",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextDark
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.Shield,
                        contentDescription = null,
                        tint = DarkBlue,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Seguro", color = DarkBlue, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }

            Spacer(Modifier.height(10.dp))

            // ── Method selector cards ──────────────────────────────────────────
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PaymentMethod.entries.forEach { method ->
                    PaymentMethodCard(
                        method = method,
                        selected = state.selectedMethod == method,
                        onClick = { vm.onMethodSelected(method) }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Dynamic detail form by method ──────────────────────────────────
            PaymentForm(
                amount = state.amountString,
                onAmountChange = vm::onAmountChanged,
                receiptUrl = state.receiptUrl,
                onReceiptUrlChange = vm::onReceiptUrlChanged,
                amountError = state.fieldErrors["amount"],
                receiptError = state.fieldErrors["receiptUrl"],
                methodLabel = state.selectedMethod.label
            )

            Spacer(Modifier.height(16.dp))

            // ── Protected payment notice ───────────────────────────────────────
            ProtectedPaymentBanner(modifier = Modifier.padding(horizontal = 16.dp))

            Spacer(Modifier.height(20.dp))

            // ── Confirm button ─────────────────────────────────────────────────
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                JobMatchButton(
                    text = if (state.isLoading) "Procesando…"
                    else "Confirmar y Pagar ₡${formatAmount(state.amount)}",
                    onClick = { vm.submitPayment() },
                    enabled = !state.isLoading
                )
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "TRANSACCIÓN SEGURA SSL",
                        fontSize = 10.sp,
                        color = TextMuted,
                        letterSpacing = 0.8.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

// ── Service Summary Card ───────────────────────────────────────────────────────
@Composable
private fun ServiceSummaryCard(
    jobTitle: String,
    contractNumber: String,
    total: Double,
    subtotal: Double,
    commission: Double,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBlue),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            // Title row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        jobTitle,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                    Text(
                        "Contrato: #$contractNumber",
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Total amount
            Text(
                "₡${formatAmount(total)}",
                color = Color.White,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Total en Colones Costarricenses",
                color = Color.White.copy(alpha = 0.75f),
                fontSize = 12.sp
            )

            Spacer(Modifier.height(14.dp))
            Divider(color = Color.White.copy(alpha = 0.3f), thickness = 1.dp)
            Spacer(Modifier.height(12.dp))

            // Subtotal / commission breakdown
            SummaryLine("Pago neto al estudiante", "₡${formatAmount(subtotal)}")
            Spacer(Modifier.height(6.dp))
            SummaryLine("Comisión JobMatch (6%)", "₡${formatAmount(commission)}")
        }
    }
}

@Composable
private fun SummaryLine(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp)
        Text(value, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

// ── Payment Method Card ────────────────────────────────────────────────────────
@Composable
private fun PaymentMethodCard(
    method: PaymentMethod,
    selected: Boolean,
    onClick: () -> Unit
) {
    val icon: ImageVector = when (method) {
        PaymentMethod.SINPE         -> Icons.Default.Phone
        PaymentMethod.CASH          -> Icons.Default.Check
        PaymentMethod.BANK_TRANSFER -> Icons.Default.AccountBalance
    }

    val iconBg = if (selected) DarkBlue else Color(0xFFF0F4FF)
    val iconTint = if (selected) Color.White else DarkBlue

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) SelectedBorder else UnselectedBorder,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(if (selected) 2.dp else 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon container
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(22.dp))
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(method.label, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TextDark)
                Text(method.subtitle, fontSize = 12.sp, color = TextMuted)
            }

            // Radio button indicator
            RadioButton(
                selected = selected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = DarkBlue)
            )
        }
    }
}

// ── Payment Form ───────────────────────────────────────────────────────────────
@Composable
private fun PaymentForm(
    amount: String,
    onAmountChange: (String) -> Unit,
    receiptUrl: String,
    onReceiptUrlChange: (String) -> Unit,
    amountError: String?,
    receiptError: String?,
    methodLabel: String
) {
    FormSection(title = "Detalles del Pago ($methodLabel)") {
        PaymentTextField(
            value = amount,
            onValueChange = onAmountChange,
            label = "Monto a pagar (₡)",
            placeholder = "0",
            keyboardType = KeyboardType.Number,
            isError = amountError != null,
            errorMessage = amountError,
            leadingIcon = {
                Text("₡", modifier = Modifier.padding(start = 12.dp), color = TextMuted, fontWeight = FontWeight.Bold)
            }
        )

        Spacer(Modifier.height(12.dp))

        PaymentTextField(
            value = receiptUrl,
            onValueChange = onReceiptUrlChange,
            label = "Referencia / Comprobante",
            placeholder = "Número de transacción o URL",
            keyboardType = KeyboardType.Text,
            isError = receiptError != null,
            errorMessage = receiptError
        )
    }
}

// ── SINPE Form ─────────────────────────────────────────────────────────────────
// Removed as it is now integrated into the generic PaymentForm

// ── Cash Form ──────────────────────────────────────────────────────────────────
// Removed as it is now integrated into the generic PaymentForm

// ── Bank Transfer Form ─────────────────────────────────────────────────────────
// Removed as it is now integrated into the generic PaymentForm

// ── Protected payment banner ───────────────────────────────────────────────────
@Composable
private fun ProtectedPaymentBanner(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FFF4)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                Icons.Default.Check,
                contentDescription = null,
                tint = SuccessGreen,
                modifier = Modifier
                    .size(20.dp)
                    .clip(RoundedCornerShape(50))
                    .background(SuccessGreen.copy(alpha = 0.15f))
                    .padding(2.dp)
            )
            Spacer(Modifier.width(10.dp))
            Column {
                Text(
                    "Pago Protegido",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = Color(0xFF1A472A)
                )
                Text(
                    "Tu dinero se mantiene en custodia hasta que el trabajo sea completado y verificado.",
                    fontSize = 12.sp,
                    color = Color(0xFF2D6A4F),
                    lineHeight = 16.sp
                )
            }
        }
    }
}

// ── Reusable form section wrapper ──────────────────────────────────────────────
@Composable
private fun FormSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            title,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            color = TextDark
        )
        Spacer(Modifier.height(10.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                content()
            }
        }
    }
}

// ── Reusable text field for payment forms ──────────────────────────────────────
@Composable
private fun PaymentTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    keyboardType: KeyboardType,
    isError: Boolean = false,
    errorMessage: String? = null,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    visualTransformation: androidx.compose.ui.text.input.VisualTransformation =
        androidx.compose.ui.text.input.VisualTransformation.None
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label, fontSize = 13.sp) },
            placeholder = { Text(placeholder, color = TextMuted, fontSize = 13.sp) },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            visualTransformation = visualTransformation,
            isError = isError,
            leadingIcon = leadingIcon,
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = DarkBlue,
                unfocusedBorderColor = UnselectedBorder,
                errorBorderColor = Color(0xFFE53935)
            ),
            modifier = Modifier.fillMaxWidth()
        )
        if (isError && errorMessage != null) {
            Text(
                errorMessage,
                color = Color(0xFFE53935),
                fontSize = 11.sp,
                modifier = Modifier.padding(start = 4.dp, top = 2.dp)
            )
        }
    }
}

// ── Amount formatter ───────────────────────────────────────────────────────────
private fun formatAmount(amount: Double): String {
    val long = amount.toLong()
    return "%,d".format(long).replace(",", ".")
}