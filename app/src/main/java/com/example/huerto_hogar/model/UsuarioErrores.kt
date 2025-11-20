package com.example.huerto_hogar.model

data class UsuarioErrores(
    val nombre: String? = null,
    val email: String? = null,
    val password: String? = null,
    val direccion: String? = null,
    val telefono: String? = null,
    val idComuna: String? = null,
    val idTipoUsuario: String? = null
)