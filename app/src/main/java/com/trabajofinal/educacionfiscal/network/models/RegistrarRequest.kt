package com.trabajofinal.educacionfiscal.network.models

data class RegistrarRequest(
    val nombre: String,
    val email: String,
    val contrasena: String
)
