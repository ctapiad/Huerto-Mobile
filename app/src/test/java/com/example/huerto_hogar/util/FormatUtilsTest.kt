package com.example.huerto_hogar.util

import org.junit.Test
import org.junit.Assert.*

class FormatUtilsTest {

    @Test
    fun `formatPrice formatea correctamente pesos chilenos`() {
        // Given & When
        val result1 = FormatUtils.formatPrice(1200.0)
        val result2 = FormatUtils.formatPrice(50000.0)
        val result3 = FormatUtils.formatPrice(1500.50)

        // Then
        assertEquals("$1.200", result1)
        assertEquals("$50.000", result2)
        assertTrue(result3.contains("1.500") || result3.contains("1.501"))
    }

    @Test
    fun `formatPrice con cero retorna formato correcto`() {
        // Given & When
        val result = FormatUtils.formatPrice(0.0)

        // Then
        assertEquals("$0", result)
    }

    @Test
    fun `formatPrice con numeros grandes`() {
        // Given & When
        val result = FormatUtils.formatPrice(1000000.0)

        // Then
        assertEquals("$1.000.000", result)
    }

    @Test
    fun `formatPriceWithUnit combina precio y unidad correctamente`() {
        // Given & When
        val result1 = FormatUtils.formatPriceWithUnit(1200.0, "kg")
        val result2 = FormatUtils.formatPriceWithUnit(2500.0, "litro")
        val result3 = FormatUtils.formatPriceWithUnit(800.0, "unidad")

        // Then
        assertEquals("$1.200/kg", result1)
        assertEquals("$2.500/litro", result2)
        assertEquals("$800/unidad", result3)
    }

    @Test
    fun `formatPriceWithUnit con diferentes unidades`() {
        // Given
        val units = listOf("kg", "litro", "unidad", "docena", "gramo")

        // When & Then
        units.forEach { unit ->
            val result = FormatUtils.formatPriceWithUnit(1000.0, unit)
            assertTrue(result.contains("$1.000"))
            assertTrue(result.endsWith("/$unit"))
        }
    }

    @Test
    fun `formatPrice con decimales se redondea correctamente`() {
        // Given & When
        val result1 = FormatUtils.formatPrice(1234.56)
        val result2 = FormatUtils.formatPrice(999.99)

        // Then
        assertTrue(result1.contains("1.234") || result1.contains("1.235"))
        assertTrue(result2.contains("999") || result2.contains("1.000"))
    }
}
