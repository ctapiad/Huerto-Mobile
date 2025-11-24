# Pruebas Unitarias - Huerto Mobile

## 📋 Descripción

Conjunto de pruebas unitarias para las partes críticas de la aplicación con las que el usuario interactúa más frecuentemente.

## 🧪 Cobertura de Pruebas

### 1. LoginViewModelTest (7 pruebas)
Pruebas para el flujo de autenticación:
- ✅ Login exitoso con credenciales correctas
- ✅ Login fallido con contraseña incorrecta
- ✅ Login fallido con usuario no encontrado
- ✅ Estado de carga durante la petición
- ✅ Reset de estado después del login
- ✅ Mapeo correcto de tipos de usuario a roles (ADMIN, VENDEDOR, CLIENTE)

### 2. CartViewModelTest (14 pruebas)
Pruebas para la gestión del carrito de compras:
- ✅ Agregar producto al carrito exitosamente
- ✅ Agregar producto con stock insuficiente (falla)
- ✅ Incrementar cantidad de producto existente
- ✅ Actualizar cantidad de producto en carrito
- ✅ Actualizar cantidad a cero elimina el producto
- ✅ Actualizar cantidad mayor al stock disponible (falla)
- ✅ Eliminar producto del carrito
- ✅ Limpiar carrito completo
- ✅ Cálculo correcto del total del carrito
- ✅ Conteo de items del carrito
- ✅ Obtener lista de items
- ✅ Crear orden ficticia limpia el carrito
- ✅ Envío gratis para compras > $50.000
- ✅ Agregar costo de envío $3.000 para compras < $50.000

### 3. ProductsViewModelTest (9 pruebas)
Pruebas para la carga y gestión de productos:
- ✅ Cargar productos exitosamente al inicializar
- ✅ Manejar error al cargar productos
- ✅ Estado de carga durante petición
- ✅ Refresh recarga los productos
- ✅ Conversión correcta de ProductoDto a Product
- ✅ Categorías hardcodeadas siempre disponibles
- ✅ Manejo de excepciones
- ✅ Productos con diferentes estados (activo/inactivo)

### 4. FormatUtilsTest (7 pruebas)
Pruebas para utilidades de formateo:
- ✅ Formato correcto de precios chilenos ($1.200, $50.000)
- ✅ Formato con cero ($0)
- ✅ Formato con números grandes ($1.000.000)
- ✅ Combinar precio y unidad ($1.200/kg)
- ✅ Diferentes unidades (kg, litro, unidad, docena, gramo)
- ✅ Redondeo correcto de decimales

### 5. ApiResultTest (6 pruebas)
Pruebas para el manejo de respuestas de API:
- ✅ Success con respuesta exitosa
- ✅ Error con código 404
- ✅ Error con código 500
- ✅ Manejo de excepción de red
- ✅ Respuesta 204 No Content retorna lista vacía
- ✅ Body null con código exitoso retorna mensaje genérico

### 6. LocalDataRepositoryTest (7 pruebas)
Pruebas para el manejo de sesión:
- ✅ Establecer usuario correctamente
- ✅ Logout limpia el usuario
- ✅ Usuario inicial es null
- ✅ setCurrentUser con null limpia usuario
- ✅ Cambiar de usuario actualiza estado
- ✅ Usuario mantiene todas las propiedades

## 📊 Estadísticas

- **Total de pruebas:** 50 pruebas unitarias
- **ViewModels:** 30 pruebas (60%)
- **Utilidades y Data:** 20 pruebas (40%)
- **Cobertura de casos críticos:** Login, Carrito, Productos

## 🚀 Ejecutar las Pruebas

### Desde Android Studio
1. Clic derecho en la carpeta `test`
2. Seleccionar "Run 'All Tests'"

### Desde Gradle
```bash
./gradlew test
```

### Ejecutar pruebas específicas
```bash
# Solo LoginViewModel
./gradlew test --tests "*.LoginViewModelTest"

# Solo CartViewModel
./gradlew test --tests "*.CartViewModelTest"

# Solo ProductsViewModel
./gradlew test --tests "*.ProductsViewModelTest"
```

## 🔧 Dependencias de Testing

```kotlin
// JUnit 4
testImplementation("junit:junit:4.13.2")

// Coroutines Testing
testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")

// MockK para mocking
testImplementation("io.mockk:mockk:1.13.8")

// Architecture Components Testing
testImplementation("androidx.arch.core:core-testing:2.2.0")
```

## 📝 Estructura de las Pruebas

Cada prueba sigue el patrón **AAA (Arrange-Act-Assert)**:

```kotlin
@Test
fun `nombre descriptivo de la prueba`() = runTest {
    // Given (Arrange) - Preparar datos y mocks
    val testData = ...
    
    // When (Act) - Ejecutar la acción
    val result = viewModel.someMethod()
    
    // Then (Assert) - Verificar el resultado
    assertEquals(expected, result)
}
```

## ⚠️ Notas Importantes

### Limitaciones
- Las pruebas usan **mocks** de los repositorios, no hacen llamadas reales a la API
- CartViewModel no persiste datos (solo memoria)
- LocalDataRepository pierde la sesión al cerrar la app

### Casos No Cubiertos
- Pruebas de integración con APIs reales
- Pruebas de UI (Compose)
- Pruebas de pantallas completas
- Validaciones de formularios (ProductoViewModel, UsuarioViewModel)

### Recomendaciones
1. Ejecutar pruebas antes de cada commit
2. Agregar pruebas nuevas al implementar features
3. Mantener cobertura > 80% en ViewModels críticos
4. Usar TDD (Test-Driven Development) para nuevas funcionalidades

## 🎯 Casos de Uso Cubiertos

### Usuario Final (Cliente)
- ✅ Iniciar sesión
- ✅ Ver productos
- ✅ Agregar al carrito
- ✅ Modificar cantidades
- ✅ Eliminar del carrito
- ✅ Ver total con envío
- ✅ Crear orden

### Administrador
- ✅ Login con rol ADMIN
- ✅ Ver todos los productos
- ✅ Gestión de productos (cobertura parcial)

### Errores y Edge Cases
- ✅ Stock insuficiente
- ✅ Usuario no encontrado
- ✅ Contraseña incorrecta
- ✅ Errores de red
- ✅ Productos inactivos
- ✅ Carrito vacío

## 📈 Mejoras Futuras

1. **Ampliar cobertura:**
   - ProductoViewModel (validaciones)
   - UsuarioViewModel (validaciones)
   - ImageUploadService

2. **Pruebas de integración:**
   - UserRepository con API real
   - ProductRepository con API real

3. **Pruebas E2E:**
   - Flujo completo de compra
   - Login → Carrito → Checkout

4. **Performance:**
   - Tiempo de carga de productos
   - Rendimiento con carritos grandes

## 🐛 Reportar Issues

Si encuentras problemas con las pruebas:
1. Verificar que las dependencias estén sincronizadas
2. Limpiar proyecto: `./gradlew clean`
3. Rebuild: `Build > Rebuild Project`
4. Invalidar cache: `File > Invalidate Caches / Restart`

---

**Fecha de creación:** 24 de noviembre de 2025  
**Versión:** 1.0  
**Autor:** Equipo Huerto Mobile
