package com.oelnooc.planificadordevacaciones.data.remote.service

import com.oelnooc.planificadordevacaciones.data.remote.model.Indicadores
import retrofit2.http.GET

interface IndicadorService {

    @GET("api")
    suspend fun getIndicadores(): Indicadores
}