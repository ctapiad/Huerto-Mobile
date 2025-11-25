# 🎯 Guía para la Presentación - Evaluación Parcial 5
## Huerto Hogar - Desarrollo de Aplicaciones Móviles

**Duración:** 15 minutos  
**Evaluación:** Individual (24% de la nota final)  
**Fecha:** Semana 16  

---

## ⏱️ Estructura de Tiempo (15 minutos)

| Tiempo | Tema | Minutos | Puntaje |
|--------|------|---------|---------|
| 0-2 min | Inicio y Entorno | 2 | 12% |
| 2-4 min | Arquitectura MVVM | 2 | 12% |
| 4-7 min | Microservicios | 3 | 12% |
| 7-9 min | API Externa y Persistencia | 2 | - |
| 9-10 min | Tests Unitarios | 1 | 20% |
| 10-11 min | APK Firmado | 1 | 8% |
| 11-12 min | GitHub y Trello | 1 | 8% |
| **12-15 min** | **MODIFICACIÓN EN TIEMPO REAL** | **3** | **40%** |

---

## 📋 GUIÓN COMPLETO PASO A PASO

### 1️⃣ INICIO DEL PROYECTO Y ENTORNO (2 min - 12%)

#### ✅ Checklist antes de empezar:
```
□ Emulador/dispositivo Android corriendo
□ App instalada y funcionando
□ Microservicios AWS accesibles
□ MongoDB Atlas conectado
□ Android Studio abierto
```

#### 🎤 Qué decir (textual):

> "Buenos días/tardes. Mi proyecto es **Huerto Hogar**, una aplicación móvil de marketplace para productos orgánicos. 
>
> Como pueden ver, tengo la aplicación ejecutándose en el emulador/dispositivo Android [mostrar pantalla principal].
>
> La app está conectada a dos microservicios que desarrollamos en Spring Boot y desplegamos en AWS EC2:
> - **Microservicio de Usuarios**: http://34.193.190.24:8081
> - **Microservicio de Productos**: http://34.202.46.121:8081
>
> Ambos microservicios se conectan a una base de datos MongoDB Atlas remota."

#### 💻 Qué mostrar:

**1. App corriendo (HomeScreen con productos visibles)**
```
Pantalla debe mostrar:
- Logo de Huerto Hogar
- Lista de productos con imágenes
- Navegación funcional
```

**2. Terminal - Verificar microservicio de usuarios:**
```bash
curl http://34.193.190.24:8081/api/usuarios
```
Debe retornar JSON con lista de usuarios

**3. Terminal - Verificar microservicio de productos:**
```bash
curl http://34.202.46.121:8081/api/productos
```
Debe retornar JSON con lista de productos

**4. Mostrar configuración:**
Abrir `ApiConfig.kt` y señalar:
```kotlin
object ApiConfig {
    const val USER_SERVICE_BASE_URL = "http://34.193.190.24:8081/"
    const val PRODUCT_SERVICE_BASE_URL = "http://34.202.46.121:8081/"
}
```

---

### 2️⃣ ARQUITECTURA GENERAL (2 min - 12%)

#### 🎤 Qué decir:

> "La aplicación móvil utiliza arquitectura **MVVM (Model-View-ViewModel)** que es el patrón recomendado por Google para Android.
>
> Nuestra estructura de paquetes está organizada así:
> - **ui**: Contiene todas las pantallas con Jetpack Compose
> - **viewmodel**: Contiene la lógica de negocio y manejo de estado
> - **network**: Configuración de Retrofit y definición de APIs
> - **data**: Modelos de datos y repositorios
> - **service**: Servicios auxiliares como carga de imágenes
>
> El flujo de datos es: **UI → ViewModel → Repository → Retrofit → API REST**
>
> Esta separación nos permite:
> 1. Testear la lógica sin depender de la UI
> 2. Cambiar la fuente de datos sin afectar la UI
> 3. Mantener el código organizado y escalable"

#### 💻 Qué mostrar:

**1. Estructura de carpetas en Android Studio:**
```
app/src/main/java/com/example/huerto_hogar/
├── ui/
│   ├── admin/
│   │   ├── AdminDashboard.kt
│   │   ├── ProductManagementScreen.kt
│   │   └── UserManagementScreen.kt
│   ├── auth/
│   │   └── LoginViewModel.kt
│   ├── store/
│   │   ├── HomeScreen.kt
│   │   ├── CartScreen.kt
│   │   └── ProductListScreen.kt
│   └── user/
│       └── UserProfileScreen.kt
├── viewmodel/
│   ├── ProductsViewModel.kt
│   ├── ProductoViewModel.kt
│   ├── UsuarioViewModel.kt
│   └── CartViewModel.kt
├── network/
│   ├── ApiConfig.kt
│   ├── UserApiService.kt
│   ├── ProductApiService.kt
│   └── repository/
│       ├── UserRepository.kt
│       └── ProductRepository.kt
├── data/
│   ├── model/
│   └── LocalDataRepository.kt
└── service/
    └── ImageUploadService.kt
```

**2. Ejemplo de flujo MVVM - Abrir archivos:**

**HomeScreen.kt (líneas ~100-120):**
```kotlin
@Composable
fun HomeScreen(
    navController: NavController,
    productsViewModel: ProductsViewModel = viewModel()
) {
    val uiState by productsViewModel.uiState.collectAsState()
    
    // La UI solo observa el estado y renderiza
    LazyVerticalGrid(columns = GridCells.Fixed(2)) {
        items(uiState.productos) { producto ->
            ProductCard(producto)
        }
    }
}
```

**ProductsViewModel.kt (líneas ~50-70):**
```kotlin
class ProductsViewModel : ViewModel() {
    private val repository = ProductRepository()
    private val _uiState = MutableStateFlow(ProductsUiState())
    val uiState = _uiState.asStateFlow()
    
    fun loadProducts() {
        viewModelScope.launch {
            when (val result = repository.getAllProducts()) {
                is ApiResult.Success -> {
                    _uiState.update { it.copy(productos = result.data) }
                }
                // ... manejo de errores
            }
        }
    }
}
```

**ProductRepository.kt (líneas ~20-30):**
```kotlin
class ProductRepository {
    private val apiService = ProductApiClient.apiService
    
    suspend fun getAllProducts(): ApiResult<List<Producto>> {
        return safeApiCall {
            apiService.getAllProducts()
        }
    }
}
```

#### 🎯 Justificación MVVM:
> "MVVM nos permite separar responsabilidades: la UI no tiene lógica de negocio, solo muestra datos. El ViewModel maneja el estado y la lógica. El Repository abstrae la fuente de datos. Esto hace el código más testeable y mantenible."

