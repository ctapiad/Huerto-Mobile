package com.example.huerto_hogar.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Date
import java.util.concurrent.atomic.AtomicLong

object LocalDataRepository {

    // --- SECUENCIAS PARA IDs ---
    private val userSeq = AtomicLong(5) // Inicia después del último ID insertado
    private val productSeq = AtomicLong(1)
    private val orderSeq = AtomicLong(5) // Inicia después del último pedido de ejemplo (ID 5)

    private val _currentUser = MutableStateFlow<User?>(null) // El usuario actual. null si es invitado.
    val currentUser = _currentUser.asStateFlow()



    // Catálogos
    private val _tiposUsuario = MutableStateFlow(
        listOf(
            UserRole.ADMIN, UserRole.CLIENTE, UserRole.VENDEDOR
        )
    )
    private val _estadosPedido = MutableStateFlow(
        listOf(
            OrderStatus.PENDIENTE, OrderStatus.PREPARACION, OrderStatus.ENVIADO, OrderStatus.ENTREGADO, OrderStatus.CANCELADO
        )
    )

    // Datos principales
    private val _users = MutableStateFlow(
        listOf(
            User(id = 1, name = "Admin HuertoHogar", email = "admin@profesor.duoc.cl", password = "Admin*123", registrationDate = Date(), address = "Av. Alameda 100, Santiago", phone = 987654321, comunaId = 1, role = UserRole.ADMIN),
            User(id = 2, name = "Vendedor Principal", email = "vendedor@huertohogar.cl", password = "Vend*2025", registrationDate = Date(), address = "Los Copihues 456, San Bernardo", phone = 912345678, comunaId = 2, role = UserRole.VENDEDOR),
            User(id = 3, name = "Ana Gómez", email = "ana.gomez@gmail.com", password = "Ana$2025", registrationDate = Date(), address = "1 Norte 321, Viña del Mar", phone = 934567891, comunaId = 3, role = UserRole.CLIENTE),
            User(id = 4, name = "Luis Pérez", email = "luis.perez@gmail.com", password = "Luis_2025", registrationDate = Date(), address = "Caupolicán 777, Concepción", phone = 945678912, comunaId = 4, role = UserRole.CLIENTE),
            User(id = 5, name = "María Silva", email = "maria.silva@gmail.com", password = "Maria_123", registrationDate = Date(), address = "Av. Providencia 890, Santiago", phone = 987123456, comunaId = 1, role = UserRole.CLIENTE)
        )
    )

    // Categorías de productos
    private val _categories = MutableStateFlow(
        listOf(
            Categoria(id = 1, name = "Frutas Frescas", description = "Frutas de temporada, frescas y seleccionadas directamente desde nuestros huertos orgánicos."),
            Categoria(id = 2, name = "Verduras Orgánicas", description = "Verduras cultivadas sin pesticidas ni químicos, llenas de sabor y nutrientes naturales."),
            Categoria(id = 3, name = "Productos Orgánicos", description = "Productos procesados orgánicos como miel, quinua y otros superalimentos naturales."),
            Categoria(id = 4, name = "Productos Lácteos", description = "Lácteos frescos de granjas locales, libres de hormonas y antibióticos.")
        )
    )

