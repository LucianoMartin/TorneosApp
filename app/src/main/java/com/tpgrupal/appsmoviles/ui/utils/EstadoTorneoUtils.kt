package com.tpgrupal.appsmoviles.ui.utils

import com.tpgrupal.appsmoviles.data.model.enums.EstadoTorneo

fun EstadoTorneo.textoAmigable(): String {

    return when (this) {

        EstadoTorneo.INSCRIPCION ->
            "📝 Inscripciones abiertas"

        EstadoTorneo.EN_CURSO ->
            "🎮 En juego"

        EstadoTorneo.FINALIZADO ->
            "🏆 Finalizado"
    }
}