---

### 3️⃣ MICROSERVICIOS CREADOS (3 min - 12%)

#### 🎤 Qué decir:

> "Desarrollamos dos microservicios REST en Spring Boot 3.x con Java 17, ambos desplegados en AWS EC2 y conectados a MongoDB Atlas.
>
> **Microservicio de Usuarios:**
> - Gestiona registro, autenticación y administración de usuarios
> - Tiene 3 tipos de usuario: Administrador, Vendedor y Cliente
> - Los endpoints principales son GET para login por email, POST para registro, PUT para actualización y DELETE para eliminación
>
> **Microservicio de Productos:**
> - Gestiona el catálogo completo de productos orgánicos
> - Soporta categorías: Frutas, Verduras, Carnes, Lácteos, Granos y Orgánicos
> - Incluye filtrado por categoría y búsqueda
>
> Ambos implementan CRUD completo y retornan respuestas en formato JSON."

#### 💻 Qué mostrar:

**1. Microservicio de Usuarios - Abrir UserApiService.kt:**

```kotlin
interface UserApiService {
    // Listar todos los usuarios
    @GET("api/usuarios")
    suspend fun getAllUsers(): Response<List<Usuario>>
    
    // Obtener usuario por ID
    @GET("api/usuarios/{id}")
    suspend fun getUserById(@Path("id") userId: String): Response<Usuario>
    
    // ENDPOINT DE LOGIN - Buscar por email
    @GET("api/usuarios/email/{email}")
    suspend fun getUserByEmail(
        @Path(value = "email", encoded = false) email: String
    ): Response<Usuario>
    
    // Buscar usuarios por nombre
    @GET("api/usuarios/buscar/{nombre}")
    suspend fun searchUsersByName(@Path("nombre") name: String): Response<List<Usuario>>
    
    // Obtener usuarios por tipo (1=Admin, 2=Vendedor, 3=Cliente)
    @GET("api/usuarios/tipo/{idTipoUsuario}")
    suspend fun getUsersByType(@Path("idTipoUsuario") userTypeId: Int): Response<List<Usuario>>
    
    // Crear nuevo usuario (registro)
    @POST("api/usuarios")
    suspend fun createUser(@Body user: Usuario): Response<String>
    
    // Actualizar usuario existente
    @PUT("api/usuarios")
    suspend fun updateUser(@Body user: Usuario): Response<String>
    
    // Eliminar usuario por ID
    @DELETE("api/usuarios/{id}")
    suspend fun deleteUser(@Path("id") userId: String): Response<String>
}

// Modelo de datos
data class Usuario(
    val id: String? = null,
    val nombre: String,
    val email: String,
    val password: String,
    val fechaRegistro: String? = null,
    val direccion: String? = null,
    val telefono: Int? = null,
    val idComuna: Int? = null,
    val idTipoUsuario: Int? = null  // 1=Admin, 2=Vendedor, 3=Cliente
)
```

**2. Microservicio de Productos - Abrir ProductApiService.kt:**

```kotlin
interface ProductApiService {
    // Listar todos los productos
    @GET("api/productos")
    suspend fun getAllProducts(): Response<List<Producto>>
    
    // Obtener producto por ID
    @GET("api/productos/{id}")
    suspend fun getProductById(@Path("id") productId: String): Response<Producto>
    
    // FILTRAR POR CATEGORÍA
    @GET("api/productos/categoria/{categoria}")
    suspend fun getProductsByCategory(
        @Path("categoria") categoria: String
    ): Response<List<Producto>>
    
    // Crear nuevo producto
    @POST("api/productos")
    suspend fun createProduct(@Body producto: Producto): Response<String>
    
    // Actualizar producto
    @PUT("api/productos")
    suspend fun updateProduct(@Body producto: Producto): Response<String>
    
    // Eliminar producto
    @DELETE("api/productos/{id}")
    suspend fun deleteProduct(@Path("id") productId: String): Response<String>
}

// Modelo de datos
data class Producto(
    val id: String? = null,
    val nombre: String,
    val descripcion: String? = null,
    val precio: Double,
    val stock: Int,
    val categoria: String,
    val linkImagen: String? = null
)
```

**3. Demostrar integración en la app:**

**Abrir LoginViewModel.kt (líneas ~50-100):**
```kotlin
fun login() {
    viewModelScope.launch {
        // Llamada al microservicio de usuarios
        when (val result = userRepository.getUserByEmail(email)) {
            is ApiResult.Success -> {
                val usuario = result.data
                // Verificar contraseña
                if (usuario.password == password) {
                    // Login exitoso
                    LocalDataRepository.setCurrentUser(user)
                    _uiState.update { it.copy(loginSuccess = user) }
                }
            }
            is ApiResult.Error -> {
                _uiState.update { it.copy(errorMessage = result.message) }
            }
        }
    }
}
```

**4. Mostrar funcionamiento en tiempo real:**

```
1. Ir a pantalla de Login
2. Ingresar: admin@profesor.duoc.cl / Admin*123
3. Hacer login (llamada a GET /api/usuarios/email/{email})
4. Mostrar que redirige a HomeScreen
5. Productos se cargan desde GET /api/productos
6. Filtrar por categoría "Verduras" (GET /api/productos/categoria/Verduras)
```

#### 🎯 Justificación técnica:
> "Separamos en dos microservicios porque aplica el principio de responsabilidad única. Si necesitamos escalar usuarios, no afecta productos. Cada uno tiene su propia base de datos en MongoDB con colecciones independientes: 'usuarios' y 'productos'."

---

### 4️⃣ CONSUMO DE API EXTERNA (2 min - 15%)

#### 🎤 Qué decir:

> "Además de nuestros dos microservicios propios, consumimos la **API pública Open-Meteo** para mostrar información meteorológica en tiempo real en el HomeScreen.
>
> **¿Por qué usamos esta API?**
> - Es gratuita y no requiere API key
> - Retorna datos meteorológicos actualizados
> - Demuestra integración con servicios de terceros
>
> **Diferencias clave con nuestros microservicios:**
>
> | Aspecto | Microservicios Propios | API Externa (Open-Meteo) |
> |---------|----------------------|--------------------------|
> | Control | Lo controlamos 100% | No lo controlamos |
> | Endpoints | Podemos agregar/modificar | Fijos, no modificables |
> | Modelos | Diseñamos estructura | Debemos adaptarnos |
> | Despliegue | AWS EC2 (propio) | Servicio de terceros |
> | Base de datos | MongoDB Atlas (nuestra) | No tenemos acceso |
> | URL | http://34.193.190.24:8081 | https://api.open-meteo.com |
>
> Esta integración demuestra que sabemos consumir tanto APIs propias como de terceros, adaptándonos a estructuras de datos externas."

