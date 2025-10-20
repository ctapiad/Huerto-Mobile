# 🌱 Huerto Hogar - Aplicación de Tienda de Productos Orgánicos

Una aplicación móvil completa desarrollada en Android con Kotlin y Jetpack Compose para la venta de productos orgánicos frescos. La aplicación conecta a los consumidores con productores locales, promoviendo la agricultura sustentable y el consumo responsable.

## 📱 Características Principales

### 🛒 **Para Clientes**
- **Catálogo de productos**: Navegación por categorías (frutas, verduras, lácteos, productos orgánicos)
- **Carrito de compras persistente**: Mantiene productos agregados al navegar entre pantallas
- **Sistema de pedidos**: Creación y seguimiento de órdenes de compra
- **Perfil de usuario**: Gestión de información personal y direcciones
- **Historial de pedidos**: Visualización de compras anteriores con detalles

### 👨‍💼 **Para Administradores**
- **Dashboard administrativo**: Panel de control con métricas y reportes
- **Gestión de productos**: CRUD completo de productos con stock y precios
- **Gestión de usuarios**: Administración de cuentas de clientes y vendedores
- **Gestión de pedidos**: Seguimiento y actualización de estados de órdenes
- **Reportes**: Análisis de ventas y estadísticas del negocio

### 🔐 **Sistema de Autenticación**
- **Login seguro**: Autenticación con base de datos SQLite local
- **Roles de usuario**: Cliente, Vendedor, Administrador
- **Gestión de sesiones**: Persistencia de sesión entre usos de la app

## 🛠️ Tecnologías Utilizadas

### **Frontend**
- **Kotlin** - Lenguaje de programación principal
- **Jetpack Compose** - UI toolkit moderno declarativo
- **Material Design 3** - Sistema de diseño de Google

### **Arquitectura**
- **MVVM** (Model-View-ViewModel) - Patrón arquitectónico
- **Repository Pattern** - Capa de abstracción de datos
- **StateFlow** - Manejo reactivo del estado
- **Coroutines** - Programación asíncrona

### **Base de Datos**
- **Room Database** - Capa de abstracción sobre SQLite
- **SQLite** - Base de datos local persistente
- **Type Converters** - Conversión de tipos complejos

### **Navegación y UI**
- **Navigation Compose** - Navegación entre pantallas
- **ViewModel** - Gestión del estado de UI
- **LazyColumn/LazyRow** - Listas eficientes
- **Scaffold/Drawer** - Componentes de layout

## 📁 Estructura del Proyecto

```
app/src/main/java/com/example/huerto_hogar/
├── 📊 data/
│   ├── catalog/           # Catálogos (Categoría, Comuna, Región)
│   ├── dto/              # Data Transfer Objects
│   ├── enums/            # Enumeraciones (UserRole, OrderStatus)
│   ├── model/            # Modelos de dominio
│   └── LocalDataRepository.kt
├── 🗄️ database/
│   ├── converters/       # Convertidores de tipos para Room
│   ├── dao/              # Data Access Objects
│   ├── entities/         # Entidades de base de datos
│   ├── repository/       # DatabaseRepository
│   └── HuertoDatabase.kt
├── 🎨 ui/
│   ├── admin/            # Pantallas administrativas
│   ├── auth/             # Autenticación (Login)
│   ├── order/            # Gestión de pedidos
│   ├── store/            # Tienda (Home, Productos, Carrito)
│   ├── user/             # Perfil y pedidos del usuario
│   └── theme/            # Tema y estilos
├── 🧠 viewmodel/         # ViewModels (Auth, Cart)
├── MainActivity.kt       # Actividad principal
└── HuertoApplication.kt  # Clase Application
```

## 🚀 Instalación y Configuración

### **Prerrequisitos**
- Android Studio Hedgehog (2023.1.1) o superior
- JDK 11 o superior
- Android SDK 29-36
- Dispositivo Android 10+ o emulador

### **Pasos de Instalación**

1. **Clonar el repositorio**
   ```bash
   git clone https://github.com/ctapiad/Huerto-Mobile.git
   cd Huerto-Mobile
   ```

2. **Abrir en Android Studio**
   - Abrir Android Studio
   - File → Open → Seleccionar la carpeta del proyecto
   - Esperar a que se descarguen las dependencias

3. **Configurar el proyecto**
   ```bash
   # Limpiar y construir
   ./gradlew clean
   ./gradlew build
   ```

4. **Ejecutar la aplicación**
   - Conectar dispositivo Android o iniciar emulador
   - Hacer clic en "Run" o presionar Shift + F10

## 👥 Usuarios de Prueba

La aplicación incluye usuarios predefinidos para testing:

### **Administrador**
- **Email**: `admin@profesor.duoc.cl`
- **Contraseña**: `Admin*123`
- **Permisos**: Acceso completo al dashboard administrativo

### **Vendedor**
- **Email**: `vendedor@huertohogar.cl`
- **Contraseña**: `Vend*2025`
- **Permisos**: Gestión de productos y pedidos

### **Clientes**
- **Ana Gómez**: `ana.gomez@gmail.com` / `Ana$2025`
- **Luis Pérez**: `luis.perez@gmail.com` / `Luis_2025`
- **María Silva**: `maria.silva@gmail.com` / `Maria_123`

## 📊 Base de Datos

### **Tablas Principales**
- **users** - Información de usuarios y autenticación
- **products** - Catálogo de productos con precios y stock
- **categories** - Categorías de productos
- **orders** - Pedidos realizados por clientes
- **order_details** - Detalles de productos en cada pedido

### **Características de la BD**
- **Persistencia local** con SQLite
- **Relaciones normalizadas** entre tablas
- **Índices optimizados** para consultas frecuentes
- **Converters** para tipos complejos (Date, Enums)

## 🎯 Funcionalidades Destacadas

### **🛒 Carrito Persistente**
- Los productos permanecen en el carrito al navegar
- Actualización en tiempo real de cantidades
- Cálculo automático de totales y envío

### **📱 UI Responsiva**
- Diseño adaptativo a diferentes tamaños de pantalla
- Animaciones fluidas con Compose
- Componentes reutilizables y modulares

### **🔄 Estado Reactivo**
- StateFlow para actualizaciones automáticas de UI
- Sincronización en tiempo real entre pantallas
- Manejo eficiente de la memoria
