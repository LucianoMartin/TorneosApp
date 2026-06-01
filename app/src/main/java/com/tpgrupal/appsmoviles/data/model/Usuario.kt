package com.tpgrupal.appsmoviles.data.model

data class Usuario(
    val uid: String = "",
    val nombre: String = "",
    val email: String = "",
    val avatarUrl: String = "",
    val puntos: Int = 0,
    val favoritos: List<String> = emptyList()
)