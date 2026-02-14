package com.trabajofinal.educacionfiscal.network.models

data class RegistrosRequest (

    val id_usuario: Int,
    val ingresos_brutos: Double,
    val superficie_afectada: Double,
    val energia_electrica: Double,
    val alquileres: Double,
    val muebles: Boolean,
)