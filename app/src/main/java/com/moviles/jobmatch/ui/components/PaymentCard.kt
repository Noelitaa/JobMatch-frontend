package com.moviles.jobmatch.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallMade
import androidx.compose.material.icons.filled.CallReceived
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.moviles.jobmatch.data.remote.model.PaymentResponse
import com.moviles.jobmatch.ui.utils.formatApplicationDate
import java.text.NumberFormat
import java.util.Locale

@Composable
fun PaymentCard(
    payment: PaymentResponse,
    modifier: Modifier = Modifier
) {
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

fun formatColones(amount: Double): String {
    val format = NumberFormat.getNumberInstance(Locale("es", "CR"))
    format.maximumFractionDigits = 0
    return "₡${format.format(amount)}"
}

fun formatPaymentMethod(method: String): String = when (method.lowercase()) {
    "transfer" -> "Transferencia"
    "cash" -> "Efectivo"
    "sinpe" -> "SINPE Móvil"
    else -> method.replaceFirstChar { it.uppercase() }
}
