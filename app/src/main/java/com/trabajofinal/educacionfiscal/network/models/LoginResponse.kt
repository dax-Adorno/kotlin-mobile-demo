package com.trabajofinal.educacionfiscal.network.models

data class LoginResponse(
    val token: String,
    val user: Usuario
)
