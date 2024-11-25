package com.oelnooc.planificadordevacaciones.data.remote.model

data class Indicador(
    val codigo: String,
    val fecha: String,
    val nombre: String,
    val unidad_medida: String,
    val valor: Double
)