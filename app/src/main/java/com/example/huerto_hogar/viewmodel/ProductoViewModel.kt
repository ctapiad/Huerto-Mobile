package com.example.huerto_hogar.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huerto_hogar.model.ProductoErrores
import com.example.huerto_hogar.model.ProductoUIState
import com.example.huerto_hogar.network.ProductoDto
import com.example.huerto_hogar.network.UploadUrlRequest
import com.example.huerto_hogar.network.repository.ProductRepository
import com.example.huerto_hogar.network.ApiResult
import com.example.huerto_hogar.service.ImageUploadService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductoViewModel(private val imageUploadService: ImageUploadService? = null) : ViewModel() {

    private val _estado = MutableStateFlow(ProductoUIState())
    val estado: StateFlow<ProductoUIState> = _estado

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _operationResult = MutableStateFlow<String?>(null)
    val operationResult: StateFlow<String?> = _operationResult

    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri

    private val _uploadingImage = MutableStateFlow(false)
    val uploadingImage: StateFlow<Boolean> = _uploadingImage

    private val productRepository = ProductRepository()

    fun onIdProductoChange(valor: String) {
        // Convertir a mayúsculas automáticamente
        val valorMayusculas = valor.uppercase()
        _estado.update { it.copy(idProducto = valorMayusculas, errores = it.errores.copy(idProducto = null)) }
    }

    fun onNombreChange(valor: String) {
        _estado.update { it.copy(nombre = valor, errores = it.errores.copy(nombre = null)) }
    }

    fun onDescripcionChange(valor: String) {
        _estado.update { it.copy(descripcion = valor, errores = it.errores.copy(descripcion = null)) }
    }

    fun onPrecioChange(valor: String) {
        val precioInt = valor.toIntOrNull() ?: 0
        _estado.update { it.copy(precio = precioInt, errores = it.errores.copy(precio = null)) }
    }

    fun onStockChange(valor: String) {
        val stockInt = valor.toIntOrNull() ?: 0
        _estado.update { it.copy(stock = stockInt, errores = it.errores.copy(stock = null)) }
    }

    fun onLinkImagenChange(valor: String) {
        _estado.update { it.copy(linkImagen = valor, errores = it.errores.copy(linkImagen = null)) }
    }

    fun onOrigenChange(valor: String) {
        _estado.update { it.copy(origen = valor, errores = it.errores.copy(origen = null)) }
    }

    fun onCertificacionOrganicaChange(valor: Boolean) {
        _estado.update { it.copy(certificacionOrganica = valor) }
    }

    fun onEstaActivoChange(valor: Boolean) {
        _estado.update { it.copy(estaActivo = valor) }
    }

    fun onIdCategoriaChange(valor: Int) {
        _estado.update { it.copy(idCategoria = valor, errores = it.errores.copy(idCategoria = null)) }
    }

    fun onImageSelected(uri: Uri) {
        _selectedImageUri.value = uri
        // Limpiar error de imagen si había
        _estado.update { it.copy(errores = it.errores.copy(linkImagen = null)) }
    }

    fun clearImage() {
        _selectedImageUri.value = null
        _estado.update { it.copy(linkImagen = "") }
    }

    /**
     * Sube la imagen seleccionada a S3 y actualiza el linkImagen en el estado
     * @return true si la subida fue exitosa, false en caso contrario
     */
    suspend fun uploadSelectedImage(): Boolean {
        val uri = _selectedImageUri.value
        
        if (uri == null || imageUploadService == null) {
            return false
        }

        return try {
            _uploadingImage.value = true

            // Obtener info del archivo
            val fileInfo = imageUploadService.getFileInfo(uri)
            if (fileInfo == null) {
                _operationResult.value = "Error al obtener información del archivo"
                return false
            }

            // Validar tipo de archivo
            if (!imageUploadService.isValidImageType(fileInfo.mimeType)) {
                _operationResult.value = "El archivo debe ser una imagen (JPEG, PNG, GIF, WEBP)"
                return false
            }

            // Validar tamaño
            if (!imageUploadService.isValidFileSize(fileInfo.size)) {
                _operationResult.value = "La imagen no puede superar los 5MB"
                return false
            }

            // Solicitar URL prefirmada al backend
            val uploadUrlRequest = UploadUrlRequest(
                fileName = fileInfo.fileName,
                contentType = fileInfo.mimeType
            )

            when (val result = productRepository.getPresignedUploadUrl(uploadUrlRequest)) {
                is ApiResult.Success -> {
                    val uploadResponse = result.data
                    
                    // Subir el archivo a S3
                    val uploadSuccess = imageUploadService.uploadToS3(
                        presignedUrl = uploadResponse.uploadUrl,
                        uri = uri,
                        contentType = fileInfo.mimeType
                    )

                    if (uploadSuccess) {
                        // Actualizar el linkImagen con la URL pública
                        _estado.update { it.copy(linkImagen = uploadResponse.imageUrl) }
                        _operationResult.value = "Imagen subida exitosamente"
                        true
                    } else {
                        _operationResult.value = "Error al subir la imagen a S3"
                        false
                    }
                }
                is ApiResult.Error -> {
                    _operationResult.value = "Error al obtener URL de subida: ${result.message}"
                    false
                }
                else -> false
            }
        } catch (e: Exception) {
            _operationResult.value = "Error inesperado: ${e.message}"
            false
        } finally {
            _uploadingImage.value = false
        }
    }

    fun validarFormulario(esEdicion: Boolean = false): Boolean {
        val estadoActual = _estado.value
        val errores = ProductoErrores(
            // ID de producto no se valida porque se genera automáticamente en el formulario
            idProducto = null,
            nombre = when {
                estadoActual.nombre.isBlank() -> "El nombre no puede estar vacío"
                estadoActual.nombre.length > 100 -> "El nombre no puede exceder 100 caracteres"
                else -> null
            },
            descripcion = when {
                estadoActual.descripcion.isBlank() -> "La descripción no puede estar vacía"
                estadoActual.descripcion.length > 500 -> "La descripción no puede exceder 500 caracteres"
                else -> null
            },
            precio = when {
                estadoActual.precio <= 0 -> "El precio debe ser mayor a 0"
                else -> null
            },
            stock = when {
                estadoActual.stock < 0 -> "El stock no puede ser negativo"
                else -> null
            },
            linkImagen = when {
                estadoActual.linkImagen.isEmpty() -> "Debe seleccionar una imagen"
                estadoActual.linkImagen.length > 255 -> "La URL de imagen no puede exceder 255 caracteres"
                !estadoActual.linkImagen.startsWith("http") -> "La URL debe comenzar con http:// o https://"
                else -> null
            },
            origen = when {
                estadoActual.origen.isNotEmpty() && estadoActual.origen.length > 100 -> "El origen no puede exceder 100 caracteres"
                else -> null
            },
            idCategoria = when {
                estadoActual.idCategoria <= 0 -> "Debe seleccionar una categoría válida"
                else -> null
            }
        )

        val hayErrores = listOfNotNull(
            errores.idProducto,
            errores.nombre,
            errores.descripcion,
            errores.precio,
            errores.stock,
            errores.linkImagen,
            errores.origen,
            errores.idCategoria
        ).isNotEmpty()

        _estado.update { it.copy(errores = errores) }

        return !hayErrores
    }

    fun cargarProducto(producto: ProductoDto) {
        _estado.value = ProductoUIState(
            idProducto = producto.idProducto,
            nombre = producto.nombre,
            descripcion = producto.descripcion,
            precio = producto.precio,
            stock = producto.stock,
            linkImagen = producto.linkImagen ?: "",
            origen = producto.origen ?: "",
            certificacionOrganica = producto.certificacionOrganica,
            estaActivo = producto.estaActivo,
            idCategoria = producto.idCategoria,
            errores = ProductoErrores()
        )
    }

    fun limpiarFormulario() {
        _estado.value = ProductoUIState()
        _operationResult.value = null
        _selectedImageUri.value = null
    }

    fun crearProducto(onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (!validarFormulario(esEdicion = false)) {
            onError("Por favor corrija los errores en el formulario")
            return
        }

        val estadoActual = _estado.value
        
        viewModelScope.launch {
            _isLoading.value = true
            
            when (val result = productRepository.createProduct(
                idProducto = estadoActual.idProducto,
                nombre = estadoActual.nombre,
                linkImagen = estadoActual.linkImagen.ifEmpty { null },
                descripcion = estadoActual.descripcion,
                precio = estadoActual.precio,
                stock = estadoActual.stock,
                idCategoria = estadoActual.idCategoria,
                origen = estadoActual.origen.ifEmpty { null },
                certificacionOrganica = estadoActual.certificacionOrganica,
                estaActivo = estadoActual.estaActivo
            )) {
                is ApiResult.Success -> {
                    _operationResult.value = "Producto creado exitosamente"
                    limpiarFormulario()
                    onSuccess()
                }
                is ApiResult.Error -> {
                    _operationResult.value = result.message
                    onError(result.message)
                }
                else -> {}
            }
            
            _isLoading.value = false
        }
    }

    fun actualizarProducto(id: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (!validarFormulario(esEdicion = true)) {
            onError("Por favor corrija los errores en el formulario")
            return
        }

        val estadoActual = _estado.value
        
        viewModelScope.launch {
            _isLoading.value = true
            
            when (val result = productRepository.updateProduct(
                productId = id,
                nombre = estadoActual.nombre,
                linkImagen = estadoActual.linkImagen.ifEmpty { null },
                descripcion = estadoActual.descripcion,
                precio = estadoActual.precio,
                stock = estadoActual.stock,
                idCategoria = estadoActual.idCategoria,
                origen = estadoActual.origen.ifEmpty { null },
                certificacionOrganica = estadoActual.certificacionOrganica,
                estaActivo = estadoActual.estaActivo
            )) {
                is ApiResult.Success -> {
                    _operationResult.value = "Producto actualizado exitosamente"
                    limpiarFormulario()
                    onSuccess()
                }
                is ApiResult.Error -> {
                    _operationResult.value = result.message
                    onError(result.message)
                }
                else -> {}
            }
            
            _isLoading.value = false
        }
    }
}
