package com.tpgrupal.appsmoviles.ui.tienda

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tpgrupal.appsmoviles.data.model.Premio
import com.tpgrupal.appsmoviles.data.model.Usuario
import com.tpgrupal.appsmoviles.data.repository.PremioRepository
import com.tpgrupal.appsmoviles.data.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TiendaViewModel : ViewModel() {

    private val premioRepo = PremioRepository()
    private val usuarioRepo = UsuarioRepository()

    private val _premios = MutableStateFlow<List<Premio>>(emptyList())
    val premios: StateFlow<List<Premio>> = _premios

    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> = _usuario

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje

    fun cargarDatos(uid: String) {
        viewModelScope.launch {
            _loading.value = true

            try {
                _usuario.value = usuarioRepo.obtenerUsuario(uid)
                _premios.value = premioRepo.getPremios()
            } finally {
                _loading.value = false
            }
        }
    }

    fun comprarPremio(premio: Premio) {
        viewModelScope.launch {

            val user = _usuario.value ?: return@launch

            if (user.puntos < premio.costoPuntos) {
                _mensaje.value = "No tienes suficientes puntos"
                return@launch
            }

            val nuevosPuntos = user.puntos - premio.costoPuntos

            usuarioRepo.actualizarPuntos(user.uid, nuevosPuntos)

            _usuario.value = user.copy(puntos = nuevosPuntos)

            _mensaje.value = "🎉 Recompensa reclamada, llegará pronto"
        }
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }
}