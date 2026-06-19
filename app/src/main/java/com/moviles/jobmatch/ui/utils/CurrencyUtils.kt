package com.moviles.jobmatch.ui.utils

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

/**
 * Formats a double value to Costa Rican Colones currency format.
 * Example: 15000.0 -> ₡15.000
 */
fun formatColones(amount: Double): String {
    val symbols = DecimalFormatSymbols(Locale("es", "CR"))
    symbols.groupingSeparator = '.'
    symbols.decimalSeparator = ','
    val formatter = DecimalFormat("₡#,###", symbols)
    return formatter.format(amount.toLong())
}

/**
 * Formats a double value to string with thousands separator.
 * Example: 15000.0 -> 15.000
 */
fun formatAmountSimple(amount: Double): String {
    val symbols = DecimalFormatSymbols(Locale("es", "CR"))
    symbols.groupingSeparator = '.'
    val formatter = DecimalFormat("#,###", symbols)
    return formatter.format(amount.toLong())
}
