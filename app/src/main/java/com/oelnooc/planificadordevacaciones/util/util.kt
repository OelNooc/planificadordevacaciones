package com.oelnooc.planificadordevacaciones.util

fun convertirCLPtoUSD(costoCLP: Double, valorDolar: Double): String {
    val costoUSD = costoCLP / valorDolar
    return String.format("%.2f", costoUSD)
}