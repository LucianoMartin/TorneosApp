package com.tpgrupal.appsmoviles.ui.utils.torneoengine

import com.tpgrupal.appsmoviles.data.model.Enfrentamiento
import java.util.UUID

class EliminacionDirectaEngine : TorneoEngine {

    private fun esPotenciaDeDos(n: Int): Boolean {
        return n > 0 && (n and (n - 1)) == 0
    }

    override fun puedeIniciar(participantes: List<String>): Boolean {
        return esPotenciaDeDos(participantes.size)
    }

    override fun generarPrimeraRonda(
        participantes: List<String>,
        ronda: Int
    ): List<Enfrentamiento> {

        val mezclados = participantes.shuffled()
        val result = mutableListOf<Enfrentamiento>()

        for (i in mezclados.indices step 2) {

            val j1 = mezclados[i]
            val j2 = mezclados.getOrNull(i + 1)

            if (j2 != null) {
                result.add(
                    Enfrentamiento(
                        id = UUID.randomUUID().toString(),
                        jugador1 = j1,
                        jugador2 = j2,
                        ronda = ronda
                    )
                )
            }
        }

        return result
    }

    override fun registrarGanador(
        enfrentamientos: List<Enfrentamiento>,
        enfrentamientoId: String,
        ganadorId: String
    ): List<Enfrentamiento> {

        return enfrentamientos.map {
            if (it.id == enfrentamientoId) {
                it.copy(ganador = ganadorId)
            } else it
        }
    }

    override fun rondaCompleta(enfrentamientos: List<Enfrentamiento>): Boolean {
        return enfrentamientos.isNotEmpty() &&
                enfrentamientos.all { it.ganador.isNotBlank() }
    }

    override fun generarSiguienteRonda(
        enfrentamientos: List<Enfrentamiento>,
        rondaActual: Int
    ): List<Enfrentamiento> {

        val ganadores = enfrentamientos
            .filter { it.ganador.isNotBlank() }
            .map { it.ganador }

        if (ganadores.size < 2) return emptyList()

        return generarPrimeraRonda(
            participantes = ganadores,
            ronda = rondaActual + 1
        )
    }
}