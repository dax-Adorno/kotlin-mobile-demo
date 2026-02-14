package com.trabajofinal.educacionfiscal.network.models

data class LoginRequest(
    val email: String,
    val contrasena: String
)