    private val _products = MutableStateFlow(
        listOf(
            // Frutas Frescas (Categoría 1)
            Product(
                id = "FR001", name = "Manzanas Fuji",
                imageName = "manzana_funji",
                description = "Manzanas crujientes y dulces del Valle del Maule.",
                price = 1200.0, stock = 150, origin = "Valle del Maule",
                isOrganic = false, isActive = true, entryDate = Date(), categoryId = 1,
                priceUnit = "kg"
            ),
            Product(
                id = "FR002", name = "Naranjas Valencia",
                imageName = "naranjas_valencia",
                description = "Naranjas jugosas y dulces, perfectas para zumo o consumo directo.",
                price = 990.0, stock = 120, origin = "Región de Coquimbo",
                isOrganic = false, isActive = true, entryDate = Date(), categoryId = 1,
                priceUnit = "kg"
            ),
            Product(
                id = "FR003", name = "Plátanos Cavendish",
                imageName = "platanos_cavendish",
                description = "Plátanos maduros, ricos en potasio y energía natural.",
                price = 1490.0, stock = 80, origin = "Ecuador",
                isOrganic = false, isActive = true, entryDate = Date(), categoryId = 1,
                priceUnit = "kg"
            ),
            // Verduras Orgánicas (Categoría 2)
            Product(
                id = "VR001", name = "Zanahorias Orgánicas",
                imageName = "zanahorias_organicas",
                description = "Cultivadas sin pesticidas, crujientes y ricas en vitamina A.",
                price = 900.0, stock = 4, origin = "Región de O'Higgins",
                isOrganic = true, isActive = true, entryDate = Date(), categoryId = 2,
                priceUnit = "kg"
            ),
            Product(
                id = "VR002", name = "Espinacas Frescas",
                imageName = "espinacas_frescas",
                description = "Hojas tiernas de espinaca, ricas en hierro y vitaminas.",
                price = 1590.0, stock = 25, origin = "Región Metropolitana",
                isOrganic = true, isActive = true, entryDate = Date(), categoryId = 2,
                priceUnit = "kg"
            ),
            Product(
                id = "VR003", name = "Pimientos Tricolores",
                imageName = "pimientos_tricolores",
                description = "Mix de pimientos rojos, amarillos y verdes, dulces y crujientes.",
                price = 2490.0, stock = 30, origin = "Región de La Araucanía",
                isOrganic = true, isActive = true, entryDate = Date(), categoryId = 2,
                priceUnit = "kg"
            ),
            // Productos Orgánicos (Categoría 3)
            Product(
                id = "PO001", name = "Miel Orgánica",
                imageName = "miel_organica",
                description = "Miel pura de apicultores locales, rica en antioxidantes.",
                price = 5000.0, stock = 50, origin = null,
                isOrganic = true, isActive = true, entryDate = Date(), categoryId = 3,
                priceUnit = "kg"
            ),
            Product(
                id = "PO003", name = "Quinua Orgánica",
                imageName = "quinua_organica",
                description = "Superalimento andino, rica en proteínas y sin gluten.",
                price = 3990.0, stock = 40, origin = "Altiplano Boliviano",
                isOrganic = true, isActive = true, entryDate = Date(), categoryId = 3,
                priceUnit = "kg"
            ),
            // Productos Lácteos (Categoría 4)
            Product(
                id = "PL001", name = "Leche Entera",
                imageName = "leche",
                description = "Lácteos de granjas locales, frescos y de calidad.",
                price = 1200.0, stock = 200, origin = null,
                isOrganic = false, isActive = true, entryDate = Date(), categoryId = 4,
                priceUnit = "litro"
            )
        )
    )


    // Pedidos de ejemplo (algunos entregados para mostrar en reportes)
    private val _pedidos = MutableStateFlow(
        listOf(
            Pedido(
                id = 1,
                orderDate = Date(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000), // Hace 7 días
                deliveryDate = Date(System.currentTimeMillis() - 5 * 24 * 60 * 60 * 1000), // Hace 5 días
                total = 8500.0,
                deliveryAddress = "Av. Providencia 123, Santiago",
                userId = 3,
                status = OrderStatus.ENTREGADO
            ),
            Pedido(
                id = 2,
                orderDate = Date(System.currentTimeMillis() - 5 * 24 * 60 * 60 * 1000), // Hace 5 días
                deliveryDate = Date(System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000), // Hace 3 días
                total = 12300.0,
                deliveryAddress = "Av. Las Condes 456, Santiago",
                userId = 4,
                status = OrderStatus.ENTREGADO
            ),
            Pedido(
                id = 3,
                orderDate = Date(System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000), // Hace 3 días
                deliveryDate = Date(System.currentTimeMillis() - 1 * 24 * 60 * 60 * 1000), // Hace 1 día
                total = 6800.0,
                deliveryAddress = "Av. Ñuñoa 789, Santiago",
                userId = 5,
                status = OrderStatus.ENTREGADO
            ),
            Pedido(
                id = 4,
                orderDate = Date(System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000), // Hace 2 días
                deliveryDate = null,
                total = 15600.0,
                deliveryAddress = "Av. Maipú 321, Santiago",
                userId = 3,
                status = OrderStatus.PREPARACION
            ),
            Pedido(
                id = 5,
                orderDate = Date(System.currentTimeMillis() - 1 * 24 * 60 * 60 * 1000), // Hace 1 día
                deliveryDate = null,
                total = 11100.0,
                deliveryAddress = "Av. San Miguel 654, Santiago",
                userId = 4,
                status = OrderStatus.PENDIENTE
            )
        )
    )
    
