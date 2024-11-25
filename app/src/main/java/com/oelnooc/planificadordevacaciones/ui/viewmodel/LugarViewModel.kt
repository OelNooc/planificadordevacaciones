package com.oelnooc.planificadordevacaciones.ui.viewmodel

import android.util.Log
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

    private val _lugarSeleccionado = MutableStateFlow<Lugar?>(null)
    val lugarSeleccionado: StateFlow<Lugar?> = _lugarSeleccionado

    init {
        loadLugares()
        loadIndicadores()
    }

    fun loadLugares() {
        viewModelScope.launch {
            _lugares.value = lugarRepository.getLugares()
        }
    }

    fun loadLugarById(lugarId: Int) {
        viewModelScope.launch {
            val lugar = lugarRepository.getLugarById(lugarId)
            _lugarSeleccionado.value = lugar
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
                val response = indicadorRepo.getListaIndicadores()
                _indicadores.value = response
                Log.d("API_Success", "Indicadores cargados correctamente")
                Log.d("api_result", response.toString())
            } catch (e: Exception) {
                _errorMessage.value = "Error obteniendo indicadores: ${e.message}"
                Log.e("error_api_exception", e.message.toString())
                e.printStackTrace()
            }
        }
    }

    fun getDolarValue(): Double {
        Log.e("valor_dolar", _indicadores.value?.dolar?.valor.toString())
        return _indicadores.value?.dolar?.valor ?: 900.0
    }

    fun abrirGeolocalizacion(lugar: Lugar): Pair<Double, Double> {
        val (lat, lon) = lugar.ubicacion.split(",").map { it.trim().toDoubleOrNull() ?: 0.0 }
        return Pair(lat, lon)
    }

    fun actualizarFotoUsuario(lugarId: Int, nuevaFotoUri: String) {
        viewModelScope.launch {
            val lugar = _lugares.value.find { it.id == lugarId }
            if (lugar != null) {
                val lugarActualizado = lugar.copy(fotoUsuario = nuevaFotoUri)
                lugarRepository.updateLugar(lugarActualizado)
                loadLugares()
                loadLugarById(lugarId)
            }
        }
    }
}