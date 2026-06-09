package com.tpgrupal.appsmoviles.ui.participaciones

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tpgrupal.appsmoviles.data.model.Torneo
import com.tpgrupal.appsmoviles.data.model.enums.EstadoTorneo
import com.tpgrupal.appsmoviles.data.repository.TorneoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ParticipacionesViewModel : ViewModel() {

    private val repository = TorneoRepository()

    private val _torneosActivos =
        MutableStateFlow<List<Torneo>>(emptyList())

    val torneosActivos: StateFlow<List<Torneo>>
        get() = _torneosActivos

    private val _torneosFinalizados =
        MutableStateFlow<List<Torneo>>(emptyList())

    val torneosFinalizados: StateFlow<List<Torneo>>
        get() = _torneosFinalizados

    init {
        observarParticipaciones()
    }

    private fun observarParticipaciones() {

        repository.observarTorneos { lista ->

            val usuarioId =
                Firebase.auth.currentUser?.uid
                    ?: return@observarTorneos

            _torneosActivos.value =
                lista.filter {

                    usuarioId in it.participantes &&
                            it.estado != EstadoTorneo.FINALIZADO
                }

            _torneosFinalizados.value =
                lista.filter {

                    usuarioId in it.participantes &&
                            it.estado == EstadoTorneo.FINALIZADO
                }
        }
    }

    fun toggleFavorito(
        torneo: Torneo,
        usuarioId: String
    ) {

        viewModelScope.launch {

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
        }
    }
}