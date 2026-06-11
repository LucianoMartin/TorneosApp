package com.tpgrupal.appsmoviles.data.model
import com.tpgrupal.appsmoviles.data.model.enums.TipoTorneo
import com.tpgrupal.appsmoviles.data.model.enums.EstadoTorneo
import com.tpgrupal.appsmoviles.data.model.Enfrentamiento

data class Torneo(

    val id: String = "",
    val nombre: String = "",
    val juegoId: String = "",
    val tipo: TipoTorneo = TipoTorneo.ELIMINACION_SIMPLE,
    val permiteEquipos: Boolean = false,

    val descripcion: String = "",
    val requisitos: String = "",
    val imagenUrl: String = "",

    val creadorId: String = "",
    val moderadores: List<String> = emptyList(),
    val participantes: List<String> = emptyList(),
    val maxParticipantes: Int = 16,
    val favoritos: List<String> = emptyList(),

    val fechaCreacion: Long = 0L,
    val fechaInicio: Long = 0L,
    val fechaFin: Long = 0L,

    val estado: EstadoTorneo = EstadoTorneo.INSCRIPCION,

    val ciudad: String = "",
    val latitud: Double = 0.0,
    val longitud: Double = 0.0,

    val streamUrl: String = "",

    val enfrentamientos: List<Enfrentamiento> = emptyList()
)