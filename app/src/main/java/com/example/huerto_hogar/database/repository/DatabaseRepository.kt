package com.example.huerto_hogar.database.repository

import android.content.Context
import com.example.huerto_hogar.data.enums.*
import com.example.huerto_hogar.data.model.*
import com.example.huerto_hogar.data.catalog.*
import com.example.huerto_hogar.database.HuertoDatabase
import com.example.huerto_hogar.database.entities.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date

class DatabaseRepository(context: Context) {
    
    private val database = HuertoDatabase.getDatabase(context)
    private val userDao = database.userDao()
    private val productDao = database.productDao()
    private val categoryDao = database.categoryDao()
    private val orderDao = database.orderDao()
    private val orderDetailDao = database.orderDetailDao()
    
    // Método para poblar la base de datos con datos del LocalDataRepository
    suspend fun initializeWithLocalData() {
        // Solo poblar si la base de datos está vacía
        val existingUsers = userDao.getAllUsers()
        // Para verificar si está vacía, necesitamos convertir el Flow
        // Por simplicidad, insertamos siempre (Room manejará duplicados si usamos REPLACE)
        
        insertInitialCategories()
        insertInitialUsers()
        insertInitialProducts()
        insertInitialOrdersAndDetails()
    }
    
    private suspend fun insertInitialCategories() {
        val categories = listOf(
            Categoria(id = 1, name = "Frutas Frescas", description = "Frutas de temporada, frescas y seleccionadas directamente desde nuestros huertos orgánicos."),
            Categoria(id = 2, name = "Verduras Orgánicas", description = "Verduras cultivadas sin pesticidas ni químicos, llenas de sabor y nutrientes naturales."),
            Categoria(id = 3, name = "Productos Orgánicos", description = "Productos procesados orgánicos como miel, quinua y otros superalimentos naturales."),
            Categoria(id = 4, name = "Productos Lácteos", description = "Lácteos frescos de granjas locales, libres de hormonas y antibióticos.")
        )
        
        categories.forEach { category ->
            categoryDao.insertCategory(category.toCategoryEntity())
        }
    }
    
    private suspend fun insertInitialUsers() {
        val users = listOf(
            User(id = 1, name = "Admin HuertoHogar", email = "admin@profesor.duoc.cl", password = "Admin*123", registrationDate = Date(), address = "Av. Alameda 100, Santiago", phone = 987654321, comunaId = 1, role = UserRole.ADMIN),
            User(id = 2, name = "Vendedor Principal", email = "vendedor@huertohogar.cl", password = "Vend*2025", registrationDate = Date(), address = "Los Copihues 456, San Bernardo", phone = 912345678, comunaId = 2, role = UserRole.VENDEDOR),
            User(id = 3, name = "Ana Gómez", email = "ana.gomez@gmail.com", password = "Ana$2025", registrationDate = Date(), address = "1 Norte 321, Viña del Mar", phone = 934567891, comunaId = 3, role = UserRole.CLIENTE),
            User(id = 4, name = "Luis Pérez", email = "luis.perez@gmail.com", password = "Luis_2025", registrationDate = Date(), address = "Caupolicán 777, Concepción", phone = 945678912, comunaId = 4, role = UserRole.CLIENTE),
            User(id = 5, name = "María Silva", email = "maria.silva@gmail.com", password = "Maria_123", registrationDate = Date(), address = "Av. Providencia 890, Santiago", phone = 987123456, comunaId = 1, role = UserRole.CLIENTE)
        )
        
        users.forEach { user ->
            userDao.insertUser(user.toUserEntity())
        }
    }
    
