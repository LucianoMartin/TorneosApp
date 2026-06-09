package com.tpgrupal.appsmoviles.ui.utils

import androidx.compose.ui.graphics.Color
import com.tpgrupal.appsmoviles.data.model.enums.EstadoTorneo
import com.tpgrupal.appsmoviles.ui.theme.ErrorRed
import com.tpgrupal.appsmoviles.ui.theme.NeonBlue
import com.tpgrupal.appsmoviles.ui.theme.SuccessGreen
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.ui.graphics.vector.ImageVector

fun EstadoTorneo.textoAmigable(): String {

    return when (this) {

        EstadoTorneo.INSCRIPCION ->
            "Inscripciones abiertas"

        EstadoTorneo.EN_CURSO ->
            "En juego"

        EstadoTorneo.FINALIZADO ->
            "Finalizado"
    }
}

fun EstadoTorneo.colorEstado(): Color {

    return when (this) {

        EstadoTorneo.INSCRIPCION ->
            SuccessGreen

        EstadoTorneo.EN_CURSO ->
            NeonBlue

        EstadoTorneo.FINALIZADO ->
            ErrorRed
    }
}

fun EstadoTorneo.iconoEstado(): ImageVector {

    return when (this) {

        EstadoTorneo.INSCRIPCION ->
            Icons.Default.Edit

        EstadoTorneo.EN_CURSO ->
            Icons.Default.SportsEsports

        EstadoTorneo.FINALIZADO ->
            Icons.Default.EmojiEvents
    }
}