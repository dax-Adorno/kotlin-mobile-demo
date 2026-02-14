package com.trabajofinal.educacionfiscal.network.models

import java.util.Date

data class Impuestos (
    val id_impuesto: Int,
    val nombre: String,
    val descripcion: String,
    val tipo: String,
    val porcentaje: Double,
    val monto_fijo: Double,
    val fecha_inicio: Date,
    val fecha_fin: Date,
    val activo: Boolean,
    val alcance: String,
    val id_tutorial: Int

)