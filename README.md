# 🌱 Huerto Hogar - Aplicación Móvil

## 📋 Información del Proyecto

**Nombre**: Huerto Hogar  
**Plataforma**: Android  
**Lenguaje**: Kotlin  
**Framework UI**: Jetpack Compose  
**Equipo de Desarrollo**: bencastroo y ctapiad  
**Organización**: Duoc UC  
**Ubicación**: Viña del Mar, Valparaíso  

## 👥 Autores

- **bencastroo** - [@bencastroo](https://github.com/bencastroo)
- **ctapiad** - [@ctapiad](https://github.com/ctapiad)

**Repositorio**: [ctapiad/Huerto-Mobile](https://github.com/ctapiad/Huerto-Mobile)  
**Branch**: `feature/migracion-mongodb-atlas`

## 📱 Descripción

Huerto Hogar es una aplicación móvil Android para la gestión y venta de productos orgánicos y de huerto. La aplicación permite a los usuarios navegar por un catálogo de productos, gestionar carritos de compra, realizar pedidos y administrar usuarios.

### ✨ Características Principales

- 🛒 **Carrito de Compras**: Agregar, modificar y eliminar productos del carrito
- 📦 **Catálogo de Productos**: Navegación por categorías (Frutas, Verduras, Orgánicos)
- 👤 **Gestión de Usuarios**: Registro, login y administración de perfiles
- 📋 **Gestión de Pedidos**: Creación y seguimiento de órdenes
- 🔐 **Autenticación**: Sistema de login seguro con tokens JWT
- 📊 **Panel de Administración**: Gestión de productos y usuarios (solo administradores)
- 🎨 **Interfaz Moderna**: Diseño con Material Design 3 y Jetpack Compose

## 🏗️ Arquitectura

### Stack Tecnológico

- **Lenguaje**: Kotlin 1.9.0
- **SDK Mínimo**: Android 8.0 (API 26)
- **SDK Target**: Android 14 (API 34)
- **UI Framework**: Jetpack Compose
- **Navegación**: Compose Navigation
- **Gestión de Estado**: ViewModel + StateFlow
- **Red**: Retrofit 2.9.0 + OkHttp 4.12.0
- **Serialización**: Gson 2.10.1
- **Coroutines**: Kotlinx Coroutines 1.7.3

### Arquitectura de la Aplicación

```
app/
├── config/           # Configuración de la API
├── controller/       # ViewModels (CartViewModel, ProductsViewModel, etc.)
├── model/           # Modelos de datos (Product, User, Order, etc.)
├── repository/      # Repositorios para acceso a datos
├── service/         # Servicios de API (ProductService, AuthService, etc.)
└── view/            # Pantallas UI con Jetpack Compose
    ├── CartScreen.kt
    ├── ProductListScreen.kt
    ├── LoginScreen.kt
    ├── ProductManagementScreen.kt
    └── UserManagementScreen.kt
```

## 🔌 API Backend

### URL Base
```
http://ec2-3-16-149-246.us-east-2.compute.amazonaws.com:8080/api/productos
```

### Endpoints Principales

#### Productos
```
GET    /                         # Listar todos los productos
GET    /{id}                     # Obtener producto por ID
POST   /                         # Crear producto (Admin)
PUT    /{id}                     # Actualizar producto (Admin)
DELETE /{id}                     # Eliminar producto (Admin)
GET    /categoria/{categoriaId}  # Productos por categoría
```

#### Usuarios
```
POST   /usuarios/registro        # Registrar nuevo usuario
POST   /usuarios/login           # Iniciar sesión
GET    /usuarios                 # Listar usuarios (Admin)
PUT    /usuarios/{id}            # Actualizar usuario
DELETE /usuarios/{id}            # Eliminar usuario (Admin)
```

#### Órdenes
```
POST   /orders                   # Crear nueva orden
GET    /orders                   # Listar órdenes (Admin)
GET    /orders/user/{userId}     # Órdenes por usuario
PUT    /orders/{id}              # Actualizar estado de orden (Admin)
```

## 🚀 Instalación y Ejecución

### Requisitos Previos

- Android Studio Hedgehog | 2023.1.1 o superior
- JDK 17 o superior
- Gradle 8.13
- Dispositivo Android o Emulador (API 26+)

### Pasos de Instalación

1. **Clonar el repositorio**
```bash
git clone https://github.com/ctapiad/Huerto-Mobile.git
cd Huerto-Mobile
```

2. **Abrir en Android Studio**
   - Abrir Android Studio
   - Seleccionar "Open an Existing Project"
   - Navegar a la carpeta `Huerto-Mobile`

3. **Sincronizar Gradle**
   - Android Studio sincronizará automáticamente las dependencias
   - Esperar a que termine la sincronización

4. **Ejecutar la aplicación**
   - Conectar un dispositivo Android vía USB o iniciar un emulador
   - Hacer clic en el botón "Run" (▶️) o presionar `Shift + F10`

### Ejecución por Línea de Comandos

```bash
# Compilar la aplicación
./gradlew build

# Instalar en dispositivo conectado
./gradlew installDebug

# Ejecutar tests
./gradlew test
```

## 🧪 Testing

### Cobertura de Tests

La aplicación cuenta con **79 pruebas unitarias** con un **100% de éxito**:

- **CartViewModelTest** (11 tests): Lógica del carrito de compras
- **ProductsViewModelTest** (8 tests): Operaciones del catálogo
- **LoginViewModelTest** (4 tests): Autenticación
- **UsuarioViewModelTest** (15 tests): Gestión de usuarios
- **ProductoViewModelTest** (19 tests): Gestión de productos
- **AuthViewModelTest** (3 tests): Gestión de sesión
- **FormatUtilsTest** (6 tests): Formateo de precios
- **ApiResultTest** (6 tests): Respuestas de API
- **LocalDataRepositoryTest** (6 tests): Almacenamiento local
- **ExampleUnitTest** (1 test): Test de ejemplo

### Ejecutar Tests

```bash
# Ejecutar todos los tests
./gradlew test

# Ejecutar tests con reporte detallado
./gradlew test --info

# Ver reporte HTML
# Abrir: app/build/reports/tests/testDebugUnitTest/index.html
```

### Documentación de Testing

Para más detalles sobre los tests, consultar:
- [TESTING_COMPLETE_SUMMARY.md](TESTING_COMPLETE_SUMMARY.md) - Resumen completo de tests
- [TESTING_QUICKSTART.md](TESTING_QUICKSTART.md) - Guía rápida
- [GUIA_PRUEBAS.md](GUIA_PRUEBAS.md) - Guía de pruebas

## 🔐 Firma de la Aplicación

### Información del Keystore

- **Archivo**: `huerto-hogar-key.jks`
- **Alias**: `huerto-hogar`
- **Algoritmo**: RSA 2048 bits
- **Validez**: Hasta el 11 de abril de 2053

### Certificado Digital

```
CN=Huerto Hogar
OU=bencastroo y ctapiad
O=Duoc UC
L=Viña del Mar
ST=Valparaiso
C=CL
```

### Huellas del Certificado

```
SHA-256: 5E:63:87:52:09:F3:77:40:DC:18:12:AD:91:BC:99:3F:3C:12:A6:76:7A:53:A9:2E:C7:31:B1:62:B2:CA:DD:84
SHA-1:   8A:37:5E:E5:A2:A4:47:77:79:1B:F4:5C:1A:44:BB:27:7C:08:9D:34
MD5:     5B:0D:29:A1:9C:AB:04:C2:69:49:CC:6F:FA:93:E7:08
```

Para más información: [KEYSTORE_INFO.md](KEYSTORE_INFO.md)

### Generar APK Firmado

```bash
# Generar APK de release firmado
./gradlew assembleRelease

# El APK estará en:
# app/build/outputs/apk/release/app-release.apk
```

## 📦 APK de Producción

### Descargar APK

El APK firmado está disponible en:
```
app/build/outputs/apk/release/app-release.apk
```

### Instalación en Dispositivo

1. Transferir el APK al dispositivo Android
2. Habilitar "Fuentes desconocidas" en Configuración > Seguridad
3. Abrir el archivo APK y seguir las instrucciones de instalación

## 👤 Usuarios de Prueba

### Usuario Administrador
```
Email: admin@huertohogar.cl
Contraseña: admin123
```

### Usuario Regular
```
Email: usuario@test.cl
Contraseña: user123
```

## 🎨 Capturas de Pantalla

### Pantallas Principales

- **Login**: Autenticación de usuarios
- **Catálogo**: Navegación por productos con filtros de categoría
- **Detalle de Producto**: Información completa y agregar al carrito
- **Carrito**: Gestión de productos seleccionados
- **Pedidos**: Confirmación y seguimiento de órdenes
- **Administración**: Panel para gestión de productos y usuarios (solo admin)

### Características de UI

- ✨ Diseño Material Design 3
- 🎨 Tema verde corporativo (#4CAF50)
- 📱 Interfaz responsive
- 🔘 Botones y chips totalmente opacos para mejor visibilidad
- 💳 Formateo de precios chilenos (CLP)
- ✅ Validación de formularios en tiempo real

## 🔧 Configuración

### Variables de Entorno

La configuración de la API se encuentra en `ApiConfig.kt`:

```kotlin
object ApiConfig {
    const val BASE_URL = "http://ec2-3-16-149-246.us-east-2.compute.amazonaws.com:8080/"
}
```

### Configuración de Build

El proyecto usa Gradle Version Catalogs. Ver `gradle/libs.versions.toml` para dependencias.

## 🤝 Colaboración

### Evidencia de Trabajo Colaborativo

Este proyecto fue desarrollado de manera colaborativa entre **bencastroo** y **ctapiad**:

#### Commits Recientes
- `071cb15` - Implementación completa de tests unitarios (79 tests)
- Configuración de firma de aplicación
- Migración a MongoDB Atlas
- Implementación de UI con Jetpack Compose

#### Ramas
- `main` - Rama principal de producción
- `feature/migracion-mongodb-atlas` - Rama de desarrollo activa

### Proceso de Desarrollo

1. **Planificación**: Definición de arquitectura y división de tareas
2. **Desarrollo**: Implementación de features en ramas separadas
3. **Testing**: 79 pruebas unitarias con 100% de cobertura
4. **Code Review**: Revisión cruzada de código
5. **Integración**: Merge a rama principal
6. **Deploy**: Generación de APK firmado

## 📚 Documentación Adicional

- [API_DOCUMENTATION.md](API_DOCUMENTATION.md) - Documentación completa de la API
- [TESTING_COMPLETE_SUMMARY.md](TESTING_COMPLETE_SUMMARY.md) - Resumen de tests
- [KEYSTORE_INFO.md](KEYSTORE_INFO.md) - Información del keystore
- [MIGRATION_TO_MONGODB.md](../producto-huerto/MIGRATION_TO_MONGODB.md) - Migración a MongoDB

## 📄 Licencia

Este proyecto es parte de un trabajo académico para Duoc UC.

## 📞 Contacto

Para consultas o reportar problemas:

- **GitHub Issues**: [github.com/ctapiad/Huerto-Mobile/issues](https://github.com/ctapiad/Huerto-Mobile/issues)
- **Desarrolladores**: bencastroo, ctapiad
- **Organización**: Duoc UC, Viña del Mar, Valparaíso

---

**Desarrollado con ❤️ por bencastroo y ctapiad para Duoc UC**
