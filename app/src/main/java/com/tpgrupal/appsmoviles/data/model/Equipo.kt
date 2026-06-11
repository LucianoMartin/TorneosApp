package com.tpgrupal.appsmoviles.data.model

data class Equipo(
    val id: String = "",
    val nombre: String = "",
    val capitanId: String = "",
    val integrantes: List<String> = emptyList(),
    val logoUrl: String = ""
)