package com.example.huerto_hogar.service

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.InputStream

/**
 * Servicio para manejar la subida de imágenes a S3 mediante URLs prefirmadas
 */
class ImageUploadService(private val context: Context) {

    private val okHttpClient = OkHttpClient()

    /**
     * Obtiene información del archivo seleccionado desde el URI
     */
    fun getFileInfo(uri: Uri): FileInfo? {
        return try {
            val contentResolver = context.contentResolver
            val cursor = contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val displayNameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
                    
                    val fileName = if (displayNameIndex >= 0) {
                        it.getString(displayNameIndex)
                    } else {
                        "image_${System.currentTimeMillis()}.jpg"
                    }
                    
                    val fileSize = if (sizeIndex >= 0) {
                        it.getLong(sizeIndex)
                    } else {
                        0L
                    }
                    
                    val mimeType = contentResolver.getType(uri) ?: "image/jpeg"
                    
                    FileInfo(
                        fileName = fileName,
                        mimeType = mimeType,
                        size = fileSize,
                        uri = uri
                    )
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            println("Error obteniendo información del archivo: ${e.message}")
            null
        }
    }

    /**
     * Sube un archivo a S3 usando una URL prefirmada
     * @param presignedUrl URL firmada obtenida del backend
     * @param uri URI del archivo a subir
     * @param contentType Tipo MIME del archivo
     * @return true si la subida fue exitosa, false en caso contrario
     */
    suspend fun uploadToS3(presignedUrl: String, uri: Uri, contentType: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val contentResolver = context.contentResolver
                val inputStream: InputStream = contentResolver.openInputStream(uri)
                    ?: return@withContext false

                val fileBytes = inputStream.readBytes()
                inputStream.close()

                val requestBody = fileBytes.toRequestBody(contentType.toMediaType())
                
                val request = Request.Builder()
                    .url(presignedUrl)
                    .put(requestBody)
                    .addHeader("Content-Type", contentType)
                    .build()

                val response = okHttpClient.newCall(request).execute()
                val isSuccessful = response.isSuccessful
                
                if (!isSuccessful) {
                    println("Error en subida a S3: ${response.code} - ${response.message}")
                }
                
                response.close()
                isSuccessful

            } catch (e: Exception) {
                println("Excepción al subir a S3: ${e.message}")
                e.printStackTrace()
                false
            }
        }
    }

    /**
     * Valida que el archivo sea una imagen válida
     */
    fun isValidImageType(mimeType: String): Boolean {
        return mimeType.startsWith("image/") && mimeType in listOf(
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/gif",
            "image/webp"
        )
    }

    /**
     * Valida que el tamaño del archivo no exceda el límite (5MB por defecto)
     */
    fun isValidFileSize(sizeBytes: Long, maxSizeMB: Int = 5): Boolean {
        val maxSizeBytes = maxSizeMB * 1024 * 1024
        return sizeBytes <= maxSizeBytes
    }

    data class FileInfo(
        val fileName: String,
        val mimeType: String,
        val size: Long,
        val uri: Uri
    )
}
