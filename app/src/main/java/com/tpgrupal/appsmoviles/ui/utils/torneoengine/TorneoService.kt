    package com.tpgrupal.appsmoviles.ui.utils.torneoengine

    import com.tpgrupal.appsmoviles.data.model.Torneo
    import com.tpgrupal.appsmoviles.data.model.Partida
    import com.tpgrupal.appsmoviles.data.model.Prediccion
    import com.tpgrupal.appsmoviles.data.repository.TorneoRepository
    import com.tpgrupal.appsmoviles.data.repository.PartidaRepository
    import com.tpgrupal.appsmoviles.data.repository.PrediccionRepository
    import com.tpgrupal.appsmoviles.data.repository.UsuarioRepository

    class TorneoService(

        private val repo: TorneoRepository,
        private val partidaRepo: PartidaRepository,
        private val prediccionRepo: PrediccionRepository,
        private val usuarioRepo: UsuarioRepository,
        private val engine: TorneoEngine
    ) {

        fun puedeIniciar(participantes: List<String>): Boolean {
            return engine.puedeIniciar(participantes)
        }

        suspend fun iniciarTorneo(torneoId: String) {

            val torneo = repo.obtenerTorneoPorId(torneoId) ?: return

            val participantes = torneo.participantes

            if (!engine.puedeIniciar(participantes)) {
                throw IllegalStateException("Este formato no permite iniciar con estos jugadores")
            }

            val enfrentamientos =
                engine.generarPrimeraRonda(participantes)

            repo.actualizarEnfrentamientos(torneoId, enfrentamientos)
            repo.iniciarTorneo(torneoId, enfrentamientos)

            enfrentamientos.forEach {
                partidaRepo.crearPartida(
                    Partida(
                        torneoId = torneoId,
                        enfrentamientoId = it.id,
                        ronda = it.ronda,
                        participantes = listOf(it.jugador1, it.jugador2),
                        fecha = System.currentTimeMillis()
                    )
                )
            }
        }

        suspend fun seleccionarGanador(
            torneoId: String,
            enfrentamientoId: String,
            ganadorId: String
        ) {
            val torneo = repo.obtenerTorneoPorId(torneoId) ?: return

            val enfrentamiento = torneo.enfrentamientos
                .firstOrNull { it.id == enfrentamientoId } ?: return

            val actualizados = engine.registrarGanador(
                torneo.enfrentamientos,
                enfrentamientoId,
                ganadorId
            )

            repo.actualizarEnfrentamientos(torneoId, actualizados)

            val partida = partidaRepo.obtenerPartidaPorEnfrentamiento(
                torneoId,
                enfrentamientoId
            )

            partida?.let {
                partidaRepo.finalizarPartida(it.id, ganadorId)
                procesarPredicciones(it.id, ganadorId)
            }

            if (engine.rondaCompleta(actualizados)) {
                avanzarSiCorresponde(torneo.copy(enfrentamientos = actualizados))
            }
        }

        suspend fun predecirGanador(
            usuarioId: String,
            partidaId: String,
            ganadorPredichoId: String
        ) {

            prediccionRepo.guardarPrediccion(
                Prediccion(
                    usuarioId = usuarioId,
                    partidaId = partidaId,
                    ganadorPredichoId = ganadorPredichoId
                )
            )
        }

        private suspend fun procesarPredicciones(
            partidaId: String,
            ganadorId: String
        ) {

            val predicciones =
                prediccionRepo.obtenerPrediccionesDePartida(
                    partidaId
                )

            predicciones.forEach {

                if (
                    it.ganadorPredichoId == ganadorId
                ) {

                    usuarioRepo.sumarPuntos(
                        it.usuarioId,
                        10
                    )
                }
            }
        }

        private suspend fun avanzarSiCorresponde(torneo: Torneo) {

            val actualizados = torneo.enfrentamientos

            val rondaActual = actualizados.maxOf { it.ronda }

            val enfrentamientosRondaActual =
                actualizados.filter { it.ronda == rondaActual }

            val ganadores = enfrentamientosRondaActual
                .filter { it.ganador.isNotBlank() }
                .map { it.ganador }

            if (ganadores.size == 1 && enfrentamientosRondaActual.all { it.ganador.isNotBlank() }) {
                repo.finalizarTorneo(torneo.id)
                return
            }

            val siguienteRonda = engine.generarSiguienteRonda(
                enfrentamientosRondaActual,
                rondaActual
            )

            if (siguienteRonda.isEmpty()) {
                repo.finalizarTorneo(torneo.id)
                return
            }

            val todos = actualizados + siguienteRonda

            repo.actualizarEnfrentamientos(torneo.id, todos)

            siguienteRonda.forEach {
                partidaRepo.crearPartida(
                    Partida(
                        torneoId = torneo.id,
                        enfrentamientoId = it.id,
                        ronda = it.ronda,
                        participantes = listOf(it.jugador1, it.jugador2),
                        fecha = System.currentTimeMillis()
                    )
                )
            }
        }
    }