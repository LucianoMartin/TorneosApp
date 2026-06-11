package com.tpgrupal.appsmoviles.data.model

data class Prediccion(
    val usuarioId: String = "",
    val partidaId: String = "",
    val ganadorPredichoId: String = "",
    val acertada: Boolean = false
)