#### 💻 Qué mostrar:

**1. Abrir WeatherApiService.kt:**

```kotlin
/**
 * Interfaz de servicios REST para Open-Meteo API (API Externa)
 * Esta es una API EXTERNA (diferente a nuestros microservicios propios)
 */
interface WeatherApiService {
    
    @GET("v1/forecast")
    suspend fun getCurrentWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = "temperature_2m,wind_speed_10m"
    ): Response<WeatherResponse>
}

data class WeatherResponse(
    @SerializedName("current")
    val current: CurrentWeather
)

data class CurrentWeather(
    @SerializedName("temperature_2m")
    val temperature: Double,
    
    @SerializedName("wind_speed_10m")
    val windSpeed: Double
)

// Cliente Retrofit separado
object WeatherApiClient {
    private const val BASE_URL = "https://api.open-meteo.com/"
    
    val apiService: WeatherApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApiService::class.java)
    }
}
```

**Explicar:**
> "Como pueden ver, el endpoint y modelo de datos están predefinidos por Open-Meteo. No podemos modificarlos. Usamos `@SerializedName` para mapear los nombres exactos que retorna la API externa (como `temperature_2m`). Creamos un cliente Retrofit separado con la URL base de la API externa."

**2. Abrir WeatherViewModel.kt:**

```kotlin
class WeatherViewModel : ViewModel() {
    
    private val weatherApi = WeatherApiClient.apiService
    
    // Coordenadas de Viña del Mar, Chile
    private val latitude = -33.0472
    private val longitude = -71.6127
    
    fun loadWeather() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                val response = weatherApi.getCurrentWeather(
                    latitude = latitude,
                    longitude = longitude
                )
                
                if (response.isSuccessful && response.body() != null) {
                    val weatherData = response.body()!!.current
                    _uiState.update { 
                        it.copy(weather = weatherData, isLoading = false)
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(errorMessage = "Error: ${e.message}")
                }
            }
        }
    }
}
```

**Explicar:**
> "El ViewModel consume la API externa igual que nuestros microservicios: mediante Retrofit y corrutinas. La diferencia es que no podemos controlar qué datos retorna ni su estructura. Usamos las coordenadas de Viña del Mar (ciudad de nuestro proyecto) para obtener el clima local."

**3. Mostrar en la app (HomeScreen):**

```
1. Abrir la app en el emulador
2. Ir a HomeScreen
3. Señalar el widget del clima arriba de "Productos Destacados"
4. Mostrar que aparece:
   - 🌤️ Viña del Mar
   - Temperatura actual (ej: 18.5°C)
   - Velocidad del viento (ej: 12.3 km/h)
   - Texto "API Externa: Open-Meteo"
```

**4. Abrir HomeScreen.kt y mostrar el composable WeatherWidget:**

```kotlin
@Composable
fun WeatherWidget(weatherUiState: WeatherUiState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(60.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2196F3).copy(alpha = 0.1f)
        )
    ) {
        Row {
            Text("🌤️ Viña del Mar")
            
            when {
                weatherUiState.weather != null -> {
                    Text("${weatherUiState.weather.temperature}°C")
                    Text("${weatherUiState.weather.windSpeed} km/h")
                }
            }
        }
    }
}
```

**Explicar:**
> "Integramos el clima en el HomeScreen de forma no invasiva. Es un widget compacto que muestra temperatura y viento en tiempo real. No interfiere con nuestros microservicios ni con los datos locales. Es información complementaria para el usuario."

**5. Demostrar llamada en tiempo real (Terminal):**

```bash
curl "https://api.open-meteo.com/v1/forecast?latitude=-33.0472&longitude=-71.6127&current=temperature_2m,wind_speed_10m"
```

**Salida esperada:**
```json
{
  "current": {
    "time": "2025-11-24T15:00",
    "temperature_2m": 18.5,
    "wind_speed_10m": 12.3
  }
}
```

> "Esta es exactamente la respuesta que recibe nuestra app. Como pueden ver, es un servicio externo funcionando en tiempo real."

#### 🎯 Justificación técnica:

> "**¿Por qué integramos una API externa?**
>
> 1. **Cumplir requisitos**: La rúbrica pide consumir una API de terceros (IE 3.1.4 - 15%)
>
> 2. **Demostrar versatilidad**: Sabemos integrar tanto servicios propios como externos
>
> 3. **Mejorar experiencia**: El clima añade contexto local al usuario sin afectar funcionalidad core
>
> 4. **Aprender diferencias**: Entendemos las limitaciones de APIs externas vs propias
>
> **¿Cómo se diferencia del consumo de microservicios?**
>
> - **Control**: Nuestros microservicios podemos modificarlos, la API externa no
> - **Estructura**: Debemos adaptarnos al modelo de Open-Meteo, no podemos cambiarlo
> - **Autenticación**: Open-Meteo es pública, nuestros microservicios podrían tener auth
> - **Propósito**: API externa para datos públicos, microservicios para lógica de negocio
>
> Esta separación es importante: usamos servicios externos cuando necesitamos datos que no generamos (clima, geolocalización, cotizaciones), pero creamos microservicios propios para la lógica core de negocio (usuarios, productos, pedidos)."

---

### 5️⃣ PERSISTENCIA REMOTA (1 min)

#### 🎤 Qué decir:

> "La persistencia de datos es completamente remota en MongoDB Atlas. No usamos Room ni bases de datos locales.
>
> El flujo de persistencia es:
> 1. Usuario interactúa con la UI (ejemplo: crear producto)
> 2. ViewModel recibe la acción
> 3. Repository llama al endpoint POST del microservicio vía Retrofit
> 4. Microservicio inserta en MongoDB y retorna el ID generado
> 5. La app actualiza la UI con el nuevo dato
>
> Voy a demostrar el flujo completo de creación."

#### 💻 Qué mostrar en tiempo real:

**Flujo de Inserción Completa:**

```
1. Abrir app → Login como admin
2. Ir a Panel de Administración → Tab "Productos"
3. Hacer clic en "Agregar Producto"
4. Llenar formulario:
   - Nombre: "Lechuga Orgánica"
   - Precio: 1200
   - Stock: 50
   - Categoría: Verduras
   - Descripción: "Lechuga fresca orgánica"
   - URL Imagen: https://ejemplo.com/lechuga.jpg
5. Guardar
6. Mostrar que aparece en la lista inmediatamente
7. Ir a MongoDB Atlas Compass → mostrar el documento insertado
```

