package com.trabajofinal.educacionfiscal.network.models

data class Usuario(
    val id_usuario: Int,
    val nombre: String,
    val email: String,
    val contrasena: String,
    val fecha_registro: String,
    val id_tipo_de_usuario: Int
)
