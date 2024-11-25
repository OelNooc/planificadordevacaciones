package com.oelnooc.planificadordevacaciones.data.local.dao

import androidx.room.*
import com.oelnooc.planificadordevacaciones.data.local.model.Lugar

@Dao
interface LugarDao {

    @Query("SELECT * FROM lugar ORDER BY orden ASC")
    suspend fun getAll(): List<Lugar>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(lugar: Lugar)

    @Update
    suspend fun update(lugar: Lugar)

    @Delete
    suspend fun delete(lugar: Lugar)
}