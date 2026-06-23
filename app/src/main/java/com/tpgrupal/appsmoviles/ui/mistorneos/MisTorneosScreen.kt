package com.tpgrupal.appsmoviles.ui.mistorneos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.tpgrupal.appsmoviles.ui.components.AppToolbar
import com.tpgrupal.appsmoviles.ui.components.BottomNavBar
import com.tpgrupal.appsmoviles.ui.components.TorneoCard
import com.tpgrupal.appsmoviles.ui.navigation.LocalNavController

@Composable
fun MisTorneosScreen(
    viewModel: MisTorneosViewModel = viewModel(),
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
                titulo = "Mis Torneos",
                onPerfilClick = onPerfilClick
            )
        },

        bottomBar = {

            BottomNavBar(
                navController = navController,
                selectedIndex = 2
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
                            Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint =
                            MaterialTheme.colorScheme.primary
                    )

                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )

                    Text(
                        text = "Torneos que organizas",
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
                                "Todavía no creaste ningún torneo",
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
                            Icons.Default.History,
                        contentDescription = null,
                        tint =
                            MaterialTheme.colorScheme.primary
                    )

                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )

                    Text(
                        text = "Torneos organizaste",
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