package com.example.huerto_hogar.network

import android.util.Log
import com.example.huerto_hogar.network.repository.ProductRepository
import com.example.huerto_hogar.network.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Helper para probar la conexión con los microservicios
 * 
 * USO:
 * NetworkTestHelper.testConnection { success, message ->
 *     Log.d("NetworkTest", "Success: $success, Message: $message")
 * }
 */
object NetworkTestHelper {
    
    private const val TAG = "NetworkTest"
    
    fun testConnection(callback: (success: Boolean, message: String) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val results = mutableListOf<String>()
            var allSuccess = true
            
            // Test 1: Health check de productos (si existe)
            Log.d(TAG, "Testing Products API...")
            try {
                when (val result = ProductRepository().getAvailableProducts()) {
                    is ApiResult.Success -> {
                        val count = result.data.size
                        if (count == 0) {
                            results.add("⚠️ Products API: Conectado pero sin productos (BD vacía)")
                        } else {
                            results.add("✅ Products API: $count productos disponibles")
                        }
                        Log.d(TAG, "Products API OK: $count items")
                    }
                    is ApiResult.Error -> {
                        results.add("❌ Products API: ${result.message}")
                        Log.e(TAG, "Products API Error: ${result.message}")
                        allSuccess = false
                    }
                    else -> {
                        results.add("⏳ Products API: Loading...")
                    }
                }
            } catch (e: Exception) {
                results.add("❌ Products API: ${e.message}")
                Log.e(TAG, "Products API Exception: ${e.message}")
                allSuccess = false
            }
            
            // Test 2: Listar usuarios
            Log.d(TAG, "Testing Users API...")
            try {
                when (val result = UserRepository().getAllUsers()) {
                    is ApiResult.Success -> {
                        val count = result.data.size
                        if (count == 0) {
                            results.add("⚠️ Users API: Conectado pero sin usuarios (BD vacía)")
                        } else {
                            results.add("✅ Users API: $count usuarios encontrados")
                        }
                        Log.d(TAG, "Users API OK: $count users")
                    }
                    is ApiResult.Error -> {
                        results.add("❌ Users API: ${result.message}")
                        Log.e(TAG, "Users API Error: ${result.message}")
                        allSuccess = false
                    }
                    else -> {
                        results.add("⏳ Users API: Loading...")
                    }
                }
            } catch (e: Exception) {
                results.add("❌ Users API: ${e.message}")
                Log.e(TAG, "Users API Exception: ${e.message}")
                allSuccess = false
            }
            
            // Resultado final
            val finalMessage = results.joinToString("\n")
            withContext(Dispatchers.Main) {
                callback(allSuccess, finalMessage)
            }
        }
    }
    
    /**
     * Test específico de login
     */
    fun testLogin(email: String, password: String, callback: (success: Boolean, message: String) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            Log.d(TAG, "Testing login for: $email")
            
            when (val result = UserRepository().login(email, password)) {
                is ApiResult.Success -> {
                    val user = result.data
                    val message = "✅ Login exitoso: ${user.nombre} (${user.email})"
                    Log.d(TAG, message)
                    withContext(Dispatchers.Main) {
                        callback(true, message)
                    }
                }
                is ApiResult.Error -> {
                    val message = "❌ Login fallido: ${result.message}"
                    Log.e(TAG, message)
                    withContext(Dispatchers.Main) {
                        callback(false, message)
                    }
                }
                else -> {
                    withContext(Dispatchers.Main) {
                        callback(false, "⏳ Login en proceso...")
                    }
                }
            }
        }
    }
    
    /**
     * Test de búsqueda de productos
     */
    fun testProductSearch(query: String, callback: (success: Boolean, count: Int, message: String) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            Log.d(TAG, "Searching products: $query")
            
            when (val result = ProductRepository().searchProducts(query)) {
                is ApiResult.Success -> {
                    val count = result.data.size
                    val message = "✅ Encontrados $count productos con '$query'"
                    Log.d(TAG, message)
                    withContext(Dispatchers.Main) {
                        callback(true, count, message)
                    }
                }
                is ApiResult.Error -> {
                    val message = "❌ Error en búsqueda: ${result.message}"
                    Log.e(TAG, message)
                    withContext(Dispatchers.Main) {
                        callback(false, 0, message)
                    }
                }
                else -> {
                    withContext(Dispatchers.Main) {
                        callback(false, 0, "⏳ Buscando...")
                    }
                }
            }
        }
    }
}
