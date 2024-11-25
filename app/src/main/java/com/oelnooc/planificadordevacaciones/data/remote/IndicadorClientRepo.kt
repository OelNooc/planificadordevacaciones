package com.oelnooc.planificadordevacaciones.data.remote

import com.oelnooc.planificadordevacaciones.data.remote.client.IndicadorClient
import com.oelnooc.planificadordevacaciones.data.remote.model.Indicadores
import retrofit2.Call

class IndicadorClientRepo {
    private val cliente = IndicadorClient.getInstance()

    fun getListaIndicadores(): Call<Indicadores> {
        return cliente.getIndicadores()
    }
}