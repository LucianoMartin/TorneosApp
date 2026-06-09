package com.tpgrupal.appsmoviles.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tpgrupal.appsmoviles.ui.components.AppToolbar
import com.tpgrupal.appsmoviles.ui.components.BottomNavBar
import com.tpgrupal.appsmoviles.ui.navigation.LocalNavController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onCrearTorneo: () -> Unit,
    onTorneoClick: (String) -> Unit,
    onPerfilClick: () -> Unit
) {

    val navController =
        LocalNavController.current

    val torneos by viewModel.torneos.collectAsState()

    var busqueda by remember {
        mutableStateOf("")
    }

    val torneosFiltrados = torneos.filter {
        it.nombre.contains(
            busqueda,
            ignoreCase = true
        )
    }
    Scaffold(

        topBar = {

            AppToolbar(
                titulo = "Torneos",
                onPerfilClick = onPerfilClick
            )
        },

        bottomBar = {

            BottomNavBar(
                navController = navController,
                selectedIndex = 0
            )
        },

        floatingActionButton = {

            FloatingActionButton(
                onClick = onCrearTorneo
            ) {

                Icon(
                    Icons.Default.Add,
                    null
                )
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {

            OutlinedTextField(
                value = busqueda,
                onValueChange = { busqueda = it },
                label = { Text("Buscar torneo") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                singleLine = true
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    top = 8.dp,
                    bottom = 80.dp
                )
            ) {

                if (torneosFiltrados.isEmpty()) {

                    item {

                        Box(
                            modifier = Modifier.fillParentMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {

                            Text("No hay torneos disponibles")
                        }
                    }
                }

                items(torneosFiltrados) { torneo ->

                    TorneoCard(
                        torneo = torneo,

                        onClick = {
                            println("ID TORNEO = ${torneo.id}")
                            onTorneoClick(torneo.id)
                        },

                        onFavoritoClick = {

                            val usuarioId =
                                Firebase.auth.currentUser?.uid
                                    ?: return@TorneoCard

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