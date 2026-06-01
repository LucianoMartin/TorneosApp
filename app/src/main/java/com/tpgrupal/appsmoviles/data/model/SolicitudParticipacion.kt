package com.tpgrupal.appsmoviles.data.model
import com.tpgrupal.appsmoviles.data.model.enums.EstadoSolicitud

data class SolicitudParticipacion(
    val usuarioId: String = "",

    val fechaSolicitud: Long = 0L,

    val estado: EstadoSolicitud = EstadoSolicitud.PENDIENTE,

    val comentario: String = ""
)