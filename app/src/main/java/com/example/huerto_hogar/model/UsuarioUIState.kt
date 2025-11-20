package com.example.huerto_hogar.model

data class UsuarioUIState(
    val nombre : String =
        ""
    ,
    val email : String =
        ""
    ,
    val password : String =
        ""
    ,
    val direccion : String =
        ""
    ,
    val telefono : Int =
        0,
    val idComuna : Int =
        1,  // Valor por defecto válido
    val idTipoUsuario : Int =
        0,
    val errores : UsuarioErrores = UsuarioErrores()
)