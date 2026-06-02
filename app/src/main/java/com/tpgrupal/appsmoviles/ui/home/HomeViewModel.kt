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
        observarTorneos()
    }

    private fun observarTorneos() {

        repository.observarTorneos { lista ->

            _torneos.value = lista
        }
    }

    fun toggleFavorito(
        torneo: Torneo,
        usuarioId: String
    ) {

        viewModelScope.launch {

            try {

                if (torneo.favoritos.contains(usuarioId)) {

                    repository.quitarFavorito(
                        torneo.id,
                        usuarioId
                    )

                } else {

                    repository.agregarFavorito(
                        torneo.id,
                        usuarioId
                    )
                }

            } catch (e: Exception) {

                e.printStackTrace()
            }
        }
    }
}