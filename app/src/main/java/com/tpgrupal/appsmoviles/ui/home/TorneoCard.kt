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
import com.tpgrupal.appsmoviles.ui.utils.textoAmigable
import com.tpgrupal.appsmoviles.ui.theme.TextPrimary
import com.tpgrupal.appsmoviles.ui.utils.colorEstado
import com.tpgrupal.appsmoviles.ui.utils.iconoEstado
import com.tpgrupal.appsmoviles.ui.utils.juegoAmigable

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

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

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

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

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

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )

                Spacer(
                    modifier = Modifier.width(6.dp)
                )

                Text(
                    text = if (torneo.favoritos.size == 1)
                        "1 favorito"
                    else
                        "${torneo.favoritos.size} favoritos"
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                AssistChip(
                    onClick = {},
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    label = {
                        Text(torneo.juegoId.juegoAmigable())
                    }
                )

                AssistChip(
                    onClick = {},
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = torneo.estado.colorEstado(),
                        labelColor = TextPrimary
                    ),
                    label = {

                        Row {

                            Icon(
                                imageVector = torneo.estado.iconoEstado(),
                                contentDescription = null,
                                tint = TextPrimary,
                                modifier = Modifier.size(18.dp)
                            )

                            Spacer(
                                modifier = Modifier.width(4.dp)
                            )

                            Text(
                                torneo.estado.textoAmigable()
                            )
                        }
                    }
                )
            }
        }
    }
}