package com.example.huerto_hogar.network

import android.util.Log

/**
 * Clase sellada para representar el resultado de una operación de red
 */
sealed class ApiResult<out T> {
    data class Success<out T>(val data: T) : ApiResult<T>()
    data class Error(val message: String, val code: Int? = null) : ApiResult<Nothing>()
    object Loading : ApiResult<Nothing>()
}

/**
 * Extensión para ejecutar llamadas API de forma segura
 */
suspend fun <T> safeApiCall(
    apiCall: suspend () -> retrofit2.Response<T>
): ApiResult<T> {
    return try {
        val response = apiCall()
        when {
            response.isSuccessful -> {
                // HTTP 204 No Content - respuesta vacía pero exitosa
                if (response.code() == 204) {
                    // Para listas vacías, devolver lista vacía en lugar de error
                    @Suppress("UNCHECKED_CAST")
                    ApiResult.Success(emptyList<Any>() as T)
                } else {
                    response.body()?.let {
                        ApiResult.Success(it)
                    } ?: run {
                        // Si no hay body pero la respuesta es exitosa (200, 201, etc.)
                        // devolver un mensaje de éxito genérico
                        if (response.code() in 200..299) {
                            @Suppress("UNCHECKED_CAST")
                            ApiResult.Success("Operación exitosa" as T)
                        } else {
                            ApiResult.Error("Respuesta vacía del servidor", response.code())
                        }
                    }
                }
            }
            else -> {
                val errorBody = response.errorBody()?.string()
                Log.e("ApiResult", "Error HTTP ${response.code()}: $errorBody")
                Log.e("ApiResult", "URL: ${response.raw().request.url}")
                Log.e("ApiResult", "Method: ${response.raw().request.method}")
                ApiResult.Error(
                    message = errorBody ?: response.message() ?: "Error desconocido",
                    code = response.code()
                )
            }
        }
    } catch (e: com.google.gson.JsonSyntaxException) {
        // Error de parseo JSON - probablemente la API devolvió texto plano
        // pero la operación fue exitosa (ya que llegó aquí sin error de red)
        @Suppress("UNCHECKED_CAST")
        ApiResult.Success("Operación completada exitosamente" as T)
    } catch (e: Exception) {
        ApiResult.Error(
            message = "Error: ${e.message ?: "Verifica tu conexión y que los microservicios estén corriendo"}"
        )
    }
}
