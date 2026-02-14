package com.trabajofinal.educacionfiscal.network.models

data class Ejemplo (
    val id_ejemplo: Int,
    val nombre: String,
    val descripcion: String,
    val ingresos_brutos: Double,
    val superficie_afectada: Double,
    val energia_electrica: Double,
    val alquileres: Double,
    val muebles: Boolean,
    val id_categoria: Int
)