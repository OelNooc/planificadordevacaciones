package com.oelnooc.planificadordevacaciones.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oelnooc.planificadordevacaciones.data.local.model.Lugar
import com.oelnooc.planificadordevacaciones.data.local.repository.LugarRepository
import com.oelnooc.planificadordevacaciones.data.remote.model.Indicadores
import com.oelnooc.planificadordevacaciones.data.remote.repository.IndicadorClientRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LugarViewModel(
    private val lugarRepository: LugarRepository,
    private val indicadorRepo: IndicadorClientRepo
) : ViewModel() {

    private val _lugares = MutableStateFlow<List<Lugar>>(emptyList())
    val lugares: StateFlow<List<Lugar>> = _lugares

    private val _indicadores = MutableStateFlow<Indicadores?>(null)
    val indicadores: StateFlow<Indicadores?> = _indicadores

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _lugarSeleccionado = MutableStateFlow<Lugar?>(null)
    val lugarSeleccionado: StateFlow<Lugar?> = _lugarSeleccionado

    fun loadLugares() {
        viewModelScope.launch {
            _lugares.value = lugarRepository.getLugares()
        }
    }

    fun agregarLugar(lugar: Lugar) {
        viewModelScope.launch {
            lugarRepository.insertLugar(lugar)
            loadLugares()
        }
    }

    fun eliminarLugar(lugar: Lugar) {
        viewModelScope.launch {
            lugarRepository.deleteLugar(lugar)
            loadLugares()
        }
    }

    fun actualizarLugar(lugar: Lugar) {
        viewModelScope.launch {
            lugarRepository.updateLugar(lugar)
            loadLugares()
        }
    }

    fun seleccionarLugar(lugar: Lugar) {
        _lugarSeleccionado.value = lugar
    }

    fun obtenerLugarPorId(lugarId: Int): Lugar? {
        return _lugares.value.find { it.id == lugarId }
    }

    fun loadIndicadores() {
        viewModelScope.launch {
            try {
                val response = indicadorRepo.getListaIndicadores().execute()
                if (response.isSuccessful) {
                    _indicadores.value = response.body()
                } else {
                    _errorMessage.value = "Error obteniendo indicadores"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }

    fun abrirGeolocalizacion(lugar: Lugar): Uri {
        return Uri.parse("geo:0,0?q=${lugar.ubicacion}")
    }
}