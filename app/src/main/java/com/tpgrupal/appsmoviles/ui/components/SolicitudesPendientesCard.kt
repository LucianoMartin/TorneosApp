package com.tpgrupal.appsmoviles.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tpgrupal.appsmoviles.data.model.SolicitudParticipacion
import com.tpgrupal.appsmoviles.data.model.Usuario

@Composable
fun SolicitudesPendientesCard(
    solicitudes: List<SolicitudParticipacion>,
    usuarios: Map<String, Usuario>,
    onAceptar: (SolicitudParticipacion) -> Unit,
    onRechazar: (SolicitudParticipacion) -> Unit
) {

    ElevatedCard {

        Column(
            Modifier.padding(20.dp)
        ) {

            Text(
                "Solicitudes pendientes",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(
                Modifier.height(12.dp)
            )

            solicitudes.forEach { solicitud ->

                SolicitudItem(
                    usuario =
                        usuarios[solicitud.usuarioId],

                    solicitud = solicitud,

                    onAceptar = {
                        onAceptar(solicitud)
                    },

                    onRechazar = {
                        onRechazar(solicitud)
                    }
                )

                Spacer(
                    Modifier.height(12.dp)
                )

                HorizontalDivider()

                Spacer(
                    Modifier.height(12.dp)
                )
            }
        }
    }
}