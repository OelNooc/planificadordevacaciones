package com.oelnooc.planificadordevacaciones.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lugar")
data class Lugar(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var nombre: String,
    var foto: String,
    var costoPorNoche: String,
    var traslados: String,
    var comentarios: String,
    var ubicacion: String,
    var orden: Int,
    var fotoUsuario: String?
)
