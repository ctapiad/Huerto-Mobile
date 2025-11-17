# Documentación de APIs - Huerto Hogar

## � Microservicio de Usuarios (Puerto 8081)

### ✅ ESTADO: Documentación OpenAPI disponible

**Base URL:** `http://localhost:8081/`  
**Documentación:** `http://localhost:8081/api-docs`

### Endpoints Implementados:

#### 🔍 Consultas
- `GET /api/usuarios` - Obtener todos los usuarios
- `GET /api/usuarios/{id}` - Obtener usuario por ID
- `GET /api/usuarios/{id}/dto` - Obtener DTO de usuario (sin password)
- `GET /api/usuarios/email/{email}` - Obtener usuario por email
- `GET /api/usuarios/buscar/{nombre}` - Buscar usuarios por nombre
- `GET /api/usuarios/tipo/{idTipoUsuario}` - Obtener usuarios por tipo

#### ✏️ Modificación
- `POST /api/usuarios` - Crear nuevo usuario
- `PUT /api/usuarios` - Modificar usuario existente
- `DELETE /api/usuarios/{id}` - Eliminar usuario por ID

### Modelo de Datos:

```json
{
  "id": "507f1f77bcf86cd799439011",
  "nombre": "Juan Pérez",
  "email": "juan@example.com",
  "password": "hashed_password",
  "fechaRegistro": "2025-11-12T20:00:00",
  "direccion": "Santiago, Chile",
  "telefono": 912345678,
  "idComuna": 1,
  "idTipoUsuario": 2
}
```

**UsuarioDto** (sin password):
```json
{
  "id": "507f1f77bcf86cd799439011",
  "nombre": "Juan Pérez",
  "email": "juan@example.com",
  "fechaRegistro": "2025-11-12T20:00:00",
  "direccion": "Santiago, Chile",
  "telefono": 912345678,
  "idComuna": 1,
  "idTipoUsuario": 2
}
```

---

## �📦 Microservicio de Productos (Puerto 8082)

### ✅ ESTADO: Documentación OpenAPI disponible

**Base URL:** `http://localhost:8082/`  
**Documentación:** `http://localhost:8082/v3/api-docs`

### Endpoints Implementados:

#### 🔍 Consultas
- `GET /api/productos` - Obtener todos los productos
- `GET /api/productos/activos` - Productos activos
- `GET /api/productos/disponibles` - Productos activos con stock
- `GET /api/productos/organicos` - Productos orgánicos
- `GET /api/productos/stock-bajo?stockMinimo={min}` - Stock bajo
- `GET /api/productos/categoria/{idCategoria}` - Por categoría
- `GET /api/productos/precio?precioMin={min}&precioMax={max}` - Por rango de precio
- `GET /api/productos/buscar?nombre={nombre}` - Búsqueda por nombre
- `GET /api/productos/{id}` - Por ID
- `GET /api/productos/health` - Health check

#### ✏️ Modificación
- `POST /api/productos` - Crear producto
- `PUT /api/productos/{id}` - Actualizar producto
- `PATCH /api/productos/{id}/stock?stock={cantidad}` - Actualizar stock
- `PATCH /api/productos/{id}/desactivar` - Desactivar producto
- `DELETE /api/productos/{id}` - Eliminar producto

### Modelo de Datos:

```json
{
  "idProducto": "PR001",
  "nombre": "Tomate Cherry",
  "linkImagen": "https://...",
  "descripcion": "Tomates frescos",
  "precio": 2500,
  "stock": 100,
  "origen": "Chile",
  "certificacionOrganica": true,
  "estaActivo": true,
  "fechaIngreso": "2025-11-12T20:00:00",
  "idCategoria": 1
}
```

---

## 👤 Microservicio de Usuarios (Puerto 8081)

### ⚠️ ESTADO: Sin documentación Swagger

**Base URL:** `http://localhost:8081/`  
**Documentación:** No disponible (404 en /swagger-ui y /v3/api-docs)

### 🔧 ACCIÓN REQUERIDA:

Debes verificar manualmente qué endpoints están disponibles. Opciones:

#### 1. **Revisar el código del microservicio**
```bash
# Busca los controladores en tu proyecto Spring Boot
grep -r "@GetMapping\|@PostMapping\|@PutMapping" src/main/java/
```

