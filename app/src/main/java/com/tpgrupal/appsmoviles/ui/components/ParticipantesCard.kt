package com.tpgrupal.appsmoviles.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tpgrupal.appsmoviles.data.model.enums.EstadoTorneo

@Composable
fun ParticipantesCard(
    torneoCompleto: Boolean,
    estadoTorneo: EstadoTorneo,
    onSolicitarParticipacion: (String) -> Unit
) {

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 8.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(
                text = "Participar en el torneo",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            HorizontalDivider()

            var comentario by remember {
                mutableStateOf("")
            }

            if (!torneoCompleto) {
                OutlinedTextField(
                    value = comentario,
                    onValueChange = {
                        comentario = it
                    },
                    label = {
                        Text("Comentario (opcional)")
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Button(

                enabled =
                    !torneoCompleto &&
                    estadoTorneo == EstadoTorneo.INSCRIPCION,

                onClick = {
                    onSolicitarParticipacion(comentario)
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)

            ) {

                Text(
                    text = when {

                        torneoCompleto ->
                            "Cupos completos"

                        else ->
                            "Solicitar participación"
                    },
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
    }
}