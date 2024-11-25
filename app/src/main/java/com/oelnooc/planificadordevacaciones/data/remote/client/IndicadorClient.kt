package com.oelnooc.planificadordevacaciones.data.remote.client

import com.oelnooc.planificadordevacaciones.data.remote.service.IndicadorService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class IndicadorClient {
    companion object{
        const val base_url = "https://mindicador.cl/"

        fun getInstance() : IndicadorService
        {
            val retrofit = Retrofit.Builder()
                .baseUrl(base_url)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(IndicadorService::class.java)
        }
    }
}