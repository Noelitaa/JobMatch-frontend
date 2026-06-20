package com.moviles.jobmatch.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

/**
 * A standard confirmation dialog for logging out.
 */
@Composable
fun LogoutConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Cerrar Sesión",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(text = "¿Estás seguro de que deseas cerrar la sesión?")
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = "Cerrar Sesión", color = Color(0xFFE53935), fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancelar")
            }
        }
    )
}