    private val _detallesPedido = MutableStateFlow(
        listOf(
            // Detalles para pedido 1 (ENTREGADO)
            DetallePedido(pedidoId = 1, productId = "FR001", cantidad = 2, precioUnitario = 1200.0, subtotal = 2400.0),
            DetallePedido(pedidoId = 1, productId = "VE002", cantidad = 1, precioUnitario = 2300.0, subtotal = 2300.0),
            DetallePedido(pedidoId = 1, productId = "OR003", cantidad = 1, precioUnitario = 3800.0, subtotal = 3800.0),
            
            // Detalles para pedido 2 (ENTREGADO)
            DetallePedido(pedidoId = 2, productId = "VE001", cantidad = 3, precioUnitario = 1800.0, subtotal = 5400.0),
            DetallePedido(pedidoId = 2, productId = "LA004", cantidad = 2, precioUnitario = 3450.0, subtotal = 6900.0),
            
            // Detalles para pedido 3 (ENTREGADO)
            DetallePedido(pedidoId = 3, productId = "FR002", cantidad = 2, precioUnitario = 2800.0, subtotal = 5600.0),
            DetallePedido(pedidoId = 3, productId = "VE003", cantidad = 1, precioUnitario = 1200.0, subtotal = 1200.0),
            
            // Detalles para pedido 4 (PREPARACIÓN)
            DetallePedido(pedidoId = 4, productId = "FR003", cantidad = 3, precioUnitario = 2200.0, subtotal = 6600.0),
            DetallePedido(pedidoId = 4, productId = "VE001", cantidad = 5, precioUnitario = 1800.0, subtotal = 9000.0),
            
            // Detalles para pedido 5 (PENDIENTE)
            DetallePedido(pedidoId = 5, productId = "OR003", cantidad = 2, precioUnitario = 3800.0, subtotal = 7600.0),
            DetallePedido(pedidoId = 5, productId = "PL001", cantidad = 1, precioUnitario = 1200.0, subtotal = 1200.0),
            DetallePedido(pedidoId = 5, productId = "VE002", cantidad = 1, precioUnitario = 2300.0, subtotal = 2300.0)
        )
    )

    // Carrito de compras (específico de la sesión, no es una tabla)
    private val _shoppingCart = MutableStateFlow<MutableMap<String, CartItem>>(mutableMapOf())

    // --- FLUJOS PÚBLICOS (para la UI) ---
    val users = _users.asStateFlow()
    val products = _products.asStateFlow()
    val categories = _categories.asStateFlow()
    val pedidos = _pedidos.asStateFlow()
    val shoppingCart = _shoppingCart.asStateFlow()

    // --- LÓGICA DE NEGOCIO ---

    fun login(email: String, pass: String): User? {
        val user = _users.value.firstOrNull { it.email.equals(email, ignoreCase = true) && it.password == pass }
        // Al hacer login, actualizamos el usuario actual
        _currentUser.value = user
        return user
    }

    fun registerUser(name: String, email: String, pass: String, comunaId: Long): Result<User> {
        // Primero, revisamos si el email ya existe para evitar duplicados
        if (_users.value.any { it.email.equals(email, ignoreCase = true) }) {
            return Result.failure(Exception("El correo electrónico ya está en uso."))
        }

        // Si no existe, creamos el nuevo usuario con todos sus datos
        val newUser = User(
            id = userSeq.getAndIncrement(), // Genera un nuevo ID
            name = name,
            email = email,
            password = pass,
            registrationDate = Date(), // Usa la fecha y hora actual
            address = null, // La dirección se puede añadir después
            phone = null,   // El teléfono se puede añadir después
            comunaId = comunaId,
            role = UserRole.CLIENTE // Todo nuevo registro es un Cliente
        )

        // Añadimos el nuevo usuario a la lista
        _users.update { currentList -> currentList + newUser }

        // Después de registrarse, el nuevo usuario inicia sesión automáticamente
        _currentUser.value = newUser

        // Devolvemos el usuario recién creado
        return Result.success(newUser)
    }


    // --- CRUD DE PRODUCTOS (ADMIN) ---
    // Métodos CRUD movidos a la sección de administrador al final del archivo

    // --- CARRITO DE COMPRAS ---
    fun addToCart(product: Product, quantity: Int): Result<Unit> {
        if (product.stock < quantity) return Result.failure(Exception("Stock insuficiente."))
        _shoppingCart.update { cart ->
            val existingItem = cart[product.id]
            if (existingItem != null) {
                // Valida que la nueva cantidad no supere el stock
                if (product.stock < existingItem.quantity + quantity) {
                    throw Exception("No puedes agregar más de ${product.stock} unidades.")
                }
                existingItem.quantity += quantity
            } else {
                cart[product.id] = CartItem(product, quantity)
            }
            cart
        }
        return Result.success(Unit)
    }

