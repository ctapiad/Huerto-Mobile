# Documentación de Código - Huerto Mobile

> Documentación completa de todas las clases Kotlin del paquete `com.example.huerto_hogar`

---

## 📋 Índice

1. [Aplicación Principal](#aplicación-principal)
2. [Networking - API](#networking---api)
3. [Repositorios](#repositorios)
4. [Servicios](#servicios)
5. [Modelos de Datos](#modelos-de-datos)
6. [DTOs y Enumeraciones](#dtos-y-enumeraciones)
7. [ViewModels](#viewmodels)
8. [UI - Pantallas y Temas](#ui---pantallas-y-temas)
9. [Utilidades](#utilidades)

---

## Aplicación Principal

### HuertoApplication.kt
**Paquete:** `com.example.huerto_hogar`

**Descripción:**  
Clase de aplicación principal que hereda de `Application`. Se ejecuta cuando la aplicación se inicia.

**Responsabilidades:**
- Inicializar componentes globales de la aplicación
- Configurar logs de inicio de la aplicación
- Punto de entrada para inicialización de librerías o SDKs

**Métodos:**
- `onCreate()`: Método del ciclo de vida donde se inicializa la aplicación

**Notas:**
- Registrada en `AndroidManifest.xml` como `android:name=".HuertoApplication"`
- Usa logs para facilitar debugging del inicio de la app

---

### MainActivity.kt
**Paquete:** `com.example.huerto_hogar`

**Descripción:**  
Actividad principal de la aplicación que contiene toda la navegación y estructura UI usando Jetpack Compose.

**Características principales:**
- **Navegación:** Usa `NavController` para gestionar rutas entre pantallas
- **UI:** Implementa `ModalNavigationDrawer` con menú lateral
- **Scaffold:** TopAppBar centralizada con botón de menú hamburguesa
- **Imagen de fondo:** Muestra `huertohogarfondo` como fondo de toda la app
- **ViewModel compartido:** `CartViewModel` es único para toda la aplicación

**Rutas de navegación:**
```kotlin
object Routes {
    const val HOME = "home"
    const val LOGIN = "login"
    const val PRODUCTS = "products"
    const val ADMIN_DASHBOARD = "admin_dashboard"
    const val CART = "cart"
    const val USER_PROFILE = "user_profile"
    const val PAYMENT_SUCCESS = "payment_success"
}
```

**Composables:**
- `AppNavigation()`: Gestiona toda la navegación con NavHost
- `AppDrawerContent()`: Contenido del menú lateral que se adapta según el usuario logueado

**Flujo de navegación:**
1. Usuario invitado → puede ver HOME, PRODUCTS, CART
2. Al agregar al carrito sin login → redirige a LOGIN
3. Usuario ADMIN → acceso adicional a ADMIN_DASHBOARD
4. Checkout exitoso → navega a PAYMENT_SUCCESS con parámetros

**Test de conexión:**
- Método `testMicroservicesConnection()` (comentado) para verificar conectividad con APIs

---

### SplashActivity.kt
**Paquete:** `com.example.huerto_hogar`

**Descripción:**  
Pantalla de bienvenida que se muestra durante 2.5 segundos al iniciar la aplicación.

**Flujo:**
1. Muestra layout `activity_splash.xml`
2. Espera 2.5 segundos usando `Handler`
3. Navega a `MainActivity`
4. Se cierra automáticamente con `finish()` para evitar volver con botón atrás

**Manejo de errores:**
- Si falla la carga del splash, intenta ir directo a MainActivity
- Logs detallados para debugging

---

## Networking - API

### ApiConfig.kt
**Paquete:** `com.example.huerto_hogar.network`

**Descripción:**  
Configuración centralizada de URLs de los microservicios desplegados en AWS EC2.

**Constantes:**
```kotlin
const val USER_SERVICE_BASE_URL = "http://34.193.190.24:8081/"
const val PRODUCT_SERVICE_BASE_URL = "http://34.202.46.121:8081/"
const val TIMEOUT_SECONDS = 30L
```

**Microservicios:**
- **Usuario:** EC2 en `34.193.190.24:8081` (MongoDB Atlas)
- **Producto:** EC2 en `34.202.46.121:8081` (MongoDB Atlas)

**Documentación Swagger:**
- Usuarios: `http://34.193.190.24:8081/swagger-ui/index.html#/`
- Productos: `http://34.202.46.121:8081/swagger-ui/index.html#/`

---

### ApiResult.kt
**Paquete:** `com.example.huerto_hogar.network`

**Descripción:**  
Clase sellada para representar el resultado de operaciones de red de manera tipo-segura.

**Estados:**
```kotlin
sealed class ApiResult<out T> {
    data class Success<out T>(val data: T)
    data class Error(val message: String, val code: Int? = null)
    object Loading
}
```

**Función de utilidad:**
```kotlin
suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): ApiResult<T>
```

**Manejo especial:**
- **HTTP 204 (No Content):** Devuelve lista vacía en lugar de error
- **Respuestas 200-299 sin body:** Devuelve mensaje genérico de éxito
- **JsonSyntaxException:** Asume operación exitosa (para respuestas texto plano)
- **Errores de red:** Captura excepciones y retorna mensaje amigable

**Ventajas:**
- Permite usar `when` exhaustivo para todos los casos
- Facilita manejo de estados de loading/success/error en UI
- Logs automáticos de errores HTTP

---

### RetrofitClient.kt
**Paquete:** `com.example.huerto_hogar.network`

**Descripción:**  
Configuración de clientes Retrofit para comunicación con microservicios.

**Objetos singleton:**

#### UserApiClient
```kotlin
object UserApiClient {
    val apiService: UserApiService
}
```
- URL base: `USER_SERVICE_BASE_URL`
- Convertidores: ScalarsConverter (texto plano) + GsonConverter (JSON)
- Timeout: 30 segundos

#### ProductApiClient
```kotlin
object ProductApiClient {
    val apiService: ProductApiService
}
```
- URL base: `PRODUCT_SERVICE_BASE_URL`
- Convertidores: ScalarsConverter + GsonConverter
- Timeout: 30 segundos

**Configuraciones compartidas:**
- `HttpLoggingInterceptor` en nivel BODY para debugging
- `GsonBuilder().setLenient()` para parseo flexible
- Timeouts configurables: connect, read, write

---

### UserApiService.kt
**Paquete:** `com.example.huerto_hogar.network`

**Descripción:**  
Interfaz Retrofit que define todos los endpoints del microservicio de Usuarios.

**Endpoints REST:**

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/usuarios` | Obtener todos los usuarios |
| GET | `/api/usuarios/{id}` | Obtener usuario por ID (con password) |
| GET | `/api/usuarios/{id}/dto` | Obtener usuario por ID (sin password) |
| GET | `/api/usuarios/email/{email}` | Buscar por email |
| GET | `/api/usuarios/buscar/{nombre}` | Buscar por nombre |
| GET | `/api/usuarios/tipo/{idTipoUsuario}` | Filtrar por tipo |
| POST | `/api/usuarios` | Crear nuevo usuario |
| PUT | `/api/usuarios` | Actualizar usuario |
| DELETE | `/api/usuarios/{id}` | Eliminar usuario |

**DTOs:**
```kotlin
data class Usuario(
    val id: String?,
    val nombre: String,
    val email: String,
    val password: String,
    val fechaRegistro: String?,
    val direccion: String?,
    val telefono: Int?,
    val idComuna: Int?,
    val idTipoUsuario: Int?
)

data class UsuarioDto(
    // Igual que Usuario pero sin password
)
```

**Tipos de usuario:**
- `1` = ADMIN
- `2` = VENDEDOR
- `3` = CLIENTE

---

### ProductApiService.kt
**Paquete:** `com.example.huerto_hogar.network`

**Descripción:**  
Interfaz Retrofit que define todos los endpoints del microservicio de Productos.

**Endpoints REST:**

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/productos/health` | Health check del servicio |
| GET | `/api/productos` | Obtener todos los productos |
| GET | `/api/productos/activos` | Productos activos (estaActivo=true) |
| GET | `/api/productos/disponibles` | Activos con stock > 0 |
| GET | `/api/productos/organicos` | Con certificación orgánica |
| GET | `/api/productos/stock-bajo` | Stock menor al mínimo especificado |
| GET | `/api/productos/categoria/{id}` | Filtrar por categoría |
| GET | `/api/productos/precio?min&max` | Filtrar por rango de precio |
| GET | `/api/productos/buscar?nombre` | Buscar por nombre |
| GET | `/api/productos/{id}` | Obtener producto por ID |
| POST | `/api/productos` | Crear nuevo producto |
| PUT | `/api/productos/{id}` | Actualizar producto |
| PATCH | `/api/productos/{id}/stock` | Actualizar solo stock |
| PATCH | `/api/productos/{id}/desactivar` | Desactivar producto |
| DELETE | `/api/productos/{id}` | Eliminar producto |
| POST | `/api/productos/upload-url` | Obtener URL prefirmada para S3 |

**DTOs principales:**
```kotlin
data class ProductoDto(
    val idProducto: String,      // Patrón: PR001, VE002, etc.
    val nombre: String,
    val linkImagen: String?,     // URL pública de S3
    val descripcion: String,
    val precio: Int,            // En pesos chilenos
    val stock: Int,
    val origen: String?,
    val certificacionOrganica: Boolean,
    val estaActivo: Boolean,
    val fechaIngreso: String?,  // ISO 8601
    val idCategoria: Int,
    val createdAt: String?,     // MongoDB timestamp
    val updatedAt: String?      // MongoDB timestamp
)

data class CrearProductoDto(
    val idProducto: String,     // Validación: ^[A-Z]{2}[0-9]{3}$
    val nombre: String,         // Max 100 caracteres
    val linkImagen: String?,    // Max 255 caracteres
    val descripcion: String,    // Max 500 caracteres
    val precio: Int,           // Mínimo 1
    val stock: Int,            // Mínimo 0
    val origen: String?,       // Max 100 caracteres
    val certificacionOrganica: Boolean,
    val estaActivo: Boolean,
    val idCategoria: Int
)

data class ActualizarProductoDto(
    // Todos los campos opcionales para actualización parcial
    val nombre: String?,
    val linkImagen: String?,
    // ... etc
)
```

**Upload de imágenes a S3:**
```kotlin
data class UploadUrlRequest(
    val fileName: String,
    val contentType: String
)

data class UploadUrlResponse(
    val uploadUrl: String,    // URL prefirmada para PUT
    val imageUrl: String,     // URL pública final
    val key: String,          // Key en S3
    val expiresIn: String     // Tiempo de expiración
)
```

---

### NetworkTestHelper.kt
**Paquete:** `com.example.huerto_hogar.network`

**Descripción:**  
Utilidad para probar la conexión con los microservicios antes de usar la app.

**Métodos:**

#### testConnection()
```kotlin
fun testConnection(callback: (success: Boolean, message: String) -> Unit)
```
Prueba la conectividad con ambos microservicios en paralelo:
- Llama a `/api/productos/disponibles`
- Llama a `/api/usuarios`
- Retorna resultado consolidado con emoji indicators (✅/❌/⚠️)

#### testLogin()
```kotlin
fun testLogin(email: String, password: String, callback: ...)
```
Prueba credenciales de login sin guardar sesión.

#### testProductSearch()
```kotlin
fun testProductSearch(query: String, callback: ...)
```
Prueba búsqueda de productos y retorna cantidad encontrada.

**Uso típico:**
```kotlin
NetworkTestHelper.testConnection { success, message ->
    Log.d("NetworkTest", message)
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}
```

---

## Repositorios

### UserRepository.kt
**Paquete:** `com.example.huerto_hogar.network.repository`

**Descripción:**  
Capa de abstracción sobre `UserApiService` que simplifica llamadas a la API de usuarios.

**Métodos principales:**

```kotlin
// Consultas
suspend fun getAllUsers(): ApiResult<List<Usuario>>
suspend fun getUserById(userId: String): ApiResult<Usuario>
suspend fun getUserDtoById(userId: String): ApiResult<UsuarioDto>
suspend fun getUserByEmail(email: String): ApiResult<Usuario>
suspend fun searchUsersByName(name: String): ApiResult<List<Usuario>>
suspend fun getUsersByType(userTypeId: Int): ApiResult<List<Usuario>>

// Autenticación
suspend fun login(email: String, password: String): ApiResult<Usuario>

// Modificaciones
suspend fun createUser(...): ApiResult<String>
suspend fun updateUser(...): ApiResult<String>
suspend fun deleteUser(userId: String): ApiResult<String>
```

**Lógica especial de login:**
1. Busca usuario por email
2. Si existe, compara contraseña en texto plano (⚠️ sin hash)
3. Retorna `ApiResult.Success(user)` o `ApiResult.Error("Contraseña incorrecta")`

**updateUser:**
- Primero hace GET para obtener datos actuales
- Mezcla campos nuevos con existentes (solo actualiza los que se pasan)
- Envía objeto completo al PUT

---

### ProductRepository.kt
**Paquete:** `com.example.huerto_hogar.network.repository`

**Descripción:**  
Capa de abstracción sobre `ProductApiService` para operaciones con productos.

**Métodos principales:**

```kotlin
// Consultas
suspend fun healthCheck(): ApiResult<String>
suspend fun getAllProducts(): ApiResult<List<ProductoDto>>
suspend fun getActiveProducts(): ApiResult<List<ProductoDto>>
suspend fun getAvailableProducts(): ApiResult<List<ProductoDto>>
suspend fun getOrganicProducts(): ApiResult<List<ProductoDto>>
suspend fun getLowStockProducts(minStock: Int): ApiResult<List<ProductoDto>>
suspend fun getProductsByCategory(categoryId: Int): ApiResult<List<ProductoDto>>
suspend fun getProductsByPriceRange(minPrice: Int, maxPrice: Int): ApiResult<List<ProductoDto>>
suspend fun searchProducts(name: String): ApiResult<List<ProductoDto>>
suspend fun getProductById(productId: String): ApiResult<ProductoDto>

// Modificaciones
suspend fun createProduct(...): ApiResult<ProductoDto>
suspend fun updateProduct(producto: ProductoDto): ApiResult<ProductoDto>
suspend fun updateProduct(productId: String, ...): ApiResult<ProductoDto>
suspend fun updateStock(productId: String, newStock: Int): ApiResult<Unit>
suspend fun deactivateProduct(productId: String): ApiResult<Unit>
suspend fun deleteProduct(productId: String): ApiResult<Unit>

// Imágenes
suspend fun getPresignedUploadUrl(request: UploadUrlRequest): ApiResult<UploadUrlResponse>
```

**Sobrecarga de updateProduct:**
- Versión 1: Recibe `ProductoDto` completo y lo envía al backend
- Versión 2: Recibe campos opcionales y construye `ActualizarProductoDto`

**Logs de debugging:**
- Logs detallados en `updateProduct` para tracking de cambios

---

## Servicios

### ImageUploadService.kt
**Paquete:** `com.example.huerto_hogar.service`

**Descripción:**  
Servicio para manejar la subida de imágenes a AWS S3 mediante URLs prefirmadas.

**Métodos:**

#### getFileInfo()
```kotlin
fun getFileInfo(uri: Uri): FileInfo?
```
- Extrae metadata del archivo seleccionado
- Retorna nombre, tipo MIME, tamaño y URI

#### uploadToS3()
```kotlin
suspend fun uploadToS3(presignedUrl: String, uri: Uri, contentType: String): Boolean
```
- Lee bytes del archivo desde el URI
- Hace PUT request a la URL prefirmada con el contenido
- Usa `OkHttpClient` en lugar de Retrofit

#### Validaciones:
```kotlin
fun isValidImageType(mimeType: String): Boolean
// Acepta: image/jpeg, image/jpg, image/png, image/gif, image/webp

fun isValidFileSize(sizeBytes: Long, maxSizeMB: Int = 5): Boolean
// Por defecto máximo 5MB
```

**Data class:**
```kotlin
data class FileInfo(
    val fileName: String,
    val mimeType: String,
    val size: Long,
    val uri: Uri
)
```

**Flujo de uso:**
1. Usuario selecciona imagen desde galería
2. `getFileInfo()` extrae metadata
3. Validar tipo y tamaño
4. `ProductRepository.getPresignedUploadUrl()` obtiene URL S3
5. `uploadToS3()` sube el archivo
6. Backend retorna URL pública de la imagen

---

## Modelos de Datos

### User.kt
**Paquete:** `com.example.huerto_hogar.data.model`

**Descripción:**  
Modelo local de usuario usado en la sesión actual (no se persiste en base de datos).

```kotlin
data class User(
    val id: Long,
    var name: String,
    val email: String,
    var password: String,
    val registrationDate: Date,
    var address: String?,
    var phone: Int?,
    var comunaId: Long,
    val role: UserRole
)
```

**Notas:**
- Usado por `LocalDataRepository` para mantener sesión
- Se crea a partir de `Usuario` (DTO de API) en el login
- Campos mutables: name, password, address, phone

---

### Product.kt
**Paquete:** `com.example.huerto_hogar.data.model`

**Descripción:**  
Modelo local de producto usado en la UI (convertido desde `ProductoDto`).

```kotlin
data class Product(
    val id: String,
    var name: String,
    var imageName: String?,
    var description: String?,
    var price: Double,          // Convertido de Int a Double
    var stock: Int,
    var origin: String?,
    var isOrganic: Boolean,
    var isActive: Boolean,
    val entryDate: Date,
    val categoryId: Long,
    var priceUnit: String = "kg"
)
```

**Diferencias con ProductoDto:**
- `price`: Double en lugar de Int (para compatibilidad con UI antigua)
- `priceUnit`: Campo adicional para mostrar unidad de medida

---

### Pedido.kt
**Paquete:** `com.example.huerto_hogar.data.model`

**Descripción:**  
Modelo de pedido (⚠️ NO SE PERSISTE - solo para UI de confirmación).

```kotlin
data class Pedido(
    val id: Long,
    val orderDate: Date,
    var deliveryDate: Date?,
    val total: Double,
    val deliveryAddress: String,
    val userId: Long,
    var status: OrderStatus
)
```

**Estados posibles:** Ver `OrderStatus` enum.

---

### DetallePedido.kt
**Paquete:** `com.example.huerto_hogar.data.model`

**Descripción:**  
Modelo de detalle de pedido (⚠️ NO SE PERSISTE).

```kotlin
data class DetallePedido(
    val pedidoId: Long,
    val productId: String,
    val cantidad: Int,
    val precioUnitario: Double,
    val subtotal: Double
)
```

---

## DTOs y Enumeraciones

### CartItem.kt
**Paquete:** `com.example.huerto_hogar.data.dto`

**Descripción:**  
Item del carrito de compras (solo en memoria).

```kotlin
data class CartItem(
    val product: Product,
    var quantity: Int
)
```

---

### Categoria.kt
**Paquete:** `com.example.huerto_hogar.data.catalog`

**Descripción:**  
Modelo de categoría (⚠️ HARDCODED - no hay API de categorías).

```kotlin
data class Categoria(
    val id: Long,
    val name: String,
    val description: String? = null
)
```

**Categorías predefinidas:**
1. Frutas
2. Verduras
3. Carnes
4. Lácteos
5. Granos

---

### UserRole.kt
**Paquete:** `com.example.huerto_hogar.data.enums`

**Descripción:**  
Enum para roles de usuario.

```kotlin
enum class UserRole(val id: Long) {
    ADMIN(1), 
    VENDEDOR(2), 
    CLIENTE(3)
}
```

---

### OrderStatus.kt
**Paquete:** `com.example.huerto_hogar.data.enums`

**Descripción:**  
Enum para estados de pedido.

```kotlin
enum class OrderStatus(val id: Long) {
    PENDIENTE(1), 
    PREPARACION(2), 
    ENVIADO(3), 
    ENTREGADO(4), 
    CANCELADO(5)
}
```

---

### ProductoUIState.kt
**Paquete:** `com.example.huerto_hogar.model`

**Descripción:**  
Estado de formulario de producto para crear/editar.

```kotlin
data class ProductoUIState(
    val idProducto: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val precio: Int = 0,
    val stock: Int = 0,
    val linkImagen: String = "",
    val origen: String = "",
    val certificacionOrganica: Boolean = false,
    val estaActivo: Boolean = true,
    val idCategoria: Int = 1,
    val errores: ProductoErrores = ProductoErrores()
)
```

---

### ProductoErrores.kt
**Paquete:** `com.example.huerto_hogar.model`

**Descripción:**  
Errores de validación para formulario de producto.

```kotlin
data class ProductoErrores(
    val idProducto: String? = null,
    val nombre: String? = null,
    val descripcion: String? = null,
    val precio: String? = null,
    val stock: String? = null,
    val linkImagen: String? = null,
    val origen: String? = null,
    val idCategoria: String? = null
)
```

**Uso:** Cada campo null significa "sin error", string con mensaje indica error.

---

### UsuarioUIState.kt
**Paquete:** `com.example.huerto_hogar.model`

**Descripción:**  
Estado de formulario de usuario para crear/editar.

```kotlin
data class UsuarioUIState(
    val nombre: String = "",
    val email: String = "",
    val password: String = "",
    val direccion: String = "",
    val telefono: Int = 0,
    val idComuna: Int = 1,
    val idTipoUsuario: Int = 0,
    val errores: UsuarioErrores = UsuarioErrores()
)
```

---

### UsuarioErrores.kt
**Paquete:** `com.example.huerto_hogar.model`

**Descripción:**  
Errores de validación para formulario de usuario.

```kotlin
data class UsuarioErrores(
    val nombre: String? = null,
    val email: String? = null,
    val password: String? = null,
    val direccion: String? = null,
    val telefono: String? = null,
    val idComuna: String? = null,
    val idTipoUsuario: String? = null
)
```

---

## ViewModels

### AuthViewModel.kt
**Paquete:** `com.example.huerto_hogar.viewmodel`

**Descripción:**  
ViewModel simplificado para autenticación. Solo maneja logout y estado de usuario.

```kotlin
class AuthViewModel(application: Application) : AndroidViewModel(application) {
    val currentUser: StateFlow<User?> = LocalDataRepository.currentUser
    
    fun logout()
}
```

**Nota:** El login se maneja en `LoginViewModel` (en paquete ui.auth).

---

### CartViewModel.kt
**Paquete:** `com.example.huerto_hogar.viewmodel`

**Descripción:**  
ViewModel que gestiona el carrito de compras en memoria (sin persistencia).

**Estados:**
```kotlin
val cartItems: StateFlow<Map<String, CartItem>>
val currentUser: StateFlow<User?>
```

**Métodos:**

```kotlin
fun addToCart(product: Product, quantity: Int): Result<Unit>
fun updateCartItemQuantity(productId: String, newQuantity: Int): Result<Unit>
fun removeFromCart(productId: String): Result<Unit>
fun clearCart()
fun getCartTotal(): Double
fun getCartItemCount(): Int
fun getCartItems(): List<CartItem>

fun createOrderFromCart(deliveryAddress: String, callback: (Result<Long>) -> Unit)
```

**createOrderFromCart - IMPORTANTE:**
- ⚠️ NO persiste el pedido en base de datos
- Solo simula un pedido exitoso con ID ficticio aleatorio
- Limpia el carrito después de "crear" el pedido
- Calcula envío: gratis si total ≥ $50.000, sino $3.000

**Validaciones:**
- Stock disponible al agregar
- No permitir cantidades negativas
- Usuario debe estar logueado para checkout

---

### ProductoViewModel.kt
**Paquete:** `com.example.huerto_hogar.viewmodel`

**Descripción:**  
ViewModel para gestión de productos en panel de administrador.

**Estados:**
```kotlin
val estado: StateFlow<ProductoUIState>
val isLoading: StateFlow<Boolean>
val operationResult: StateFlow<String?>
val selectedImageUri: StateFlow<Uri?>
val uploadingImage: StateFlow<Boolean>
```

**Métodos de actualización de campos:**
```kotlin
fun onIdProductoChange(valor: String)  // Convierte a mayúsculas
fun onNombreChange(valor: String)
fun onDescripcionChange(valor: String)
fun onPrecioChange(valor: String)
fun onStockChange(valor: String)
fun onLinkImagenChange(valor: String)
fun onOrigenChange(valor: String)
fun onCertificacionOrganicaChange(valor: Boolean)
fun onEstaActivoChange(valor: Boolean)
fun onIdCategoriaChange(valor: Int)
```

**Métodos de imágenes:**
```kotlin
fun onImageSelected(uri: Uri)
fun clearImage()
suspend fun uploadSelectedImage(): Boolean
```

**Validación y CRUD:**
```kotlin
fun validarFormulario(esEdicion: Boolean): Boolean
fun crearProducto(onSuccess: () -> Unit, onError: (String) -> Unit)
fun actualizarProducto(id: String, onSuccess: () -> Unit, onError: (String) -> Unit)
fun cargarProducto(producto: ProductoDto)
fun limpiarFormulario()
```

**Validaciones implementadas:**
- Nombre: no vacío, max 100 caracteres
- Descripción: no vacía, max 500 caracteres
- Precio: mayor a 0
- Stock: no negativo
- Link imagen: no vacío, max 255, debe empezar con http
- Origen: max 100 caracteres (opcional)
- Categoría: ID válido > 0

**Flujo de subida de imagen:**
1. `onImageSelected()` guarda URI local
2. `uploadSelectedImage()` valida y sube a S3
3. URL pública se guarda en `linkImagen`
4. Al crear/actualizar producto, se envía la URL

---

### UsuarioViewModel.kt
**Paquete:** `com.example.huerto_hogar.viewmodel`

**Descripción:**  
ViewModel para gestión de usuarios en panel de administrador.

**Estados:**
```kotlin
val estado: StateFlow<UsuarioUIState>
val isLoading: StateFlow<Boolean>
val operationResult: StateFlow<String?>
```

**Métodos de actualización:**
```kotlin
fun onNombreChange(valor: String)
fun onEmailChange(valor: String)
fun onPasswordChange(valor: String)
fun onDireccionChange(valor: String)
fun onTelefonoChange(valor: String)
fun onIdComunaChange(valor: Int)
fun onIdTipoUsuarioChange(valor: Int)
```

**CRUD:**
```kotlin
fun validarFormulario(esEdicion: Boolean): Boolean
fun crearUsuario(onSuccess: () -> Unit, onError: (String) -> Unit)
fun actualizarUsuario(id: String, onSuccess: () -> Unit, onError: (String) -> Unit)
fun cargarUsuario(usuario: Usuario)
fun limpiarFormulario()
```

**Validaciones:**
- Nombre: no vacío, min 3 caracteres
- Email: no vacío, debe contener @ y .
- Password: min 6 caracteres (opcional en edición)
- Teléfono: si se proporciona, debe ser > 0
- Tipo usuario: ID válido > 0

**Nota sobre password en edición:**
- Si está vacío, mantiene el password actual
- Si tiene valor, debe cumplir validación de 6 caracteres

---

### LoginViewModel.kt (ui.auth)
**Paquete:** `com.example.huerto_hogar.ui.auth`

**Descripción:**  
ViewModel específico para la pantalla de login.

**Estado:**
```kotlin
data class LoginUiState(
    val isLoading: Boolean,
    val loginSuccess: User?,
    val errorMessage: String?
)

val uiState: StateFlow<LoginUiState>
```

**Propiedades:**
```kotlin
var email: String = "admin@profesor.duoc.cl"  // Valor por defecto
var password: String = "Admin*123"            // Valor por defecto
```

**Métodos:**
```kotlin
fun login()
fun resetState()
```

**Flujo de login:**
1. `login()` llama a `UserRepository.getUserByEmail()`
2. Si existe, verifica password
3. Convierte `Usuario` API a `User` local
4. Mapea `idTipoUsuario` a `UserRole` enum
5. Guarda usuario en `LocalDataRepository.setCurrentUser()`
6. Actualiza `uiState` con éxito o error

**Manejo de errores por código HTTP:**
- 404: "Usuario no encontrado"
- 500: "Error en el servidor"
- Otros: Mensaje del backend

---

### ProductViewModel.kt (ui.store)
**Paquete:** `com.example.huerto_hogar.ui.store`

**Descripción:**  
ViewModel para la pantalla de tienda (lista de productos para clientes).

**Estado:**
```kotlin
data class ProductUiState(
    val products: List<Product>,
    val categories: List<Categoria>,
    val isLoading: Boolean,
    val error: String?
)
```

**Métodos:**
```kotlin
fun refreshProducts()
private fun loadProducts()
private fun ProductoDto.toProduct(): Product
```

**Categorías hardcodeadas:**
```kotlin
private val hardcodedCategories = listOf(
    Categoria(1, "Frutas", "Frutas frescas y de temporada"),
    Categoria(2, "Verduras", "Verduras frescas y orgánicas"),
    Categoria(3, "Carnes", "Carnes y pollo de calidad"),
    Categoria(4, "Lácteos", "Productos lácteos frescos"),
    Categoria(5, "Granos", "Granos y legumbres")
)
```

**Conversión ProductoDto → Product:**
- Precio: Int → Double
- Fecha: String ISO → Date()
- Agrega campo `priceUnit = "kg"`

---

## UI - Pantallas y Temas

### Color.kt
**Paquete:** `com.example.huerto_hogar.ui.theme`

**Descripción:**  
Paleta de colores de Material Design 3.

```kotlin
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)
```

---

### Theme.kt
**Paquete:** `com.example.huerto_hogar.ui.theme`

**Descripción:**  
Configuración del tema de Material Design 3.

```kotlin
@Composable
fun HuertoHogarTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
)
```

**Esquemas de color:**
- `DarkColorScheme`: Tonos 80 (más claros)
- `LightColorScheme`: Tonos 40 (más oscuros)
- `dynamicColor`: Usa colores del sistema en Android 12+

---

### Type.kt
**Paquete:** `com.example.huerto_hogar.ui.theme`

**Descripción:**  
Tipografía de Material Design 3.

```kotlin
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)
```

---

## Utilidades

### FormatUtils.kt
**Paquete:** `com.example.huerto_hogar.util`

**Descripción:**  
Utilidades para formateo de números y precios.

**Métodos:**

```kotlin
fun formatPrice(price: Double): String
// Ejemplo: 1200.0 → "$1.200"

fun formatNumber(number: Int): String
// Ejemplo: 1200 → "1.200"

fun formatPriceWithUnit(price: Double, unit: String): String
// Ejemplo: 1200.0, "kg" → "$1.200/kg"
```

**Locale:** Usa `Locale("es", "CL")` para formato chileno.

---

### LocalDataRepository.kt
**Paquete:** `com.example.huerto_hogar.data`

**Descripción:**  
Repositorio singleton simplificado que solo maneja la sesión del usuario actual.

```kotlin
object LocalDataRepository {
    val currentUser: StateFlow<User?>
    
    fun setCurrentUser(user: User?)
    fun logout()
}
```

**Importante:**
- ⚠️ NO hay base de datos Room
- ⚠️ El usuario se pierde al cerrar la app
- Solo mantiene estado en memoria durante la sesión

---

## 📊 Arquitectura General

```
┌─────────────────────────────────────────────┐
│              UI Layer (Compose)             │
│  ├─ MainActivity (Navigation)               │
│  ├─ LoginScreen, HomeScreen, etc.           │
│  └─ ViewModels (UI State)                   │
└─────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│          ViewModel Layer (MVVM)             │
│  ├─ LoginViewModel                          │
│  ├─ ProductoViewModel                       │
│  ├─ UsuarioViewModel                        │
│  ├─ CartViewModel                           │
│  └─ ProductViewModel (store)                │
└─────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│         Repository Layer (Abstraction)      │
│  ├─ UserRepository                          │
│  ├─ ProductRepository                       │
│  └─ LocalDataRepository (Session only)      │
└─────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│        Network Layer (Retrofit + API)       │
│  ├─ UserApiService (34.193.190.24:8081)    │
│  ├─ ProductApiService (34.202.46.121:8081) │
│  ├─ ImageUploadService (AWS S3)            │
│  └─ ApiResult (Sealed class)                │
└─────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│      Backend (Spring Boot + MongoDB)        │
│  ├─ Usuario Microservice (MongoDB Atlas)   │
│  ├─ Producto Microservice (MongoDB Atlas)  │
│  └─ AWS S3 (image-huerto bucket)           │
└─────────────────────────────────────────────┘
```

---

## ⚠️ Notas Importantes

### Seguridad
- **Passwords en texto plano:** No hay hashing de contraseñas (⚠️ solo para desarrollo)
- **APIs sin autenticación:** Los endpoints REST son públicos
- **S3 público:** Bucket configurado con acceso público para testing

### Persistencia
- **No hay base de datos local:** Room fue eliminado
- **Sesión volátil:** Usuario se pierde al cerrar la app
- **Carrito en memoria:** No persiste entre sesiones
- **Pedidos ficticios:** createOrderFromCart NO guarda en backend

### Migración MongoDB
- **Room → REST API:** Migración completada a MongoDB Atlas
- **Modelos legacy:** `User` y `Product` mantienen compatibilidad con UI antigua
- **DTOs de API:** `Usuario` y `ProductoDto` son los modelos reales del backend

### AWS
- **Microservicios en EC2:** IPs públicas (cambiarán si se reinician instancias)
- **MongoDB Atlas:** Base de datos en la nube
- **S3 imágenes:** Bucket `image-huerto` en región `us-east-1`

---

## 🔧 Configuración Requerida

### Variables de entorno (Backend)
- `MONGODB_URI`: Conexión a MongoDB Atlas
- `AWS_ACCESS_KEY_ID`: Credenciales AWS
- `AWS_SECRET_ACCESS_KEY`: Credenciales AWS
- `AWS_REGION`: us-east-1
- `S3_BUCKET_NAME`: image-huerto

### Dependencias Gradle
- Retrofit 2.9.0
- OkHttp 4.11.0
- Gson 2.10.1
- Jetpack Compose BOM 2024.02.00
- Material 3
- Navigation Compose
- Lifecycle ViewModels
- Coroutines

---

## 📚 Documentación Externa

- **Swagger Usuarios:** http://34.193.190.24:8081/swagger-ui/index.html#/
- **Swagger Productos:** http://34.202.46.121:8081/swagger-ui/index.html#/
- **MongoDB Atlas:** https://www.mongodb.com/cloud/atlas
- **AWS S3:** https://aws.amazon.com/s3/

---

**Fecha de generación:** 23 de noviembre de 2025  
**Versión de la app:** 1.0 (feature/migracion-mongodb-atlas)  
**Total de archivos documentados:** 45 archivos .kt
