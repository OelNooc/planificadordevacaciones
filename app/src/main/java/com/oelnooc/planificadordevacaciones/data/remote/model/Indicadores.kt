package com.oelnooc.planificadordevacaciones.data.remote.model

data class Indicadores(
    val version: String,
    val autor: String,
    val fecha: String,
    val uf: Indicador,
    val ivp: Indicador,
    val dolar: Indicador,
    val dolar_intercambio: Indicador,
    val euro: Indicador,
    val ipc: Indicador,
    val utm: Indicador,
    val imacec: Indicador,
    val tpm: Indicador,
    val libra_cobre: Indicador,
    val tasa_desempleo: Indicador,
    val bitcoin: Indicador
)