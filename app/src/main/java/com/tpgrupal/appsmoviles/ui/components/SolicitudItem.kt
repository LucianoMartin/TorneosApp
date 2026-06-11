package com.tpgrupal.appsmoviles.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tpgrupal.appsmoviles.data.model.SolicitudParticipacion
import com.tpgrupal.appsmoviles.data.model.Usuario

@Composable
fun SolicitudItem(
    usuario: Usuario?,
    solicitud: SolicitudParticipacion,
    onAceptar: () -> Unit,
    onRechazar: () -> Unit
) {

    Column {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            AvatarUsuario(
                avatarUrl = usuario?.avatarUrl
            )

            Spacer(
                Modifier.width(12.dp)
            )

            Column {

                Text(
                    usuario?.nombre ?: "Jugador"
                )

                if (solicitud.comentario.isNotBlank()) {

                    Text(
                        solicitud.comentario,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        Spacer(
            Modifier.height(12.dp)
        )

        Row {

            Button(
                onClick = onAceptar
            ) {
                Text("Aceptar")
            }

            Spacer(
                Modifier.width(8.dp)
            )

            OutlinedButton(
                onClick = onRechazar
            ) {
                Text("Rechazar")
            }
        }
    }
}