package com.tpgrupal.appsmoviles.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tpgrupal.appsmoviles.data.model.Torneo
import com.tpgrupal.appsmoviles.data.repository.TorneoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val repository = TorneoRepository()

    private val _torneos =
        MutableStateFlow<List<Torneo>>(emptyList())

    val torneos: StateFlow<List<Torneo>>
        get() = _torneos

    init {
        cargarTorneos()
    }

    private fun cargarTorneos() {

        viewModelScope.launch {

            _torneos.value =
                repository.obtenerTorneos()
        }
    }
}