package com.trabajofinal.educacionfiscal.network.models

data class Categoria (
    val id_categoria: Int,
    val nombre: String,
    val ingresos_brutos: Double,
    val superficie_afectada: Double,
    val energia_electrica: Double,
    val alquileres: Double,
    val precio_unitario: Double,
    val impuesto_integrado: Double,
    val impuesto_integrado_muebles: Double,
    val aportes_sipa: Double,
    val obra_social: Double,
    val total: Double,
    val total_muebles: Double
)