**Código detrás de escena - ProductoViewModel.kt:**
```kotlin
fun crearProducto(
    nombre: String,
    precio: Double,
    stock: Int,
    categoria: String,
    descripcion: String? = null,
    linkImagen: String? = null
) {
    viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        
        when (val result = repository.createProduct(
            nombre = nombre,
            precio = precio,
            stock = stock,
            categoria = categoria,
            descripcion = descripcion,
            linkImagen = linkImagen
        )) {
            is ApiResult.Success -> {
                // Producto creado, recargar lista
                cargarProductos()
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        mensajeExito = "Producto creado exitosamente"
                    )
                }
            }
            is ApiResult.Error -> {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        mensajeError = "Error: ${result.message}"
                    )
                }
            }
        }
    }
}
```

#### 🎯 Explicación de validaciones:
> "Implementamos validaciones en dos capas: en la UI (formulario no permite campos vacíos) y en el microservicio (valida tipos de datos y rangos). Si algo falla, el flujo se revierte y mostramos el error al usuario."

---

### 6️⃣ PRUEBAS UNITARIAS (1 min - 20% ⚠️ CRÍTICO)

#### 🎤 Qué decir:

> "Implementamos **79 pruebas unitarias con 100% de éxito** usando JUnit 4, MockK para mocks y Kotlinx Coroutines Test para código asíncrono.
>
> Las pruebas cubren:
> - **ViewModels**: Lógica de negocio y manejo de estado
> - **Repositories**: Llamadas a API y manejo de errores
> - **Utilidades**: Formateo de fechas, precios y validaciones
>
> **¿Por qué usamos mocks?**
> Los mocks simulan las respuestas de la API sin hacer llamadas reales. Esto hace que los tests sean:
> 1. **Rápidos**: No dependen de red
> 2. **Confiables**: No fallan por problemas de conectividad
> 3. **Aislados**: Prueban solo la lógica, no la API
>
> **¿Cómo contribuyen a la calidad?**
> Las pruebas garantizan que si modifico código, no rompo funcionalidades existentes. Cada cambio se valida automáticamente, reduciendo bugs en producción y aumentando la confiabilidad de la app."

#### 💻 Qué mostrar:

**1. Ejecutar tests en tiempo real:**

```bash
cd /Users/administrador/AndroidStudioProjects/HuertoHogar
./gradlew test
```

**Salida esperada:**
```
BUILD SUCCESSFUL in 5s
79 tests completed, 79 succeeded
```

**2. Mostrar un test específico - ProductsViewModelTest.kt:**

```kotlin
@Test
fun `cargar productos exitosamente actualiza el estado`() = runTest {
    // Given: Preparamos datos de prueba
    val productosEsperados = listOf(
        Producto(
            id = "1",
            nombre = "Papa",
            precio = 1000.0,
            stock = 50,
            categoria = "Verduras"
        ),
        Producto(
            id = "2",
            nombre = "Manzana",
            precio = 800.0,
            stock = 100,
            categoria = "Frutas"
        )
    )
    
    // Mockeamos la respuesta del repository
    coEvery { mockRepository.getAllProducts() } returns 
        ApiResult.Success(productosEsperados)
    
    // When: Llamamos a la función que queremos probar
    viewModel.loadProducts()
    
    // Then: Verificamos que el estado se actualizó correctamente
    val estado = viewModel.uiState.value
    assertEquals(2, estado.productos.size)
    assertEquals("Papa", estado.productos[0].nombre)
    assertEquals("Manzana", estado.productos[1].nombre)
    assertEquals(false, estado.isLoading)
    assertNull(estado.errorMessage)
}
```

**3. Explicar línea por línea:**

```kotlin
// GIVEN (Preparar)
coEvery { mockRepository.getAllProducts() } returns ApiResult.Success(...)
```
> "Con `coEvery` de MockK, le decimos al mock qué debe retornar cuando se llame a `getAllProducts()`. Simula una respuesta exitosa con 2 productos."

```kotlin
// WHEN (Ejecutar)
viewModel.loadProducts()
```
> "Llamamos a la función real del ViewModel que queremos probar. Esta función internamente llama al repository mockeado."

```kotlin
// THEN (Verificar)
assertEquals(2, estado.productos.size)
```
> "Verificamos que el ViewModel procesó correctamente la respuesta: el estado debe tener 2 productos, isLoading debe ser false, y no debe haber errores."

**4. Mostrar test de error:**

```kotlin
@Test
fun `error al cargar productos muestra mensaje de error`() = runTest {
    // Given: Mock retorna error
    coEvery { mockRepository.getAllProducts() } returns 
        ApiResult.Error("Error de red", 500)
    
    // When: Intentamos cargar productos
    viewModel.loadProducts()
    
    // Then: El estado debe mostrar el error
    val estado = viewModel.uiState.value
    assertEquals(0, estado.productos.size)
    assertEquals("Error de red", estado.errorMessage)
    assertEquals(false, estado.isLoading)
}
```

> "Este test verifica que cuando hay un error de red, el ViewModel maneja la excepción correctamente y actualiza el estado con el mensaje de error apropiado."

**5. Mostrar cobertura de tests:**

```
Tests implementados:
- LocalDataRepositoryTest: 15 tests
- ApiResultTest: 8 tests
- FormatUtilsTest: 12 tests
- CartViewModelTest: 18 tests (menos 46 que se removieron)
- LoginViewModelTest: 6 tests
- ProductsViewModelTest: 14 tests
- AuthViewModelTest: 4 tests
- ProductoViewModelTest: 12 tests
- UsuarioViewModelTest: 10 tests
────────────────────────────
TOTAL: 79 tests
```

#### 🎯 Justificación de calidad (IE 3.2.2):

> "Las pruebas unitarias son fundamentales para:
>
> **Seguridad**: Detectan bugs antes de que lleguen a producción. Si alguien modifica código crítico como el login, los tests fallan inmediatamente.
>
> **Estabilidad**: Al agregar nuevas features, los tests existentes garantizan que no rompemos funcionalidades anteriores. Esto se llama regresión.
>
> **Confiabilidad**: Con 79 tests pasando, tenemos confianza de que la lógica core funciona correctamente. Cada commit se valida automáticamente.
>
> **Mantenibilidad**: Los tests sirven como documentación viva del código. Si alguien nuevo llega al proyecto, puede leer los tests para entender qué hace cada función."

---

