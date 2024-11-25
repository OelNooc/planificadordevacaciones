package com.oelnooc.planificadordevacaciones.data.remote.model

data class Indicador(
    val codigo: String,
    val nombre: String,
    val unidad_medida: String,
    val fecha: String,
    val valor: Double
)