    fun updateCartItemQuantity(productId: String, newQuantity: Int): Result<Unit> {
        val product = _products.value.find { it.id == productId } 
            ?: return Result.failure(Exception("Producto no encontrado"))
        
        if (newQuantity <= 0) {
            return removeFromCart(productId)
        }
        
        if (newQuantity > product.stock) {
            return Result.failure(Exception("Stock insuficiente. Disponible: ${product.stock}"))
        }
        
        _shoppingCart.update { cart ->
            cart[productId]?.let { 
                it.quantity = newQuantity
            }
            cart
        }
        return Result.success(Unit)
    }

    fun removeFromCart(productId: String): Result<Unit> {
        _shoppingCart.update { cart ->
            cart.remove(productId)
            cart
        }
        return Result.success(Unit)
    }

    // --- GESTIÓN DE PEDIDOS ---
    fun placeOrder(user: User): Result<Pedido> {
        val cart = _shoppingCart.value
        if (cart.isEmpty()) return Result.failure(Exception("El carrito está vacío."))
        if (user.address == null) return Result.failure(Exception("Debes configurar una dirección de entrega."))

        // 1. Validar stock de todos los productos
        cart.forEach { (_, item) ->
            val productInDb = _products.value.first { it.id == item.product.id }
            if (productInDb.stock < item.quantity) {
                return Result.failure(Exception("Stock no disponible para '${item.product.name}'."))
            }
        }

        // 2. Actualizar stock
        _products.update { currentProducts ->
            currentProducts.map { p ->
                cart[p.id]?.let { p.copy(stock = p.stock - it.quantity) } ?: p
            }
        }

        // 3. Crear el pedido y sus detalles
        val newOrderId = orderSeq.getAndIncrement()
        val total = cart.values.sumOf { it.product.price * it.quantity }
        val newOrder = Pedido(
            id = newOrderId,
            orderDate = Date(),
            deliveryDate = null,
            total = total,
            deliveryAddress = user.address!!,
            userId = user.id,
            status = OrderStatus.PENDIENTE
        )
        val newDetails = cart.values.map {
            DetallePedido(
                pedidoId = newOrderId,
                productId = it.product.id,
                cantidad = it.quantity,
                precioUnitario = it.product.price,
                subtotal = it.product.price * it.quantity
            )
        }

        _pedidos.update { it + newOrder }
        _detallesPedido.update { it + newDetails }

        // 4. Limpiar el carrito
        _shoppingCart.value.clear()

        return Result.success(newOrder)
    }

    // --- REPORTES (ADMIN) ---
    fun getCriticalStockProducts(): List<Product> {
        return _products.value.filter { it.isActive && it.stock <= 5 }
    }

    fun getSalesByDateRange(start: Date, end: Date): List<Pedido> {
        return _pedidos.value.filter { it.orderDate in start..end }
    }

    fun logout() {
        // Al cerrar sesión, simplemente ponemos el usuario actual en null
        _currentUser.value = null
        // Opcional: Limpiar el carrito al cerrar sesión
        clearCart()
    }

    // --- MÉTODOS ADICIONALES DEL CARRITO ---
    fun clearCart() {
        _shoppingCart.update { 
            it.clear()
            it 
        }
    }

    fun getCartItemCount(): Int {
        return _shoppingCart.value.values.sumOf { it.quantity }
    }

    fun getCartTotal(): Double {
        return _shoppingCart.value.values.sumOf { it.product.price * it.quantity }
    }

    fun getCartItems(): List<CartItem> {
        return _shoppingCart.value.values.toList()
    }

    // --- MÉTODOS CRUD PARA ADMINISTRADORES ---
    
    // CRUD USUARIOS
    fun createUser(user: User): User {
        val newId = userSeq.incrementAndGet()
        val newUser = user.copy(id = newId)
        _users.update { it + newUser }
        return newUser
    }

    fun updateUser(user: User) {
        _users.update { users ->
            users.map { if (it.id == user.id) user else it }
        }
    }

    fun deleteUser(userId: Long) {
        _users.update { users ->
            users.filter { it.id != userId }
        }
    }

    fun getUserById(userId: Long): User? {
        return _users.value.firstOrNull { it.id == userId }
    }

    // CRUD PRODUCTOS
    fun createProduct(product: Product): Product {
        val newId = "PR${productSeq.incrementAndGet().toString().padStart(3, '0')}"
        val newProduct = product.copy(id = newId)
        _products.update { it + newProduct }
        return newProduct
    }