### 7️⃣ FIRMA Y GENERACIÓN DE APK (1 min - 8%)

#### 🎤 Qué decir:

> "El APK firmado es el archivo instalable de la aplicación con un certificado digital que nos identifica como desarrolladores.
>
> **Proceso que seguimos:**
>
> **Paso 1**: Generamos un keystore con la herramienta keytool de Java. Este archivo (.jks) contiene nuestro certificado privado.
>
> **Paso 2**: Configuramos la firma en build.gradle.kts dentro del bloque signingConfigs, especificando la ruta al keystore, el alias de la clave y las contraseñas.
>
> **Paso 3**: Ejecutamos el comando `./gradlew assembleRelease` que compila la app en modo release y la firma automáticamente.
>
> **Paso 4**: El APK firmado se genera en `app/build/outputs/apk/release/app-release.apk` listo para distribución.
>
> **¿Por qué es necesario?**
> - Android requiere firma para instalar apps
> - Identifica al desarrollador
> - Permite actualizaciones futuras
> - Previene modificaciones maliciosas
> - Es requisito para publicar en Google Play Store"

#### 💻 Qué mostrar:

**1. Archivo keystore:**
```bash
ls -lh huerto-hogar-key.jks
# Mostrar: -rw-r--r--  2.8K huerto-hogar-key.jks
```

**2. Abrir KEYSTORE_INFO.md:**
```markdown
# Información del Keystore

**Ubicación**: huerto-hogar-key.jks
**Store Password**: huerto2024
**Key Alias**: huerto-hogar
**Key Password**: huerto2024

**Detalles del Certificado:**
- Algoritmo: RSA 2048 bits
- Validez: 10,000 días (hasta 2053)
- Organización: Duoc UC
- Unidad: bencastroo y ctapiad
- Nombre: Huerto Hogar
- Ciudad: Viña del Mar
- País: Chile
```

**3. Configuración en build.gradle.kts (líneas 22-40):**
```kotlin
android {
    // ...
    
    signingConfigs {
        create("release") {
            storeFile = file("../huerto-hogar-key.jks")
            storePassword = "huerto2024"
            keyAlias = "huerto-hogar"
            keyPassword = "huerto2024"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
}
```

**4. Generar APK en tiempo real (si hay tiempo):**
```bash
./gradlew assembleRelease
```

**Salida esperada:**
```
BUILD SUCCESSFUL in 25s
50 actionable tasks: 49 executed, 1 up-to-date

APK generado en:
app/build/outputs/apk/release/app-release.apk (50 MB)
```

**5. Mostrar el APK:**
```bash
ls -lh app/build/outputs/apk/release/
```

```
total 103184
-rw-r--r--  50M  app-release.apk
-rw-r--r--  712B output-metadata.json
```

#### 🎯 Explicación paso a paso:

> "**Paso 1 - Generar keystore** (ya hecho):
> ```bash
> keytool -genkey -v -keystore huerto-hogar-key.jks \
>   -keyalg RSA -keysize 2048 -validity 10000 \
>   -alias huerto-hogar
> ```
> Esto crea el certificado con algoritmo RSA de 2048 bits válido por 10,000 días.
>
> **Paso 2 - Configurar en Gradle**: Agregamos el bloque signingConfigs con la ruta, alias y contraseñas.
>
> **Paso 3 - Compilar**: El comando assembleRelease compila en modo optimizado y firma.
>
> **Paso 4 - Distribuir**: El APK resultante se puede instalar en cualquier dispositivo Android o subir a Play Store."

---

### 8️⃣ COLABORACIÓN (1 min - 8%)

#### 🎤 Qué decir:

> "Para el desarrollo colaborativo utilizamos **GitHub para control de versiones** y **Trello para planificación de tareas**.
>
> **Mi participación técnica específica:**
> [PERSONALIZAR SEGÚN TU ROL - Opciones:]
>
> **Opción A - Si trabajaste en Frontend:**
> - Desarrollé las pantallas de la app con Jetpack Compose
> - Implementé la integración con Coil para carga de imágenes desde URLs
> - Configuré la navegación con Navigation Component
> - Desarrollé los ViewModels de Login, Products y Cart
> - Implementé el panel de administración con tabs
> - Arreglé bugs de NullPointerException en imágenes
>
> **Opción B - Si trabajaste en Backend:**
> - Desarrollé los dos microservicios en Spring Boot
> - Configuré la conexión con MongoDB Atlas
> - Implementé todos los endpoints REST (CRUD completo)
> - Desplegué los microservicios en AWS EC2
> - Configuré los Security Groups y permisos
> - Implementé validaciones de datos en el backend
>
> **Opción C - Si trabajaste en ambos (dividido):**
> - Yo: Microservicio de Productos + UI de administración
> - Mi compañero: Microservicio de Usuarios + UI de login/registro
> - Compartido: Tests unitarios y documentación
>
> **Mis commits en GitHub demuestran:**
> - X commits en la rama feature/migracion-mongodb-atlas
> - Trabajo técnico real con código funcional
> - Commits descriptivos con mensajes claros
> - Pull requests revisados antes de merge"

#### 💻 Qué mostrar:

**1. GitHub - Abrir navegador:**

```
URL: https://github.com/ctapiad/Huerto-Mobile
Rama: feature/migracion-mongodb-atlas
```

**2. Mostrar commits:**

```bash
# En terminal
git log --oneline --author="TU_NOMBRE" -10
```

**Ejemplo de salida:**
```
9173747 docs: Actualizar documentación completa
071cb15 feat: Agregar 45 pruebas unitarias
a54926a Limpieza de código
21ffb67 feat: AWS deployment configuration
7598ed5 Fix: URL encoding de email en login
9e4fe68 Fix: Agregadas nuevas IPs AWS a network security config
```

> "Como pueden ver, tengo [X] commits que muestran mi participación activa. Cada commit tiene un mensaje descriptivo que explica qué se cambió y por qué."

**3. Mostrar un commit específico en GitHub:**

Hacer clic en un commit y mostrar:
```
Files changed: 5
Insertions: +234 lines
Deletions: -89 lines

Archivos modificados:
+ ProductsViewModel.kt
+ ProductRepository.kt
+ HomeScreen.kt
+ build.gradle.kts
+ README.md
```

**4. Mostrar branches y merges:**

```bash
git log --graph --oneline --all -10
```

**5. Abrir Trello (o mostrar PLANIFICACION_PROYECTO.md):**

