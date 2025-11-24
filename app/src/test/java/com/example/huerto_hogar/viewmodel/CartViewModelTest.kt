package com.example.huerto_hogar.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.huerto_hogar.data.enums.UserRole
import com.example.huerto_hogar.data.model.Product
import com.example.huerto_hogar.data.model.User
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*
import java.util.Date

@ExperimentalCoroutinesApi
class CartViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    
    private lateinit var viewModel: CartViewModel
    private lateinit var mockApplication: Application
    private lateinit var testProduct: Product
    private lateinit var testUser: User

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockApplication = mockk(relaxed = true)
        
        viewModel = CartViewModel(mockApplication)
        
        testProduct = Product(
            id = "PR001",
            name = "Manzana",
            imageName = null,
            description = "Manzana roja",
            price = 1500.0,
            stock = 100,
            origin = "Chile",
            isOrganic = true,
            isActive = true,
            entryDate = Date(),
            categoryId = 1,
            priceUnit = "kg"
        )
        
        testUser = User(
            id = 1,
            name = "Test User",
            email = "test@test.cl",
            password = "pass",
            registrationDate = Date(),
            address = "Test Address",
            phone = 123456789,
            comunaId = 1,
            role = UserRole.CLIENTE
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `agregar producto al carrito exitosamente`() = runTest {
        // Given
        val quantity = 5

        // When
        val result = viewModel.addToCart(testProduct, quantity)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(1, viewModel.cartItems.value.size)
        assertEquals(quantity, viewModel.cartItems.value[testProduct.id]?.quantity)
        assertEquals(testProduct.name, viewModel.cartItems.value[testProduct.id]?.product?.name)
    }

    @Test
    fun `agregar producto con stock insuficiente falla`() = runTest {
        // Given
        val quantity = 150 // Más que el stock disponible (100)

        // When
        val result = viewModel.addToCart(testProduct, quantity)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Stock insuficiente.", result.exceptionOrNull()?.message)
        assertEquals(0, viewModel.cartItems.value.size)
    }

    @Test
    fun `agregar producto existente incrementa cantidad`() = runTest {
        // Given
        viewModel.addToCart(testProduct, 5)

        // When
        val result = viewModel.addToCart(testProduct, 3)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(1, viewModel.cartItems.value.size)
        assertEquals(8, viewModel.cartItems.value[testProduct.id]?.quantity)
    }

    @Test
    fun `actualizar cantidad de producto en carrito`() = runTest {
        // Given
        viewModel.addToCart(testProduct, 5)

        // When
        val result = viewModel.updateCartItemQuantity(testProduct.id, 10)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(10, viewModel.cartItems.value[testProduct.id]?.quantity)
    }

    @Test
    fun `actualizar cantidad a cero elimina producto`() = runTest {
        // Given
        viewModel.addToCart(testProduct, 5)

        // When
        val result = viewModel.updateCartItemQuantity(testProduct.id, 0)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(0, viewModel.cartItems.value.size)
    }

    @Test
    fun `actualizar cantidad mayor al stock falla`() = runTest {
        // Given
        viewModel.addToCart(testProduct, 5)

        // When
        val result = viewModel.updateCartItemQuantity(testProduct.id, 150)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Stock insuficiente. Disponible: 100", result.exceptionOrNull()?.message)
    }

    @Test
    fun `eliminar producto del carrito`() = runTest {
        // Given
        viewModel.addToCart(testProduct, 5)

        // When
        val result = viewModel.removeFromCart(testProduct.id)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(0, viewModel.cartItems.value.size)
    }

    @Test
    fun `limpiar carrito elimina todos los productos`() = runTest {
        // Given
        viewModel.addToCart(testProduct, 5)
        
        val product2 = testProduct.copy(id = "PR002", name = "Pera")
        viewModel.addToCart(product2, 3)

        // When
        viewModel.clearCart()

        // Then
        assertEquals(0, viewModel.cartItems.value.size)
    }

    @Test
    fun `calcular total del carrito correctamente`() = runTest {
        // Given
        viewModel.addToCart(testProduct, 5) // 1500 * 5 = 7500
        
        val product2 = testProduct.copy(id = "PR002", name = "Pera", price = 2000.0)
        viewModel.addToCart(product2, 3) // 2000 * 3 = 6000

        // When
        val items = viewModel.cartItems.value.values.toList()
        val total = items.sumOf { it.product.price * it.quantity }

        // Then
        assertEquals(13500.0, total, 0.01)
    }

    @Test
    fun `contar items del carrito correctamente`() = runTest {
        // Given
        viewModel.addToCart(testProduct, 5)
        
        val product2 = testProduct.copy(id = "PR002", name = "Pera")
        viewModel.addToCart(product2, 3)

        // When
        val items = viewModel.cartItems.value.values.toList()
        val count = items.sumOf { it.quantity }

        // Then
        assertEquals(8, count) // 5 + 3
    }

    @Test
    fun `obtener lista de items del carrito`() = runTest {
        // Given
        viewModel.addToCart(testProduct, 5)
        
        val product2 = testProduct.copy(id = "PR002", name = "Pera")
        viewModel.addToCart(product2, 3)

        // When
        val items = viewModel.getCartItems()

        // Then
        assertEquals(2, items.size)
        assertTrue(items.any { it.product.id == "PR001" && it.quantity == 5 })
        assertTrue(items.any { it.product.id == "PR002" && it.quantity == 3 })
    }
}