    fun updateProduct(product: Product) {
        _products.update { products ->
            products.map { if (it.id == product.id) product else it }
        }
    }

    fun deleteProduct(productId: String) {
        _products.update { products ->
            products.filter { it.id != productId }
        }
    }

    fun getProductById(productId: String): Product? {
        return _products.value.firstOrNull { it.id == productId }
    }

    // Método para actualizar solo el stock de un producto
    fun updateProductStock(productId: String, newStock: Int) {
        _products.update { products ->
            products.map { product ->
                if (product.id == productId) {
                    product.copy(stock = newStock)
                } else {
                    product
                }
            }
        }
    }

    // Método para activar/desactivar producto
    fun toggleProductActive(productId: String) {
        _products.update { products ->
            products.map { product ->
                if (product.id == productId) {
                    product.copy(isActive = !product.isActive)
                } else {
                    product
                }
            }
        }
    }

    // ESTADÍSTICAS Y REPORTES ADICIONALES
    fun getTotalUsers(): Int = _users.value.size
    
    fun getActiveProducts(): List<Product> = _products.value.filter { it.isActive }
    
    fun getInactiveProducts(): List<Product> = _products.value.filter { !it.isActive }
    
    fun getOrganicProducts(): List<Product> = _products.value.filter { it.isOrganic }
    
    fun getUsersByRole(role: UserRole): List<User> = _users.value.filter { it.role == role }
    
    fun getTotalInventoryValue(): Double {
        return _products.value.sumOf { it.price * it.stock }
    }
    
    fun getProductsByStockRange(min: Int, max: Int): List<Product> {
        return _products.value.filter { it.stock in min..max }
    }

    // Simulación de productos más vendidos (basado en stock restante)
    fun getTopSellingProducts(limit: Int = 5): List<Product> {
        return _products.value
            .filter { it.isActive }
            .sortedBy { it.stock } // Menos stock = más vendido
            .take(limit)
    }

    // Estadísticas de pedidos
    fun getTotalSales(): Double = _pedidos.value.sumOf { it.total }
    
    fun getCompletedOrders(): List<Pedido> {
        return _pedidos.value.filter { it.status == OrderStatus.ENTREGADO }
    }
    
    fun getPendingOrders(): List<Pedido> {
        return _pedidos.value.filter { it.status == OrderStatus.PENDIENTE }
    }

    // Método para obtener reportes completos
    fun getAdminReports(): AdminReports {
        return AdminReports(
            totalUsers = getTotalUsers(),
            totalProducts = _products.value.size,
            activeProducts = getActiveProducts().size,
            totalOrders = _pedidos.value.size,
            completedOrders = getCompletedOrders().size,
            totalSales = getTotalSales(),
            inventoryValue = getTotalInventoryValue(),
            lowStockProducts = getCriticalStockProducts(),
            topProducts = getTopSellingProducts()
        )
    }

    // === GESTIÓN DE PEDIDOS ===

    /**
     * Crea un nuevo pedido basado en los productos del carrito actual
     * @param deliveryAddress Dirección de entrega del pedido
     * @return El ID del pedido creado o null si no hay usuario logueado
     */
    fun createOrderFromCart(deliveryAddress: String): Long? {
        val user = _currentUser.value ?: return null
        val cartItems = _shoppingCart.value.values.toList()
        
        if (cartItems.isEmpty()) return null

        // Calcular subtotal de productos
        val subtotal = cartItems.sumOf { cartItem ->
            cartItem.product.price * cartItem.quantity
        }
        
        // Calcular tarifa de envío (gratis si el subtotal es >= 50000)
        val deliveryFee = if (subtotal >= 50000) 0.0 else 3000.0
        
        // Calcular total incluyendo envío
        val total = subtotal + deliveryFee

        // Crear nuevo pedido
        val newOrderId = orderSeq.incrementAndGet()
        val newOrder = Pedido(
            id = newOrderId,
            orderDate = Date(),
            deliveryDate = null, // Se asignará cuando cambie el estado
            total = total,
            deliveryAddress = deliveryAddress,
            userId = user.id,
            status = OrderStatus.PENDIENTE
        )

        // Crear detalles del pedido
        val orderDetails = cartItems.map { cartItem ->
            DetallePedido(
                pedidoId = newOrderId,
                productId = cartItem.product.id,
                cantidad = cartItem.quantity,
                precioUnitario = cartItem.product.price,
                subtotal = cartItem.product.price * cartItem.quantity
            )
        }

        // Actualizar stock de productos
        _products.update { productList ->
            productList.map { product ->
                val cartItem = cartItems.find { it.product.id == product.id }
                if (cartItem != null) {
                    product.copy(stock = product.stock - cartItem.quantity)
                } else {
                    product
                }
            }
        }

        // Agregar pedido y detalles a las listas
        _pedidos.update { it + newOrder }
        _detallesPedido.update { it + orderDetails }

        // Limpiar carrito después de crear el pedido
        clearCart()

        return newOrderId
    }