    private suspend fun insertInitialProducts() {
        val products = listOf(
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
        
        products.forEach { product ->
            productDao.insertProduct(product.toProductEntity())
        }
    }
    
    private suspend fun insertInitialOrdersAndDetails() {
        // Pedidos de ejemplo
        val pedidos = listOf(
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
        
        // Detalles de pedidos
        val detallesPedido = listOf(
            // Detalles para pedido 1 (ENTREGADO)
            DetallePedido(pedidoId = 1, productId = "FR001", cantidad = 2, precioUnitario = 1200.0, subtotal = 2400.0),
            DetallePedido(pedidoId = 1, productId = "VR002", cantidad = 1, precioUnitario = 1590.0, subtotal = 1590.0),
            DetallePedido(pedidoId = 1, productId = "PO001", cantidad = 1, precioUnitario = 5000.0, subtotal = 5000.0),
            
            // Detalles para pedido 2 (ENTREGADO)
            DetallePedido(pedidoId = 2, productId = "VR001", cantidad = 3, precioUnitario = 900.0, subtotal = 2700.0),
            DetallePedido(pedidoId = 2, productId = "PL001", cantidad = 2, precioUnitario = 1200.0, subtotal = 2400.0),
            
            // Detalles para pedido 3 (ENTREGADO)
            DetallePedido(pedidoId = 3, productId = "FR002", cantidad = 2, precioUnitario = 990.0, subtotal = 1980.0),
            DetallePedido(pedidoId = 3, productId = "VR003", cantidad = 1, precioUnitario = 2490.0, subtotal = 2490.0),
            
            // Detalles para pedido 4 (PREPARACIÓN)
            DetallePedido(pedidoId = 4, productId = "FR003", cantidad = 3, precioUnitario = 1490.0, subtotal = 4470.0),
            DetallePedido(pedidoId = 4, productId = "VR001", cantidad = 5, precioUnitario = 900.0, subtotal = 4500.0),
            
            // Detalles para pedido 5 (PENDIENTE)
            DetallePedido(pedidoId = 5, productId = "PO003", cantidad = 2, precioUnitario = 3990.0, subtotal = 7980.0),
            DetallePedido(pedidoId = 5, productId = "PL001", cantidad = 1, precioUnitario = 1200.0, subtotal = 1200.0),
            DetallePedido(pedidoId = 5, productId = "VR002", cantidad = 1, precioUnitario = 1590.0, subtotal = 1590.0)
        )
        
        // Insertar pedidos
        pedidos.forEach { pedido ->
            orderDao.insertOrder(pedido.toOrderEntity())
        }
        
        // Insertar detalles
        detallesPedido.forEach { detalle ->
            orderDetailDao.insertOrderDetail(detalle.toOrderDetailEntity())
        }
    }
    
    // === FUNCIONES DE CONVERSIÓN ===
    
    // User conversions
    fun User.toUserEntity(): UserEntity = UserEntity(
        id = id,
        name = name,
        email = email,
        password = password,
        registrationDate = registrationDate,
        address = address,
        phone = phone?.toLong(),
        comunaId = comunaId,
        role = role
    )
    
    fun UserEntity.toUser(): User = User(
        id = id,
        name = name,
        email = email,
        password = password,
        registrationDate = registrationDate,
        address = address,
        phone = phone?.toInt(),
        comunaId = comunaId,
        role = role
    )
    
    // Product conversions
    fun Product.toProductEntity(): ProductEntity = ProductEntity(
        id = id,
        name = name,
        imageName = imageName ?: "",
        description = description ?: "",
        price = price,
        stock = stock,
        origin = origin,
        isOrganic = isOrganic,
        isActive = isActive,
        entryDate = entryDate,
        categoryId = categoryId,
        priceUnit = priceUnit
    )
    
    fun ProductEntity.toProduct(): Product = Product(
        id = id,
        name = name,
        imageName = if (imageName.isEmpty()) null else imageName,
        description = description ?: "",
        price = price,
        stock = stock,
        origin = origin,
        isOrganic = isOrganic,
        isActive = isActive,
        entryDate = entryDate,
        categoryId = categoryId,
        priceUnit = priceUnit
    )
    
    // Category conversions
    fun Categoria.toCategoryEntity(): CategoryEntity = CategoryEntity(
        id = id,
        name = name,
        description = description ?: ""
    )
    
    fun CategoryEntity.toCategoria(): Categoria = Categoria(
        id = id,
        name = name,
        description = description
    )
    
    // Order conversions
    fun Pedido.toOrderEntity(): OrderEntity = OrderEntity(
        id = id,
        orderDate = orderDate,
        deliveryDate = deliveryDate,
        total = total,
        deliveryAddress = deliveryAddress,
        userId = userId,
        status = status
    )
    
    fun OrderEntity.toPedido(): Pedido = Pedido(
        id = id,
        orderDate = orderDate,
        deliveryDate = deliveryDate,
        total = total,
        deliveryAddress = deliveryAddress,
        userId = userId,
        status = status
    )
    
    // OrderDetail conversions
    fun DetallePedido.toOrderDetailEntity(): OrderDetailEntity = OrderDetailEntity(
        orderId = pedidoId,
        productId = productId,
        quantity = cantidad,
        unitPrice = precioUnitario,
        subtotal = subtotal
    )
    
    fun OrderDetailEntity.toDetallePedido(): DetallePedido = DetallePedido(
        pedidoId = orderId,
        productId = productId,
        cantidad = quantity,
        precioUnitario = unitPrice,
        subtotal = subtotal
    )
    
    // === MÉTODOS DE ACCESO A DATOS ===
    
    // Métodos de Usuario
    suspend fun loginUser(email: String, password: String): User? {
        return userDao.getUserByEmailAndPassword(email, password)?.toUser()
    }
    
    suspend fun registerUser(user: User): Result<User> {
        return try {
            // Verificar si el email ya existe
            val existingUser = userDao.getUserByEmail(user.email)
            if (existingUser != null) {
                Result.failure(Exception("El correo electrónico ya está en uso."))
            } else {
                val newUserEntity = user.toUserEntity()
                val newId = userDao.insertUser(newUserEntity)
                val newUser = user.copy(id = newId)
                Result.success(newUser)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun getAllUsers(): Flow<List<User>> {
        return userDao.getAllUsers().map { entities ->
            entities.map { it.toUser() }
        }
    }
    
    suspend fun getUserById(userId: Long): User? {
        return userDao.getUserById(userId)?.toUser()
    }
    
    suspend fun updateUser(user: User) {
        userDao.updateUser(user.toUserEntity())
    }
    
    suspend fun deleteUser(userId: Long) {
        userDao.deleteUserById(userId)
    }
    
    // Métodos de Producto
    fun getAllProducts(): Flow<List<Product>> {
        return productDao.getAllProducts().map { entities ->
            entities.map { it.toProduct() }
        }
    }
    
    fun getActiveProducts(): Flow<List<Product>> {
        return productDao.getActiveProducts().map { entities ->
            entities.map { it.toProduct() }
        }
    }
    
    suspend fun getProductById(productId: String): Product? {
        return productDao.getProductById(productId)?.toProduct()
    }
    
    suspend fun insertProduct(product: Product) {
        productDao.insertProduct(product.toProductEntity())
    }
    
    suspend fun updateProduct(product: Product) {
        productDao.updateProduct(product.toProductEntity())
    }
    
    suspend fun deleteProduct(productId: String) {
        productDao.deleteProductById(productId)
    }
    
    suspend fun updateProductStock(productId: String, newStock: Int) {
        productDao.updateProductStock(productId, newStock)
    }
    
    fun getLowStockProducts(): Flow<List<Product>> {
        return productDao.getLowStockProducts().map { entities ->
            entities.map { it.toProduct() }
        }
    }
    
    // Métodos de Categoría
    fun getAllCategories(): Flow<List<Categoria>> {
        return categoryDao.getAllCategories().map { entities ->
            entities.map { it.toCategoria() }
        }
    }
    
    suspend fun insertCategory(category: Categoria): Long {
        return categoryDao.insertCategory(category.toCategoryEntity())
    }
    
    // Métodos de Pedidos
    suspend fun insertOrder(order: Pedido): Long {
        return orderDao.insertOrder(order.toOrderEntity())
    }
    
    fun getAllOrders(): Flow<List<Pedido>> {
        return orderDao.getAllOrders().map { entities ->
            entities.map { it.toPedido() }
        }
    }
    
    fun getOrdersByUser(userId: Long): Flow<List<Pedido>> {
        return orderDao.getOrdersByUser(userId).map { entities ->
            entities.map { it.toPedido() }
        }
    }
    
    suspend fun getOrderById(orderId: Long): Pedido? {
        return orderDao.getOrderById(orderId)?.toPedido()
    }
    
    suspend fun updateOrderStatus(orderId: Long, newStatus: OrderStatus): Boolean {
        return try {
            val order = orderDao.getOrderById(orderId)
            if (order != null) {
                val updatedOrder = order.copy(status = newStatus)
                orderDao.updateOrder(updatedOrder)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
    
    // Métodos de Detalles de Pedido
    suspend fun insertOrderDetail(orderDetail: DetallePedido) {
        orderDetailDao.insertOrderDetail(orderDetail.toOrderDetailEntity())
    }
    
    suspend fun insertOrderDetails(orderDetails: List<DetallePedido>) {
        orderDetailDao.insertOrderDetails(orderDetails.map { it.toOrderDetailEntity() })
    }
    
    suspend fun getOrderDetails(orderId: Long): List<DetallePedido> {
        return orderDetailDao.getDetailsByOrderId(orderId).map { it.toDetallePedido() }
    }
    
    // Método completo para crear pedido con detalles
    suspend fun createOrderWithDetails(order: Pedido, orderDetails: List<DetallePedido>): Long {
        return try {
            // Insertar pedido
            val orderId = insertOrder(order)
            
            // Insertar detalles con el ID del pedido
            val detailsWithOrderId = orderDetails.map { detail ->
                detail.copy(pedidoId = orderId)
            }
            insertOrderDetails(detailsWithOrderId)
            
            orderId
        } catch (e: Exception) {
            throw e
        }
    }
}