package com.tpgrupal.appsmoviles.data.model

data class Notificacion(

    val id: String = "",

    val usuarioId: String = "",

    val titulo: String = "",

    val mensaje: String = "",

    val fecha: Long = 0L,

    val leida: Boolean = false
)