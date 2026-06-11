package com.tpgrupal.appsmoviles.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tpgrupal.appsmoviles.data.model.Enfrentamiento
import com.tpgrupal.appsmoviles.data.model.Usuario

@Composable
fun EnfrentamientoCard(
    enfrentamiento: Enfrentamiento,
    jugador1Id: String,
    jugador2Id: String,
    ganadorId: String,
    usuarios: Map<String, Usuario>,
    esAdmin: Boolean,
    yaParticipa: Boolean,
    miPrediccion: String?,
    partidaId: String?,
    onPredecir: (String, String) -> Unit,
    onSeleccionarGanador: (Enfrentamiento, String) -> Unit
) {

    val j1 = usuarios[jugador1Id]
    val j2 = usuarios[jugador2Id]

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor =
                if (ganadorId.isNotBlank())
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.surface
        )
    ) {

        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val j1IsWinner = ganadorId == jugador1Id
                val j2IsWinner = ganadorId == jugador2Id

                JugadorSide(j1, isWinner = j1IsWinner)

                Text("VS", color = MaterialTheme.colorScheme.primary)

                JugadorSide(j2, isWinner = j2IsWinner)
            }

            HorizontalDivider()

            // Botones de predicciones
            if (!esAdmin && yaParticipa && ganadorId.isBlank() && partidaId != null) {

                if (miPrediccion != null) {
                    Text(
                        "Tu predicción: ${
                            usuarios[miPrediccion]?.nombre ?: miPrediccion
                        }"
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                    Button(
                        onClick = { onPredecir(partidaId, jugador1Id) }
                    ) {
                        Text("Gana ${j1?.nombre ?: "J1"}")
                    }

                    Button(
                        onClick = { onPredecir(partidaId, jugador2Id) }
                    ) {
                        Text("Gana ${j2?.nombre ?: "J2"}")
                    }
                }
            }

            // Bones para decidir el ganador
            if (esAdmin && ganadorId.isBlank()) {

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                    OutlinedButton(
                        onClick = { onSeleccionarGanador(enfrentamiento, jugador1Id) }
                    ) {
                        Text("Gana ${j1?.nombre ?: "J1"}")
                    }

                    OutlinedButton(
                        onClick = { onSeleccionarGanador(enfrentamiento, jugador2Id) }
                    ) {
                        Text("Gana ${j2?.nombre ?: "J2"}")
                    }
                }
            }

            // Ganador
            if (ganadorId.isNotBlank()) {
                Text(
                    text = "🏆 Ganador: ${usuarios[ganadorId]?.nombre ?: ganadorId}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}