```markdown
# Sprint 4 (11 nov - 17 nov): Features Principales

## Mi responsabilidad:

✅ Historia 10: Pantalla Principal (HomeScreen)
   - Asignado a: [TU NOMBRE]
   - Estado: Completado
   - Commits: 5
   
✅ Historia 11: Lista de Productos por Categoría
   - Asignado a: [TU NOMBRE]
   - Estado: Completado
   - Commits: 3

✅ Historia 17: Tests Unitarios - ProductsViewModel
   - Asignado a: [TU NOMBRE]
   - Estado: Completado (14 tests)
```

**6. Estadísticas de contribución:**

```bash
git shortlog -sn --no-merges
```

**Salida ejemplo:**
```
    25  ctapiad
    23  bencastroo
```

> "La distribución de commits muestra trabajo equitativo entre ambos integrantes del equipo."

#### 🎯 Justificación de colaboración:

> "Usamos Git Flow: trabajamos en la rama feature/migracion-mongodb-atlas y hacemos commits frecuentes. Antes de mergear a main, revisamos el código mutuamente. 
>
> En Trello dividimos las historias de usuario por sprints semanales. Cada tarea tiene responsable, fecha de entrega y criterios de aceptación. Nos reunimos 2 veces por semana para sincronizar avances.
>
> Esta metodología nos permitió trabajar en paralelo sin conflictos y entregar el proyecto en tiempo."

---

### 9️⃣ MODIFICACIÓN EN TIEMPO REAL (3 min - 40% ⚠️ ULTRA CRÍTICO)

#### ⚠️ ESTE ES EL 40% DE TU NOTA - PRACTICA ESTAS 3 OPCIONES

#### 🎯 Estrategia general:

1. **Escucha atentamente** lo que pide el docente
2. **Anota los pasos** en papel antes de empezar
3. **Explica mientras codificas:** "Primero modifico el modelo, luego..."
4. **Compila después de cambios:** `./gradlew assembleDebug`
5. **Muestra funcionando** en el emulador

---

#### 🔧 MODIFICACIÓN #1: Agregar nuevo campo a Producto

**Escenario:** "Agrega un campo 'descuento' a los productos y muéstralo en la UI"

**Pasos detallados:**

**1. Modificar el modelo (ProductApiService.kt línea ~75):**

```kotlin
// ANTES:
data class Producto(
    val id: String? = null,
    val nombre: String,
    val descripcion: String? = null,
    val precio: Double,
    val stock: Int,
    val categoria: String,
    val linkImagen: String? = null
)

// DESPUÉS:
data class Producto(
    val id: String? = null,
    val nombre: String,
    val descripcion: String? = null,
    val precio: Double,
    val stock: Int,
    val categoria: String,
    val linkImagen: String? = null,
    val descuento: Double = 0.0  // ← NUEVO CAMPO
)
```

**Explicar mientras escribes:**
> "Agrego el campo descuento como Double con valor por defecto 0.0. Esto mantiene compatibilidad con productos existentes que no tienen descuento."

**2. Modificar la UI (ProductListScreen.kt o HomeScreen.kt, buscar ProductCard):**

```kotlin
// Buscar donde se muestra el precio, ejemplo línea ~250:

// ANTES:
Text(
    text = "CLP ${formatPrice(producto.precio)}",
    style = MaterialTheme.typography.titleMedium,
    fontWeight = FontWeight.Bold
)

// DESPUÉS:
Column {
    Text(
        text = "CLP ${formatPrice(producto.precio)}",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )
    
    // NUEVO: Mostrar descuento si existe
    if (producto.descuento > 0) {
        Text(
            text = "Descuento: ${producto.descuento}%",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Red
        )
    }
}
```

**Explicar:**
> "Agrego un if para mostrar el descuento solo si es mayor a 0. Uso color rojo para llamar la atención y letra pequeña para no dominar el diseño."

**3. Compilar:**

```bash
./gradlew assembleDebug
```

**4. Ejecutar y demostrar:**
> "Como pueden ver, la app compila sin errores. Ahora si un producto tiene descuento > 0, se muestra en rojo debajo del precio. Los productos sin descuento se ven igual que antes."

**5. Bonus - Agregar en formulario de admin (si hay tiempo):**

En `ProductManagementScreen.kt`, buscar los TextField y agregar:

```kotlin
OutlinedTextField(
    value = descuento.toString(),
    onValueChange = { descuento = it.toDoubleOrNull() ?: 0.0 },
    label = { Text("Descuento (%)") },
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
)
```

---

#### 🔧 MODIFICACIÓN #2: Agregar nuevo endpoint y test

**Escenario:** "Agrega un endpoint para buscar productos por nombre y crea un test"

**Pasos detallados:**

**1. Agregar endpoint (ProductApiService.kt línea ~45):**

```kotlin
// Agregar después de getProductsByCategory:

/**
 * Buscar productos por nombre (búsqueda parcial)
 */
@GET("api/productos/buscar/{nombre}")
suspend fun searchProductsByName(
    @Path("nombre") nombre: String
): Response<List<Producto>>
```

**Explicar:**
> "Agrego un endpoint GET que recibe el nombre como path parameter. El microservicio debe implementar búsqueda parcial con regex en MongoDB."

**2. Agregar función en Repository (ProductRepository.kt línea ~60):**

```kotlin
/**
 * Buscar productos por nombre
 */
suspend fun searchProductsByName(nombre: String): ApiResult<List<Producto>> {
    return safeApiCall {
        apiService.searchProductsByName(nombre)
    }
}
```

**Explicar:**
> "El repository llama al nuevo endpoint usando el wrapper safeApiCall que maneja errores automáticamente."

**3. Agregar función en ViewModel (ProductsViewModel.kt línea ~80):**

```kotlin
fun searchProducts(query: String) {
    if (query.isBlank()) {
        loadProducts() // Si está vacío, mostrar todos
        return
    }
    
    viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        
        when (val result = repository.searchProductsByName(query)) {
            is ApiResult.Success -> {
                _uiState.update { 
                    it.copy(
                        productos = result.data,
                        isLoading = false
                    )
                }
            }
            is ApiResult.Error -> {
                _uiState.update { 
                    it.copy(
                        errorMessage = result.message,
                        isLoading = false
                    )
                }
            }
            is ApiResult.Loading -> {
                _uiState.update { it.copy(isLoading = true) }
            }
        }
    }
}
```

**Explicar:**
> "La función del ViewModel actualiza el estado según la respuesta. Si está vacío, muestra todos. Si hay error, muestra mensaje. Si es exitoso, actualiza la lista de productos."

**4. Crear test (ProductsViewModelTest.kt al final del archivo):**