#### 2. **Pruebas manuales con curl**
```bash
# Probar login
curl -X POST http://localhost:8081/api/users/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com","password":"123456"}'

# Probar registro
curl -X POST http://localhost:8081/api/users/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Test","email":"test@test.com","password":"123456"}'

# Listar usuarios (si existe)
curl http://localhost:8081/api/users
```

#### 3. **Endpoints estimados** (basados en patrones comunes):

```
POST   /api/users/login        - Login de usuario
POST   /api/users/register     - Registro de nuevo usuario
GET    /api/users              - Listar todos los usuarios
GET    /api/users/{id}         - Obtener usuario por ID
PUT    /api/users/{id}         - Actualizar usuario
DELETE /api/users/{id}         - Eliminar usuario
```

### Modelo de Datos Estimado:

```json
{
  "id": "507f1f77bcf86cd799439011",
  "name": "Juan Pérez",
  "email": "juan@example.com",
  "phone": "+56912345678",
  "address": "Santiago, Chile",
  "createdAt": "2025-11-12T20:00:00"
}
```

---

## 🚀 Próximos Pasos

### 1. **Configurar IP para dispositivo físico:**
```bash
# Obtén tu IP en Mac
ifconfig | grep "inet " | grep -v 127.0.0.1

# Actualiza ApiConfig.kt con tu IP real
# Ejemplo: const val USER_SERVICE_BASE_URL = "http://192.168.1.100:8081/"
```

### 2. **Probar la conexión desde la app:**
```kotlin
// En tu ViewModel, prueba:
viewModelScope.launch {
    // Test productos
    val productsResult = ProductRepository().healthCheck()
    Log.d("API", "Products API: $productsResult")
    
    // Test usuarios
    val usersResult = UserRepository().getAllUsers()
    Log.d("API", "Users API: $usersResult")
}
```

### 3. **Implementar Login:**
El servicio NO tiene endpoint dedicado de login, pero puedes usar:
```kotlin
// Login usando getUserByEmail + validación de password
val result = UserRepository().login("user@example.com", "password123")
```

### 4. **Integrar con ViewModels existentes:**
- Actualizar `LoginViewModel` para usar `UserRepository().login()`
- Actualizar `ProductViewModel` para usar `ProductRepository().getAllProducts()`
- Agregar manejo de estados: Loading, Success, Error

---

## 📝 Notas Importantes

### Configuración de Red
- **Emulador Android:** Usa `10.0.2.2` (ya configurado en `ApiConfig.kt`)
- **Dispositivo físico:** Cambia a la IP de tu Mac en la misma red WiFi
- **CORS:** Los microservicios deben permitir peticiones desde cualquier origen

### Autenticación
- El API NO usa JWT tokens
- Password se envía en texto plano (considera encriptar en producción)
- Usa `UsuarioDto` para mostrar datos sin exponer password

### Base de Datos
- MongoDB Atlas: mongodb+srv://ctapiad_db_user:...@huerto.bi4rvwk.mongodb.net/
- Database: "Huerto"
- Colecciones: usuarios, productos

### Tipos de Usuario (idTipoUsuario)
Verifica en tu base de datos qué IDs corresponden a:
- 1 = Admin?
- 2 = Cliente?
- 3 = Vendedor?

---

## 🧪 Testing de APIs

### Test con Postman/Thunder Client:

**Crear Usuario:**
```bash
POST http://localhost:8081/api/usuarios
Content-Type: application/json

{
  "nombre": "Test User",
  "email": "test@test.com",
  "password": "123456",
  "telefono": 912345678,
  "direccion": "Santiago",
  "idTipoUsuario": 2
}
```

**Login (obtener por email):**
```bash
GET http://localhost:8081/api/usuarios/email/test@test.com
```

**Listar Productos Disponibles:**
```bash
GET http://localhost:8082/api/productos/disponibles
```

---

## ⚠️ Consideraciones de Seguridad

1. **Password en texto plano:** Considera usar bcrypt en el backend
2. **Validación de JWT:** Implementa tokens si necesitas sesiones
3. **HTTPS:** En producción, usa conexiones seguras
4. **Validación de inputs:** Sanitiza datos antes de enviar al servidor
