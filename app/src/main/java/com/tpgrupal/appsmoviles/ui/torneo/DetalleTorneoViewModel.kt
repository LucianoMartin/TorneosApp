package com.tpgrupal.appsmoviles.ui.torneo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tpgrupal.appsmoviles.data.model.Enfrentamiento
import com.tpgrupal.appsmoviles.data.model.Partida
import com.tpgrupal.appsmoviles.data.model.SolicitudParticipacion
import com.tpgrupal.appsmoviles.data.model.Torneo
import com.tpgrupal.appsmoviles.data.model.Usuario
import com.tpgrupal.appsmoviles.data.model.enums.EstadoSolicitud
import com.tpgrupal.appsmoviles.data.repository.JuegoRepository
import com.tpgrupal.appsmoviles.data.repository.PartidaRepository
import com.tpgrupal.appsmoviles.data.repository.PrediccionRepository
import com.tpgrupal.appsmoviles.data.repository.TorneoRepository
import com.tpgrupal.appsmoviles.data.repository.UsuarioRepository
import com.tpgrupal.appsmoviles.ui.utils.torneoengine.TorneoService
import kotlinx.coroutines.launch

class DetalleTorneoViewModel(
    private val repo: TorneoRepository,
    private val service: TorneoService,
    private val usuarioRepo: UsuarioRepository
) : ViewModel() {

    var torneo by mutableStateOf<Torneo?>(null)
        private set

    var solicitudes by mutableStateOf<List<SolicitudParticipacion>>(emptyList())
        private set

    var usuarios by mutableStateOf<Map<String, Usuario>>(emptyMap())
        private set

    private val juegoRepo = JuegoRepository()

    var nombreJuego by mutableStateOf("")
        private set

    var partidas by mutableStateOf<List<Partida>>(emptyList())
        private set

    private val partidaRepo = PartidaRepository()

    private val prediccionRepo = PrediccionRepository()

    var misPredicciones by mutableStateOf<Map<String, String>>(
        emptyMap()
    )
        private set

    fun cargarPartidas(
        torneoId: String
    ) {

        viewModelScope.launch {

            partidas =
                partidaRepo.obtenerPartidasPorTorneo(
                    torneoId
                )
        }
    }

    private val nombresBots = listOf(
        "Kratos",
        "Ghost",
        "Shadow",
        "Dragon",
        "Ninja",
        "Titan",
        "Phoenix",
        "Zeus",
        "Raptor",
        "Nova"
    )

    fun cargarTorneo(id: String) {

        viewModelScope.launch {

            torneo =
                repo.obtenerTorneoPorId(id)

            torneo?.let {
                cargarJuego(it.juegoId)
            }
        }
    }

    fun cargarSolicitudes(torneoId: String) {
        viewModelScope.launch {
            solicitudes = repo.obtenerSolicitudes(torneoId)
        }
    }

    fun cargarUsuario(uid: String) {
        if (usuarios.containsKey(uid)) return

        viewModelScope.launch {

            val usuario =
                usuarioRepo.obtenerUsuario(uid)

            if (usuario != null) {

                usuarios =
                    usuarios + (uid to usuario)
            }
        }
    }

    fun cargarJuego(juegoId: String) {

        viewModelScope.launch {

            nombreJuego =
                juegoRepo
                    .obtenerJuegoPorId(juegoId)
                    ?.nombre
                    ?: "Juego desconocido"
        }
    }

    fun cargarPrediccion(
        partidaId: String,
        usuarioId: String
    ) {

        viewModelScope.launch {

            val prediccion =
                prediccionRepo.obtenerPrediccionUsuario(
                    partidaId,
                    usuarioId
                )

            if (prediccion != null) {

                misPredicciones =
                    misPredicciones +
                            (
                                    partidaId to
                                            prediccion.ganadorPredichoId
                                    )
            }
        }
    }

    fun puedeIniciarTorneo(): Boolean {
        val t = torneo ?: return false
        return service.puedeIniciar(t.participantes)
    }

    fun iniciarTorneo(
        torneoId: String
    ) {

        viewModelScope.launch {

            service.iniciarTorneo(
                torneoId
            )

            cargarTorneo(
                torneoId
            )
        }
    }

    fun seleccionarGanador(
        torneoId: String,
        enfrentamiento: Enfrentamiento,
        ganadorId: String
    ) {
        viewModelScope.launch {
            service.seleccionarGanador(
                torneoId,
                enfrentamiento.id,
                ganadorId
            )

            torneo = repo.obtenerTorneoPorId(torneoId)
            cargarPartidas(torneoId)
        }
    }

    fun solicitarParticipacion(
        torneoId: String,
        usuarioId: String,
        comentario: String
    ) {
        viewModelScope.launch {
            repo.solicitarParticipacion(
                torneoId,
                SolicitudParticipacion(
                    usuarioId = usuarioId,
                    comentario = comentario,
                    fechaSolicitud = System.currentTimeMillis(),
                    estado = EstadoSolicitud.PENDIENTE
                )
            )

            cargarTorneo(torneoId)
            cargarSolicitudes(torneoId)
        }
    }

    fun aceptarSolicitud(
        torneoId: String,
        solicitud: SolicitudParticipacion
    ) {
        viewModelScope.launch {

            repo.aceptarSolicitud(
                torneoId,
                solicitud.usuarioId
            )

            cargarTorneo(torneoId)
            cargarSolicitudes(torneoId)
        }
    }

    fun rechazarSolicitud(torneoId: String, usuarioId: String) {
        viewModelScope.launch {
            repo.rechazarSolicitud(torneoId, usuarioId)
            cargarSolicitudes(torneoId)
        }
    }

    fun toggleFavorito(
        usuarioId: String
    ) {

        val torneoActual = torneo ?: return

        viewModelScope.launch {

            try {

                if (torneoActual.favoritos.contains(usuarioId)) {

                    repo.quitarFavorito(
                        torneoActual.id,
                        usuarioId
                    )

                } else {

                    repo.agregarFavorito(
                        torneoActual.id,
                        usuarioId
                    )
                }

                cargarTorneo(torneoActual.id)

            } catch (e: Exception) {

                e.printStackTrace()
            }
        }
    }

    fun predecirGanador(
        usuarioId: String,
        partidaId: String,
        ganadorId: String
    ) {

        viewModelScope.launch {

            service.predecirGanador(
                usuarioId,
                partidaId,
                ganadorId
            )

            misPredicciones =
                misPredicciones +
                        (partidaId to ganadorId)
        }
    }

    fun agregarBot() {

        val torneoActual = torneo ?: return

        viewModelScope.launch {

            val participantesActuales = torneoActual.participantes.size
            val max = torneoActual.maxParticipantes

            if (participantesActuales >= max) return@launch

            val baseNombre = nombresBots.random()

            val cantidadExistentes = torneoActual.participantes.count {
                usuarios[it]?.nombre?.startsWith(baseNombre) == true
            }

            val nombreFinal =
                if (cantidadExistentes == 0) baseNombre
                else "$baseNombre ${cantidadExistentes + 1}"

            val botId = "bot_${System.currentTimeMillis()}"

            repo.agregarParticipante(
                torneoActual.id,
                botId
            )

            usuarioRepo.crearUsuario(
                Usuario(
                    uid = botId,
                    nombre = nombreFinal,
                    avatarUrl = ""
                )
            )

            cargarTorneo(torneoActual.id)
        }
    }

    fun esBot(uid: String): Boolean {
        return uid.startsWith("bot_")
    }
}