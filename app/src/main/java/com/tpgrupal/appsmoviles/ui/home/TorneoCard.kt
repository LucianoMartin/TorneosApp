package com.tpgrupal.appsmoviles.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tpgrupal.appsmoviles.data.model.Torneo

@Composable
fun TorneoCard(
    torneo: Torneo
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                torneo.nombre,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(4.dp))

            Text(torneo.juego)

            Spacer(Modifier.height(4.dp))

            Text(torneo.ciudad)

            Spacer(Modifier.height(4.dp))

            Text(
                "${torneo.participantes.size}/${torneo.maxParticipantes} participantes"
            )
        }
    }
}