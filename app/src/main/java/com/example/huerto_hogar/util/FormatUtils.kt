package com.example.huerto_hogar.util

import java.text.NumberFormat
import java.util.Locale

/**
 * Utilidades para formateo de datos en la aplicación
 */
object FormatUtils {
    
    /**
     * Formatea un precio a formato chileno con separadores de miles
     * Ejemplo: 1200.0 -> "$1.200"
     */
    fun formatPrice(price: Double): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
        return formatter.format(price).replace("CLP", "").trim()
    }

    
    /**
     * Formatea un precio con su unidad
     * Ejemplo: 1200.0, "kg" -> "$1.200/kg"
     */
    fun formatPriceWithUnit(price: Double, unit: String): String {
        return "${formatPrice(price)}/$unit"
    }
}