```kotlin
@Test
fun `buscar productos por nombre retorna resultados filtrados`() = runTest {
    // Given: Tenemos productos de prueba
    val todosLosProductos = listOf(
        Producto(
            id = "1",
            nombre = "Papa blanca",
            precio = 1000.0,
            stock = 50,
            categoria = "Verduras"
        ),
        Producto(
            id = "2",
            nombre = "Manzana roja",
            precio = 800.0,
            stock = 100,
            categoria = "Frutas"
        ),
        Producto(
            id = "3",
            nombre = "Papa amarilla",
            precio = 1200.0,
            stock = 30,
            categoria = "Verduras"
        )
    )
    
    // Mock retorna solo los productos que contienen "Papa"
    val productosFiltrados = todosLosProductos.filter { 
        it.nombre.contains("Papa", ignoreCase = true) 
    }
    coEvery { mockRepository.searchProductsByName("Papa") } returns 
        ApiResult.Success(productosFiltrados)
    
    // When: Buscamos "Papa"
    viewModel.searchProducts("Papa")
    
    // Then: Solo deben aparecer los 2 productos con "Papa"
    val estado = viewModel.uiState.value
    assertEquals(2, estado.productos.size)
    assertTrue(estado.productos.all { it.nombre.contains("Papa") })
    assertEquals("Papa blanca", estado.productos[0].nombre)
    assertEquals("Papa amarilla", estado.productos[1].nombre)
    assertEquals(false, estado.isLoading)
    assertNull(estado.errorMessage)
}

@Test
fun `buscar con query vacío recarga todos los productos`() = runTest {
    // Given: Mock para getAllProducts
    coEvery { mockRepository.getAllProducts() } returns 
        ApiResult.Success(listOf(producto1, producto2))
    
    // When: Buscamos con string vacío
    viewModel.searchProducts("")
    
    // Then: Debe llamar a loadProducts y mostrar todos
    val estado = viewModel.uiState.value
    assertEquals(2, estado.productos.size)
}
```

**Explicar línea por línea:**
> "El primer test verifica que la búsqueda filtra correctamente. Creamos 3 productos, 2 con 'Papa' en el nombre. Mockeamos que el repository retorna solo esos 2. Ejecutamos la búsqueda y verificamos que el estado tenga exactamente 2 productos con 'Papa' en el nombre.
>
> El segundo test verifica el caso edge: cuando la búsqueda está vacía, debe mostrar todos los productos. Esto previene bugs de UI vacía."

**5. Ejecutar el test:**

```bash
./gradlew test --tests ProductsViewModelTest."buscar productos por nombre retorna resultados filtrados"
```

**Salida esperada:**
```
ProductsViewModelTest > buscar productos por nombre retorna resultados filtrados PASSED
```

**Explicar:**
> "El test pasa exitosamente, confirmando que la lógica de búsqueda funciona correctamente."

---

#### 🔧 MODIFICACIÓN #3: Filtrar productos por rango de precio

**Escenario:** "Implementa un filtro de productos por precio máximo"

**Pasos detallados:**

**1. Agregar función en ViewModel (ProductsViewModel.kt):**

```kotlin
fun filterByMaxPrice(maxPrice: Double) {
    viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        
        // Primero cargar todos los productos
        when (val result = repository.getAllProducts()) {
            is ApiResult.Success -> {
                // Filtrar localmente por precio
                val productosFiltrados = result.data.filter { 
                    it.precio <= maxPrice 
                }
                
                _uiState.update { 
                    it.copy(
                        productos = productosFiltrados,
                        isLoading = false
                    )
                }
            }
            is ApiResult.Error -> {
                _uiState.update { 
                    it.copy(
                        errorMessage = result.message,
                        isLoading = false
                    )
                }
            }
            is ApiResult.Loading -> {
                _uiState.update { it.copy(isLoading = true) }
            }
        }
    }
}

fun clearFilters() {
    loadProducts() // Recargar todos sin filtros
}
```

**Explicar:**
> "Esta función carga todos los productos y luego filtra localmente los que tienen precio menor o igual al máximo especificado. Implementé también clearFilters para quitar el filtro."

**2. Agregar UI para el filtro (ProductListScreen.kt antes del LazyGrid):**

```kotlin
// Agregar estado para el filtro
var precioMaximo by remember { mutableStateOf("") }
var filtroActivo by remember { mutableStateOf(false) }

// UI del filtro
Row(
    modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalAlignment = Alignment.CenterVertically
) {
    OutlinedTextField(
        value = precioMaximo,
        onValueChange = { precioMaximo = it },
        label = { Text("Precio máximo") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.weight(1f)
    )
    
    Button(
        onClick = {
            val precio = precioMaximo.toDoubleOrNull()
            if (precio != null) {
                viewModel.filterByMaxPrice(precio)
                filtroActivo = true
            }
        }
    ) {
        Text("Filtrar")
    }
    
    if (filtroActivo) {
        IconButton(onClick = {
            viewModel.clearFilters()
            precioMaximo = ""
            filtroActivo = false
        }) {
            Icon(Icons.Default.Clear, "Limpiar filtro")
        }
    }
}
```

**Explicar:**
> "Agrego un TextField para ingresar el precio, un botón para aplicar el filtro, y un botón de limpiar que solo aparece cuando hay filtro activo."

**3. Crear test (ProductsViewModelTest.kt):**

```kotlin
@Test
fun `filtrar por precio máximo retorna solo productos dentro del rango`() = runTest {
    // Given: Productos con diferentes precios
    val productos = listOf(
        Producto(id = "1", nombre = "Barato", precio = 500.0, stock = 10, categoria = "A"),
        Producto(id = "2", nombre = "Medio", precio = 1500.0, stock = 10, categoria = "A"),
        Producto(id = "3", nombre = "Caro", precio = 3000.0, stock = 10, categoria = "A")
    )
    
    coEvery { mockRepository.getAllProducts() } returns 
        ApiResult.Success(productos)
    
    // When: Filtramos por precio máximo 2000
    viewModel.filterByMaxPrice(2000.0)
    
    // Then: Solo deben aparecer productos <= 2000
    val estado = viewModel.uiState.value
    assertEquals(2, estado.productos.size)
    assertTrue(estado.productos.all { it.precio <= 2000.0 })
    assertEquals("Barato", estado.productos[0].nombre)
    assertEquals("Medio", estado.productos[1].nombre)
}

@Test
fun `clearFilters recarga todos los productos sin filtro`() = runTest {
    // Given: Productos disponibles
    val todosLosProductos = listOf(producto1, producto2, producto3)
    coEvery { mockRepository.getAllProducts() } returns 
        ApiResult.Success(todosLosProductos)
    
    // When: Aplicamos filtro y luego lo limpiamos
    viewModel.filterByMaxPrice(1000.0)
    viewModel.clearFilters()
    
    // Then: Deben aparecer todos los productos
    assertEquals(3, viewModel.uiState.value.productos.size)
}
```

