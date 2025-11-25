package com.example.huerto_hogar.data

import com.example.huerto_hogar.data.enums.UserRole
import com.example.huerto_hogar.data.model.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import java.util.Date

/**
 * Tests simplificados para LocalDataRepository
 * 
 * Estos tests verifican el comportamiento básico del repositorio de datos local.
 * Nota: StateFlow puede tener comportamientos asíncronos que requieren delays o
 * múltiples lecturas para verificar cambios de estado.
 */
@ExperimentalCoroutinesApi
class LocalDataRepositoryTest {

    private lateinit var testUser: User

    @Before
    fun setup() {
        // Limpiar estado antes de cada test
        LocalDataRepository.logout()
        
        testUser = User(
            id = 1,
            name = "Test User",
            email = "test@test.cl",
            password = "password",
            registrationDate = Date(),
            address = "Test Address",
            phone = 123456789,
            comunaId = 1,
            role = UserRole.CLIENTE
        )
    }

    @Test
    fun `setCurrentUser establece usuario correctamente`() = runTest {
        // When
        LocalDataRepository.setCurrentUser(testUser)

        // Then
        val currentUser = LocalDataRepository.currentUser.first()
        assertNotNull(currentUser)
        assertEquals(testUser.id, currentUser?.id)
        assertEquals(testUser.name, currentUser?.name)
        assertEquals(testUser.email, currentUser?.email)
    }

    @Test
    fun `logout limpia el usuario actual`() = runTest {
        // Given
        LocalDataRepository.setCurrentUser(testUser)
        assertNotNull(LocalDataRepository.currentUser.first())

        // When
        LocalDataRepository.logout()

        // Then
        val currentUser = LocalDataRepository.currentUser.first()
        assertNull(currentUser)
    }

    @Test
    fun `usuario inicial es null`() = runTest {
        // When
        val currentUser = LocalDataRepository.currentUser.first()

        // Then
        assertNull(currentUser)
    }

    @Test
    fun `setCurrentUser con null limpia el usuario`() = runTest {
        // Given
        LocalDataRepository.setCurrentUser(testUser)
        assertNotNull(LocalDataRepository.currentUser.first())

        // When
        LocalDataRepository.setCurrentUser(null)

        // Then
        val currentUser = LocalDataRepository.currentUser.first()
        assertNull(currentUser)
    }

    @Test
    fun `cambiar de usuario actualiza el estado correctamente`() = runTest {
        // Given
        LocalDataRepository.setCurrentUser(testUser)

        val newUser = User(
            id = 2,
            name = "New User",
            email = "new@test.cl",
            password = "password",
            registrationDate = Date(),
            address = null,
            phone = null,
            comunaId = 1,
            role = UserRole.ADMIN
        )

        // When
        LocalDataRepository.setCurrentUser(newUser)

        // Then - leer el valor más reciente
        val currentUser = LocalDataRepository.currentUser.value
        assertNotNull("El usuario actual no debe ser null", currentUser)
        if (currentUser != null) {
            assertEquals("El ID debe ser 2", 2, currentUser.id)
            assertEquals("El nombre debe ser 'New User'", "New User", currentUser.name)
            assertEquals("El rol debe ser ADMIN", UserRole.ADMIN, currentUser.role)
        }
    }

    @Test
    fun `usuario mantiene propiedades correctamente`() = runTest {
        // Given
        val userWithAllData = User(
            id = 5,
            name = "Complete User",
            email = "complete@test.cl",
            password = "pass123",
            registrationDate = Date(),
            address = "Calle 123",
            phone = 987654321,
            comunaId = 15,
            role = UserRole.VENDEDOR
        )

        // When
        LocalDataRepository.setCurrentUser(userWithAllData)

        // Then - leer el valor más reciente
        val currentUser = LocalDataRepository.currentUser.value
        assertNotNull("El usuario actual no debe ser null", currentUser)
        if (currentUser != null) {
            assertEquals("El ID debe ser 5", 5, currentUser.id)
            assertEquals("El nombre debe ser 'Complete User'", "Complete User", currentUser.name)
            assertEquals("La dirección debe ser 'Calle 123'", "Calle 123", currentUser.address)
            assertEquals("El teléfono debe ser 987654321", 987654321, currentUser.phone)
            assertEquals("El comunaId debe ser 15", 15, currentUser.comunaId)
            assertEquals("El rol debe ser VENDEDOR", UserRole.VENDEDOR, currentUser.role)
        }
    }
}

