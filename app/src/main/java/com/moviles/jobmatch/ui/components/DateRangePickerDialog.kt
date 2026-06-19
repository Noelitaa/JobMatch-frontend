package com.moviles.jobmatch.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerDialog(
    initialStart: String?,
    initialEnd: String?,
    onApply: (startDate: String?, endDate: String?) -> Unit,
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