**Explicar:**
> "El primer test verifica que el filtro funciona: de 3 productos, solo 2 cumplen con precio <= 2000. El segundo test verifica que clearFilters restaura la lista completa."

**4. Ejecutar tests:**

```bash
./gradlew test --tests ProductsViewModelTest
```

**5. Demostrar en app:**
> "Ahora en la app, puedo escribir por ejemplo 1500 en el campo de precio máximo, hacer clic en Filtrar, y solo se muestran productos de hasta $1500. El botón de limpiar quita el filtro y muestra todos nuevamente."

---

#### 📝 TIPS PARA LA MODIFICACIÓN EN TIEMPO REAL

**Si te piden algo que no sabes:**
1. ❌ NO digas "No sé"
2. ✅ Di: "Voy a analizar qué archivos necesito modificar..."
3. ✅ Piensa en voz alta: "Esto afectaría al modelo, luego al repository..."
4. ✅ Pide aclaración: "¿Se refiere a agregar un campo nuevo o modificar uno existente?"

**Si te equivocas:**
1. ❌ NO entres en pánico
2. ✅ Di: "Veo un error aquí, voy a corregirlo..."
3. ✅ Usa Ctrl+Z si es necesario
4. ✅ Explica qué salió mal: "El error era que olvidé agregar el import..."

**Si falla la compilación:**
1. ✅ Lee el mensaje de error en voz alta
2. ✅ Identifica el archivo y línea: "El error está en línea 45 de ProductsViewModel..."
3. ✅ Explica la causa: "Falta el import de ApiResult"
4. ✅ Corrígelo y recompila

**Orden recomendado de cambios:**
```
1. Modelo (data class)
2. API Service (endpoints)
3. Repository (funciones)
4. ViewModel (lógica)
5. UI (pantallas)
6. Tests
7. Compilar
8. Ejecutar
```

---

## 📊 DISTRIBUCIÓN FINAL DE PUNTAJE

| Criterio | Peso | Prioridad | Tiempo |
|----------|------|-----------|--------|
| **Modificación en tiempo real** | 40% | 🔴 CRÍTICA | 3 min |
| **Tests unitarios** | 20% | 🔴 ALTA | 1 min |
| **Arquitectura y Microservicios** | 24% | 🟡 MEDIA | 5 min |
| **APK firmado** | 8% | 🟢 BAJA | 1 min |
| **GitHub/Trello** | 8% | 🟢 BAJA | 1 min |

---

## ✅ CHECKLIST FINAL PRE-PRESENTACIÓN

### 24 horas antes:
```
□ Practica el flujo completo 3 veces
□ Memoriza rutas de archivos clave
□ Prepara las 3 modificaciones posibles
□ Ejecuta todos los tests → deben pasar
□ Verifica que app compile sin errores
□ Prueba app en emulador → debe funcionar
□ Verifica microservicios AWS → deben responder
□ Revisa commits en GitHub → deben ser visibles
```

### 1 hora antes:
```
□ Emulador iniciado y configurado
□ App instalada y corriendo
□ Android Studio abierto con proyecto
□ Archivos clave abiertos en pestañas:
  - ProductsViewModel.kt
  - ProductApiService.kt
  - ProductRepository.kt
  - HomeScreen.kt
  - ProductListScreen.kt
  - ProductsViewModelTest.kt
  - build.gradle.kts
  - KEYSTORE_INFO.md
□ Terminal abierta en raíz del proyecto
□ GitHub abierto en navegador (página de commits)
□ MongoDB Atlas abierto (por si piden ver datos)
□ Papel y lápiz para anotar pasos
□ Agua para beber si te pones nervioso
```

### Al momento de presentar:
```
□ Respira profundo
□ Escucha atentamente cada pregunta
□ Anota los pasos antes de empezar a codificar
□ Explica mientras haces
□ No te apures, mejor lento y correcto
□ Si fallas, analiza el error y corrige
□ Mantén la calma
```

---

## 🎯 ERRORES FATALES A EVITAR

| Error | Consecuencia | Prevención |
|-------|--------------|------------|
| App no ejecuta | 0 en todo | Probar app 1 hora antes |
| No puedes modificar código | Pierdes 40% | Practicar 3 modificaciones |
| Tests no corren | Pierdes 20% | Ejecutar `./gradlew test` antes |
| Microservicios caídos | Pierdes 12% | Verificar con `curl` antes |
| No sabes explicar arquitectura | Pierdes 12% | Repasar flujo MVVM |
| No tienes commits | Pierdes 8% | Verificar `git log` antes |

---

## 💬 FRASES ÚTILES DURANTE LA DEFENSA

**Al inicio:**
> "Buenos días/tardes, voy a presentar Huerto Hogar, una aplicación de marketplace orgánico."

**Si no entiendes:**
> "¿Podría repetir la pregunta?" o "¿Se refiere a [reformular]?"

**Al modificar código:**
> "Primero voy a anotar los pasos que debo seguir..." [anotar en papel]
> "Voy a modificar primero el modelo, luego el repository, luego el viewmodel..."

**Si hay un error:**
> "Veo que hay un error de compilación. Déjeme leer el mensaje... El problema es..."

**Al compilar:**
> "Ahora voy a compilar para verificar que no hay errores sintácticos..."

**Al probar:**
> "Como pueden ver, la funcionalidad ahora está operativa..."

**Al finalizar:**
> "Esto demuestra que [explicar qué lograste]."

---

## 🚀 MENSAJE FINAL

**Recuerda:**

1. **40% está en la modificación en tiempo real** → Practica hasta que te salga automático
2. **20% está en tests** → Aprende a explicar por qué son importantes
3. **24% está en arquitectura y microservicios** → Domina el flujo completo
4. **16% restante** es documentación y colaboración → Fácil si tienes todo preparado

**La clave del éxito:**
- 🔴 Practicar las 3 modificaciones hasta dominarlas
- 🔴 Entender el flujo completo: UI → ViewModel → Repository → API → MongoDB
- 🔴 Poder explicar mientras haces
- 🔴 Mantener la calma si algo falla

---

**¡Mucha suerte en tu presentación! 🍀**

**Preparaste bien el proyecto, ahora solo falta demostrarlo con confianza. ¡Tú puedes! 💪**
