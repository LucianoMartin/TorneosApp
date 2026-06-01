package com.tpgrupal.appsmoviles.data.model

data class Partida(
    val id: String = "",

    val ronda: Int = 1,

    val participantes: List<String> = emptyList(),

    val ganadorId: String = "",

    val finalizada: Boolean = false,

    val fecha: Long = 0L
)