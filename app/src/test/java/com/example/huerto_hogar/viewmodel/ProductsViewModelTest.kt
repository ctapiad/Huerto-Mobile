package com.example.huerto_hogar.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.huerto_hogar.network.ApiResult
import com.example.huerto_hogar.network.ProductoDto
import com.example.huerto_hogar.network.repository.ProductRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*

@ExperimentalCoroutinesApi
class ProductsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    
    private lateinit var viewModel: ProductsViewModel
    private lateinit var mockApplication: Application
    private lateinit var testProducts: List<ProductoDto>

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockApplication = mockk(relaxed = true)
        
        testProducts = listOf(
            ProductoDto(
                idProducto = "PR001",
                nombre = "Manzana",
                linkImagen = null,
                descripcion = "Manzana roja",
                precio = 1500,
                stock = 100,
                origen = "Chile",
                certificacionOrganica = true,
                estaActivo = true,
                fechaIngreso = "2024-01-01",
                idCategoria = 1,
                createdAt = null,
                updatedAt = null
            ),
            ProductoDto(
                idProducto = "PR002",
                nombre = "Pera",
                linkImagen = null,
                descripcion = "Pera verde",
                precio = 2000,
                stock = 50,
                origen = "Chile",
                certificacionOrganica = false,
                estaActivo = true,
                fechaIngreso = "2024-01-01",
                idCategoria = 1,
                createdAt = null,
                updatedAt = null
            )
        )
        
        // Mockear el constructor de ProductRepository
        mockkConstructor(ProductRepository::class)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `cargar productos exitosamente al inicializar`() = runTest {
        // Given
        coEvery { anyConstructed<ProductRepository>().getAllProducts() } returns 
            ApiResult.Success(testProducts)

        // When
        viewModel = ProductsViewModel(mockApplication)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertEquals(2, state.products.size)
        assertEquals(5, state.categories.size) // Categorías hardcodeadas
    }

    @Test
    fun `manejar error al cargar productos`() = runTest {
        // Given
        coEvery { anyConstructed<ProductRepository>().getAllProducts() } returns 
            ApiResult.Error("Error de conexión", 500)

        // When
        viewModel = ProductsViewModel(mockApplication)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals("Error de conexión", state.error)
        assertEquals(0, state.products.size)
        assertEquals(5, state.categories.size)
    }

    @Test
    fun `mostrar estado de carga al cargar productos`() = runTest {
        // Given
        coEvery { anyConstructed<ProductRepository>().getAllProducts() } coAnswers {
            kotlinx.coroutines.delay(100)
            ApiResult.Success(testProducts)
        }

        // When
        viewModel = ProductsViewModel(mockApplication)

        // Then - verificar que está cargando
        assertTrue(viewModel.uiState.value.isLoading)
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Después de completar debe dejar de cargar
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `refresh products recarga los productos`() = runTest {
        // Given
        coEvery { anyConstructed<ProductRepository>().getAllProducts() } returns 
            ApiResult.Success(testProducts)
        
        viewModel = ProductsViewModel(mockApplication)
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.refreshProducts()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then - debe haber llamado al repository dos veces (init + refresh)
        coVerify(exactly = 2) { anyConstructed<ProductRepository>().getAllProducts() }
    }

    @Test
    fun `conversion correcta de ProductoDto a Product`() = runTest {
        // Given
        coEvery { anyConstructed<ProductRepository>().getAllProducts() } returns 
            ApiResult.Success(testProducts)

        // When
        viewModel = ProductsViewModel(mockApplication)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val products = viewModel.uiState.value.products
        val firstProduct = products[0]
        
        assertEquals("PR001", firstProduct.id)
        assertEquals("Manzana", firstProduct.name)
        assertEquals(1500.0, firstProduct.price, 0.01) // Convertido a Double
        assertEquals(100, firstProduct.stock)
        assertEquals("Chile", firstProduct.origin)
        assertTrue(firstProduct.isOrganic)
        assertTrue(firstProduct.isActive)
        assertEquals(1L, firstProduct.categoryId)
        assertEquals("kg", firstProduct.priceUnit)
    }

    @Test
    fun `categorias hardcodeadas siempre disponibles`() = runTest {
        // Given - incluso con error en productos
        coEvery { anyConstructed<ProductRepository>().getAllProducts() } returns 
            ApiResult.Error("Error", 500)

        // When
        viewModel = ProductsViewModel(mockApplication)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val categories = viewModel.uiState.value.categories
        assertEquals(5, categories.size)
        assertEquals("Frutas", categories[0].name)
        assertEquals("Verduras", categories[1].name)
        assertEquals("Orgánicos", categories[2].name)
        assertEquals("Lácteos", categories[3].name)
        assertEquals("Granos", categories[4].name)
    }

    @Test
    fun `manejar excepcion durante carga de productos`() = runTest {
        // Given
        coEvery { anyConstructed<ProductRepository>().getAllProducts() } throws 
            RuntimeException("Error inesperado")

        // When
        viewModel = ProductsViewModel(mockApplication)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.error?.contains("Error al cargar productos") == true)
        assertEquals(0, state.products.size)
    }

    @Test
    fun `productos con diferentes estados se cargan correctamente`() = runTest {
        // Given
        val mixedProducts = listOf(
            testProducts[0], // Activo
            testProducts[1].copy(estaActivo = false) // Inactivo
        )
        
        coEvery { anyConstructed<ProductRepository>().getAllProducts() } returns 
            ApiResult.Success(mixedProducts)

        // When
        viewModel = ProductsViewModel(mockApplication)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val products = viewModel.uiState.value.products
        assertEquals(2, products.size)
        assertTrue(products[0].isActive)
        assertFalse(products[1].isActive)
    }
}
