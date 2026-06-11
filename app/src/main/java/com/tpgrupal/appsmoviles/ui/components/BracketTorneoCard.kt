package com.tpgrupal.appsmoviles.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tpgrupal.appsmoviles.data.model.Enfrentamiento
import com.tpgrupal.appsmoviles.data.model.Partida
import com.tpgrupal.appsmoviles.data.model.Torneo
import com.tpgrupal.appsmoviles.data.model.Usuario

@Composable
fun BracketTorneoCard(
    torneo: Torneo,
    partidas: List<Partida>,
    usuarios: Map<String, Usuario>,
    misPredicciones: Map<String, String>,
    esAdmin: Boolean,
    yaParticipa: Boolean,
    onPredecir: (String, String) -> Unit,
    onSeleccionarGanador: (Enfrentamiento, String) -> Unit
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
                .padding(20.dp)
        ) {

            Text(
                text = "Bracket",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            HorizontalDivider()

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Card(
                colors = CardDefaults.cardColors(
                    containerColor =
                        MaterialTheme.colorScheme.surfaceVariant
                )
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    if (torneo.enfrentamientos.isEmpty()) {

                        Text(
                            "El torneo todavía no fue iniciado"
                        )

                    } else {

                        val enfrentamientosPorRonda =
                            torneo.enfrentamientos.groupBy { it.ronda }
                                .toSortedMap()

                        enfrentamientosPorRonda.forEach { (ronda, enfrentamientos) ->

                            Text(
                                text = "🏁 Ronda $ronda",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(Modifier.height(8.dp))

                            Column(
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {

                                enfrentamientos.forEachIndexed { index: Int, enfrentamiento: Enfrentamiento ->

                                    val partida = partidas.firstOrNull {
                                        it.ronda == enfrentamiento.ronda &&
                                                it.participantes.contains(enfrentamiento.jugador1) &&
                                                it.participantes.contains(enfrentamiento.jugador2)
                                    }

                                    EnfrentamientoCard(
                                        enfrentamiento = enfrentamiento,
                                        jugador1Id = enfrentamiento.jugador1,
                                        jugador2Id = enfrentamiento.jugador2,
                                        ganadorId = enfrentamiento.ganador,
                                        usuarios = usuarios,
                                        esAdmin = esAdmin,
                                        yaParticipa = yaParticipa,
                                        miPrediccion = partida?.let { misPredicciones[it.id] },
                                        partidaId = partida?.id,
                                        onPredecir = onPredecir,
                                        onSeleccionarGanador = onSeleccionarGanador
                                    )
                                }
                            }

                            Spacer(Modifier.height(16.dp))
                        }
                    }

                    Text(
                        "🏆 Ganadores avanzan a la siguiente ronda",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}