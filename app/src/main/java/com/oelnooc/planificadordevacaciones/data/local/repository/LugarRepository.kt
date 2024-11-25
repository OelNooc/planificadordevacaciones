package com.oelnooc.planificadordevacaciones.data.local.repository

import com.oelnooc.planificadordevacaciones.data.local.dao.LugarDao
import com.oelnooc.planificadordevacaciones.data.local.model.Lugar

class LugarRepository(private val lugarDao: LugarDao) {

    suspend fun getLugares(): List<Lugar> = lugarDao.getAll()

    suspend fun insertLugar(lugar: Lugar) = lugarDao.insert(lugar)

    suspend fun updateLugar(lugar: Lugar) = lugarDao.update(lugar)

    suspend fun deleteLugar(lugar: Lugar) = lugarDao.delete(lugar)
}