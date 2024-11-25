package com.oelnooc.planificadordevacaciones.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.oelnooc.planificadordevacaciones.data.local.repository.LugarRepository
import com.oelnooc.planificadordevacaciones.data.remote.repository.IndicadorClientRepo

class LugarViewModelFactory(
    private val lugarRepository: LugarRepository,
    private val indicadorRepo: IndicadorClientRepo
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LugarViewModel::class.java)) {
            return LugarViewModel(lugarRepository, indicadorRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}