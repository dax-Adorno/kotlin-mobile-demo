package com.trabajofinal.educacionfiscal.network.models

data class Registros (
    val id_registro: Int,
    val id_usuario: Int,
    val ingresos_brutos: Double,
    val superficie_afectada: Double,
    val energia_electrica: Double,
    val alquileres: Double,
    val muebles: Boolean,
)