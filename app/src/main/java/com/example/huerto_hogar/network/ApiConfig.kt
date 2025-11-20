package com.example.huerto_hogar.network

/**
 * Configuración de URLs de los microservicios desplegados en AWS
 * 
 * Microservicios:
 * - Usuarios: http://34.193.190.24:8081/swagger-ui/index.html#/
 * - Productos: http://34.202.46.121:8081/swagger-ui/index.html#/
 * 
 * MongoDB Atlas: mongodb+srv://ctapiad_db_user:***@huerto.bi4rvwk.mongodb.net/
 */
object ApiConfig {
    // URLs de microservicios en AWS (IPs públicas - Nuevas instancias)
    const val USER_SERVICE_BASE_URL = "http://34.193.190.24:8081/"
    const val PRODUCT_SERVICE_BASE_URL = "http://34.202.46.121:8081/"
    
    // Para desarrollo local (comentado)
    // const val USER_SERVICE_BASE_URL = "http://10.0.2.2:8081/"  // Emulador
    // const val USER_SERVICE_BASE_URL = "http://192.168.X.X:8081/"  // Dispositivo físico
    
    const val TIMEOUT_SECONDS = 30L
}
