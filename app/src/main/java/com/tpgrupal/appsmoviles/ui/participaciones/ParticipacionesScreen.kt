package com.tpgrupal.appsmoviles.ui.participaciones

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.tpgrupal.appsmoviles.ui.components.AppToolbar
import com.tpgrupal.appsmoviles.ui.components.BottomNavBar
import com.tpgrupal.appsmoviles.ui.home.TorneoCard
import com.tpgrupal.appsmoviles.ui.navigation.LocalNavController

@Composable
fun ParticipacionesScreen(
    viewModel: ParticipacionesViewModel = viewModel(),
    onTorneoClick: (String) -> Unit,
    onPerfilClick: () -> Unit
) {

    val navController =
        LocalNavController.current

    val torneosActivos by
    viewModel.torneosActivos.collectAsState()

    val torneosFinalizados by
    viewModel.torneosFinalizados.collectAsState()

    val usuarioId =
        Firebase.auth.currentUser?.uid ?: ""

    Scaffold(

        topBar = {

            AppToolbar(
                titulo = "Participaciones",
                onPerfilClick = onPerfilClick
            )
        },
        bottomBar = {

            BottomNavBar(
                navController = navController,
                selectedIndex = 1
            )
        }

    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),

            contentPadding = PaddingValues(16.dp),

            verticalArrangement =
                Arrangement.spacedBy(16.dp)
        ) {

            item {

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.SportsEsports,
                        contentDescription = null,
                        tint =
                            MaterialTheme.colorScheme.primary
                    )

                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )

                    Text(
                        text = "Torneos Activos",
                        style =
                            MaterialTheme.typography.titleLarge
                    )
                }
            }

            if (torneosActivos.isEmpty()) {

                item {

                    Card {

                        Text(
                            text =
                                "Todavía no participás en ningún torneo activo",
                            modifier =
                                Modifier.padding(16.dp)
                        )
                    }
                }

            } else {

                items(torneosActivos) { torneo ->

                    TorneoCard(

                        torneo = torneo,

                        onClick = {
                            onTorneoClick(
                                torneo.id
                            )
                        },

                        onFavoritoClick = {

                            viewModel.toggleFavorito(
                                torneo,
                                usuarioId
                            )
                        }
                    )
                }
            }

            item {

                Spacer(
                    modifier = Modifier.height(8.dp)
                )
            }

            item {

                HorizontalDivider()
            }

            item {

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Description,
                        contentDescription = null,
                        tint =
                            MaterialTheme.colorScheme.primary
                    )

                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )

                    Text(
                        text = "Historial",
                        style =
                            MaterialTheme.typography.titleLarge
                    )
                }
            }

            if (torneosFinalizados.isEmpty()) {

                item {

                    Card {

                        Text(
                            text =
                                "No hay torneos finalizados",
                            modifier =
                                Modifier.padding(16.dp)
                        )
                    }
                }

            } else {

                items(torneosFinalizados) { torneo ->

                    TorneoCard(

                        torneo = torneo,

                        onClick = {
                            onTorneoClick(
                                torneo.id
                            )
                        },

                        onFavoritoClick = {

                            viewModel.toggleFavorito(
                                torneo,
                                usuarioId
                            )
                        }
                    )
                }
            }
        }
    }
}