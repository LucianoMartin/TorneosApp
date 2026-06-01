package com.tpgrupal.appsmoviles.data.model

data class Prediccion(
    val usuarioId: String = "",
    val torneoId: String = "",
    val ganadorPredichoId: String = "",
    val puntosGanados: Int = 0
)