    /**
     * Obtiene todos los pedidos del usuario actual
     */
    fun getCurrentUserOrders(): List<Pedido> {
        val currentUserId = _currentUser.value?.id ?: return emptyList()
        return _pedidos.value
            .filter { it.userId == currentUserId }
            .sortedByDescending { it.orderDate }
    }

    /**
     * Obtiene todos los pedidos (para administradores)
     */
    fun getAllOrders(): List<Pedido> {
        return _pedidos.value.sortedByDescending { it.orderDate }
    }

    /**
     * Obtiene los detalles de un pedido específico
     */
    fun getOrderDetails(orderId: Long): List<DetallePedido> {
        return _detallesPedido.value.filter { it.pedidoId == orderId }
    }

    /**
     * Obtiene un pedido específico por su ID
     */
    fun getOrderById(orderId: Long): Pedido? {
        return _pedidos.value.find { it.id == orderId }
    }

    /**
     * Calcula el costo de envío para un pedido específico
     */
    fun getOrderDeliveryFee(orderId: Long): Double {
        val orderDetails = getOrderDetails(orderId)
        val subtotal = orderDetails.sumOf { it.subtotal }
        return if (subtotal >= 50000) 0.0 else 3000.0
    }

    /**
     * Actualiza el estado de un pedido (para administradores)
     */
    fun updateOrderStatus(orderId: Long, newStatus: OrderStatus): Boolean {
        val order = _pedidos.value.find { it.id == orderId } ?: return false
        
        val updatedOrder = when (newStatus) {
            OrderStatus.ENTREGADO -> order.copy(
                status = newStatus,
                deliveryDate = Date()
            )
            else -> order.copy(status = newStatus)
        }

        _pedidos.update { orderList ->
            orderList.map { if (it.id == orderId) updatedOrder else it }
        }
        return true
    }

    /**
     * Obtiene pedidos filtrados por estado
     */
    fun getOrdersByStatus(status: OrderStatus): List<Pedido> {
        return _pedidos.value.filter { it.status == status }
    }

    /**
     * Obtiene estadísticas de pedidos en tiempo real
     */
    fun getOrderStatistics(): Map<String, Int> {
        val orders = _pedidos.value
        return mapOf(
            "total" to orders.size,
            "pendiente" to orders.count { it.status == OrderStatus.PENDIENTE },
            "preparacion" to orders.count { it.status == OrderStatus.PREPARACION },
            "enviado" to orders.count { it.status == OrderStatus.ENVIADO },
            "entregado" to orders.count { it.status == OrderStatus.ENTREGADO },
            "cancelado" to orders.count { it.status == OrderStatus.CANCELADO }
        )
    }

    /**
     * Función para limpiar datos de ejemplo (útil para testing)
     */
    fun clearExampleData() {
        _pedidos.value = emptyList()
        _detallesPedido.value = emptyList()
        orderSeq.set(0) // Reinicia la secuencia
    }

    /**
     * Función de debug para verificar pedidos y sus detalles
     */
    fun debugOrdersInfo(): String {
        val orders = _pedidos.value
        val details = _detallesPedido.value
        
        return buildString {
            appendLine("=== DEBUG: PEDIDOS ===")
            orders.forEach { order ->
                appendLine("Pedido ID: ${order.id}, Total: ${order.total}, Usuario: ${order.userId}")
                val orderDetails = details.filter { it.pedidoId == order.id }
                orderDetails.forEach { detail ->
                    appendLine("  - Producto: ${detail.productId}, Cantidad: ${detail.cantidad}, Subtotal: ${detail.subtotal}")
                }
                if (orderDetails.isEmpty()) {
                    appendLine("  - SIN DETALLES")
                }
                appendLine()
            }
            appendLine("OrderSeq actual: ${orderSeq.get()}")
            appendLine("Total pedidos: ${orders.size}")
            appendLine("Total detalles: ${details.size}")
        }
    }
}
