package com.example.huerto_hogar.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProductoViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: ProductoViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ProductoViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `estado inicial debe estar vacio`() {
        val estado = viewModel.estado.value
        assertEquals("", estado.nombre)
        assertEquals("", estado.descripcion)
        assertEquals(0, estado.precio)
        assertEquals(0, estado.stock)
        assertEquals("", estado.linkImagen)
        assertEquals("", estado.origen)
        assertFalse(estado.certificacionOrganica)
        assertTrue(estado.estaActivo)
        assertEquals(1, estado.idCategoria)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `onNombreChange debe actualizar nombre y limpiar error`() {
        viewModel.onNombreChange("Tomate")
        
        assertEquals("Tomate", viewModel.estado.value.nombre)
        assertNull(viewModel.estado.value.errores.nombre)
    }

    @Test
    fun `onDescripcionChange debe actualizar descripcion y limpiar error`() {
        viewModel.onDescripcionChange("Tomate fresco")
        
        assertEquals("Tomate fresco", viewModel.estado.value.descripcion)
        assertNull(viewModel.estado.value.errores.descripcion)
    }

    @Test
    fun `onPrecioChange debe convertir string a int`() {
        viewModel.onPrecioChange("1500")
        assertEquals(1500, viewModel.estado.value.precio)
    }

    @Test
    fun `onStockChange debe convertir string a int`() {
        viewModel.onStockChange("50")
        assertEquals(50, viewModel.estado.value.stock)
    }

    @Test
    fun `onOrigenChange debe actualizar origen`() {
        viewModel.onOrigenChange("Chile")
        assertEquals("Chile", viewModel.estado.value.origen)
    }

    @Test
    fun `onCertificacionOrganicaChange debe actualizar certificacion`() {
        viewModel.onCertificacionOrganicaChange(true)
        assertTrue(viewModel.estado.value.certificacionOrganica)
    }

    @Test
    fun `onEstaActivoChange debe actualizar estado activo`() {
        viewModel.onEstaActivoChange(false)
        assertFalse(viewModel.estado.value.estaActivo)
    }

    @Test
    fun `onIdCategoriaChange debe actualizar categoria`() {
        viewModel.onIdCategoriaChange(2)
        assertEquals(2, viewModel.estado.value.idCategoria)
    }

    @Test
    fun `validarFormulario debe fallar con nombre vacio`() {
        val resultado = viewModel.validarFormulario()
        
        assertFalse(resultado)
        assertNotNull(viewModel.estado.value.errores.nombre)
    }

    @Test
    fun `validarFormulario debe fallar con descripcion vacia`() {
        viewModel.onNombreChange("Tomate")
        
        val resultado = viewModel.validarFormulario()
        
        assertFalse(resultado)
        assertNotNull(viewModel.estado.value.errores.descripcion)
    }

    @Test
    fun `validarFormulario debe fallar con precio cero`() {
        viewModel.onNombreChange("Tomate")
        viewModel.onDescripcionChange("Descripción")
        viewModel.onPrecioChange("0")
        
        val resultado = viewModel.validarFormulario()
        
        assertFalse(resultado)
        assertNotNull(viewModel.estado.value.errores.precio)
    }

    @Test
    fun `validarFormulario debe fallar con stock negativo`() {
        viewModel.onNombreChange("Tomate")
        viewModel.onDescripcionChange("Descripción")
        viewModel.onPrecioChange("1500")
        viewModel.onStockChange("-5")
        
        val resultado = viewModel.validarFormulario()
        
        assertFalse(resultado)
        assertNotNull(viewModel.estado.value.errores.stock)
    }

    @Test
    fun `validarFormulario debe fallar sin imagen`() {
        viewModel.onNombreChange("Tomate")
        viewModel.onDescripcionChange("Descripción")
        viewModel.onPrecioChange("1500")
        viewModel.onIdCategoriaChange(2)
        
        val resultado = viewModel.validarFormulario()
        
        assertFalse(resultado)
        assertNotNull(viewModel.estado.value.errores.linkImagen)
    }

    @Test
    fun `limpiarFormulario debe resetear estado`() {
        viewModel.onNombreChange("Tomate")
        viewModel.onDescripcionChange("Descripción")
        viewModel.onPrecioChange("1500")
        
        viewModel.limpiarFormulario()
        
        val estado = viewModel.estado.value
        assertEquals("", estado.nombre)
        assertEquals("", estado.descripcion)
        assertEquals(0, estado.precio)
        assertNull(viewModel.selectedImageUri.value)
    }

    @Test
    fun `clearImage debe limpiar imagen seleccionada`() {
        viewModel.clearImage()
        
        assertNull(viewModel.selectedImageUri.value)
        assertEquals("", viewModel.estado.value.linkImagen)
    }

    @Test
    fun `nombre muy largo debe fallar validacion`() {
        viewModel.onNombreChange("A".repeat(101))
        
        val resultado = viewModel.validarFormulario()
        
        assertFalse(resultado)
        assertNotNull(viewModel.estado.value.errores.nombre)
    }

    @Test
    fun `descripcion muy larga debe fallar validacion`() {
        viewModel.onNombreChange("Tomate")
        viewModel.onDescripcionChange("A".repeat(501))
        
        val resultado = viewModel.validarFormulario()
        
        assertFalse(resultado)
        assertNotNull(viewModel.estado.value.errores.descripcion)
    }

    @Test
    fun `categoria invalida debe fallar validacion`() {
        viewModel.onNombreChange("Tomate")
        viewModel.onDescripcionChange("Descripción")
        viewModel.onPrecioChange("1500")
        viewModel.onIdCategoriaChange(0)
        
        val resultado = viewModel.validarFormulario()
        
        assertFalse(resultado)
        assertNotNull(viewModel.estado.value.errores.idCategoria)
    }
}
