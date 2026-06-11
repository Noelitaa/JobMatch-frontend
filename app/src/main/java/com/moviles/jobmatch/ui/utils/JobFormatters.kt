package com.moviles.jobmatch.ui.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatJobDate(dateStr: String): String {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = sdf.parse(dateStr) ?: return dateStr
        val today = Date()
        val diffMs = date.time - today.time
        val diffDays = (diffMs / (1000 * 60 * 60 * 24)).toInt()
        when {
            diffDays == 0 -> "Hoy"
            diffDays == 1 -> "Mañana"
            diffDays == -1 -> "Ayer"
            diffDays > 0 -> "En $diffDays días"
            else -> SimpleDateFormat("dd 'de' MMM", Locale("es")).format(date)
        }
    } catch (e: Exception) {
        dateStr
    }
}

fun formatApplicationDate(isoDate: String): String {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val date = sdf.parse(isoDate) ?: return isoDate
        SimpleDateFormat("dd 'de' MMM yyyy", Locale("es")).format(date)
    } catch (e: Exception) {
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = sdf.parse(isoDate) ?: return isoDate
            SimpleDateFormat("dd 'de' MMM yyyy", Locale("es")).format(date)
        } catch (e2: Exception) {
            isoDate
        }
    }
}

fun formatPaymentType(type: String): String = when (type.lowercase()) {
    "fixed", "fijo" -> "Fijo"
    "hourly", "hora", "por hora" -> "Por hora"
    "daily", "diario" -> "Por día"
    "weekly", "semanal" -> "Semanal"
    else -> type.replaceFirstChar { it.uppercase() }
}
