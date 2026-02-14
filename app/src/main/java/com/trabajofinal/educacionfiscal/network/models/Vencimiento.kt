package com.trabajofinal.educacionfiscal.network.models

import java.util.Date

data class Vencimiento(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val fecha_vencimiento: String,
    val id_impuesto: Int
)
