package com.tpgrupal.appsmoviles.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tpgrupal.appsmoviles.data.model.Torneo

@Composable
fun TorneoCard(
    torneo: Torneo,
    onClick: () -> Unit,
    onFavoritoClick: () -> Unit
) {

    val usuarioId =
        Firebase.auth.currentUser?.uid ?: ""

    val esFavorito =
        torneo.favoritos.contains(usuarioId)

    ElevatedCard(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 12.dp,
                vertical = 6.dp
            )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween,
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )

                    Text(
                        text = torneo.nombre,
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                IconButton(
                    onClick = onFavoritoClick
                ) {

                    Icon(
                        imageVector =
                            if (esFavorito)
                                Icons.Default.Favorite
                            else
                                Icons.Outlined.FavoriteBorder,

                        contentDescription = "Favorito",

                        tint =
                            if (esFavorito)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Row {

                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )

                Spacer(
                    modifier = Modifier.width(6.dp)
                )

                Text(torneo.ciudad)
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Row {

                Icon(
                    imageVector = Icons.Default.Groups,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )

                Spacer(
                    modifier = Modifier.width(6.dp)
                )

                Text(
                    "${torneo.participantes.size}/${torneo.maxParticipantes} participantes"
                )
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "❤️ ${torneo.favoritos.size}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            AssistChip(
                onClick = {},
                label = {
                    Text(torneo.estado.name)
                }
            )
        }
    }
}