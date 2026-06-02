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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tpgrupal.appsmoviles.ui.components.AppToolbar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onCrearTorneo: () -> Unit,
    onTorneoClick: (String) -> Unit
) {

    val torneos by viewModel.torneos.collectAsState()

    Scaffold(

        topBar = {

            AppToolbar(
                titulo = "Torneos"
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

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            if (torneos.isEmpty()) {

                item {

                    Box(
                        modifier = Modifier
                            .fillParentMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            "No hay torneos disponibles"
                        )
                    }
                }
            }

            items(torneos) { torneo ->

                TorneoCard(
                    torneo = torneo,
                    onClick = {
                        onTorneoClick(torneo.id)
                    }
                )
            }
        }
    }
}