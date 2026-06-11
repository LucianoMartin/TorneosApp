package com.tpgrupal.appsmoviles.ui.utils.torneoengine

import com.tpgrupal.appsmoviles.data.model.Enfrentamiento

interface TorneoEngine {

    fun puedeIniciar(participantes: List<String>): Boolean

    fun generarPrimeraRonda(
        participantes: List<String>,
        ronda: Int = 1
    ): List<Enfrentamiento>

    fun registrarGanador(
        enfrentamientos: List<Enfrentamiento>,
        enfrentamientoId: String,
        ganadorId: String
    ): List<Enfrentamiento>

    fun rondaCompleta(enfrentamientos: List<Enfrentamiento>): Boolean

    fun generarSiguienteRonda(
        enfrentamientos: List<Enfrentamiento>,
        rondaActual: Int
    ): List<Enfrentamiento>
}