package com.oelnooc.planificadordevacaciones.data.remote.client

import com.oelnooc.planificadordevacaciones.data.remote.service.IndicadorService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class IndicadorClient {
    companion object {
        private const val BASE_URL = "https://mindicador.cl/"

        fun getInstance(): IndicadorService {
            val client = OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(IndicadorService::class.java)
        }
    }
}