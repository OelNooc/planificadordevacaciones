package com.oelnooc.planificadordevacaciones.data.remote.repository

import com.oelnooc.planificadordevacaciones.data.remote.client.IndicadorClient
import com.oelnooc.planificadordevacaciones.data.remote.model.Indicadores
import retrofit2.Call

class IndicadorClientRepo {
    private val cliente = IndicadorClient.getInstance()

    suspend fun getListaIndicadores(): Indicadores {
        return cliente.getIndicadores()
    }
}