package com.tpgrupal.appsmoviles.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tpgrupal.appsmoviles.data.model.Torneo
import com.tpgrupal.appsmoviles.data.repository.TorneoRepository
import com.tpgrupal.appsmoviles.ui.home.TorneoCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritosScreen(
    onVolver: () -> Unit,
    onTorneoClick: (String) -> Unit
) {

    var favoritos by remember {
        mutableStateOf<List<Torneo>>(emptyList())
    }

    LaunchedEffect(Unit) {

        val uid =
            Firebase.auth.currentUser?.uid
                ?: return@LaunchedEffect

        val torneos =
            TorneoRepository().obtenerTorneos()

        favoritos =
            torneos.filter {
                it.favoritos.contains(uid)
            }
    }

    Scaffold(

        topBar = {

            TopAppBar(

                title = {
                    Text("Mis Favoritos")
                },

                navigationIcon = {

                    IconButton(
                        onClick = onVolver
                    ) {

                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        }

    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {

            if (favoritos.isEmpty()) {

                item {

                    Text(
                        text = "No tenés torneos favoritos",
                        modifier = Modifier.padding(20.dp)
                    )
                }
            }

            items(favoritos) { torneo ->

                TorneoCard(
                    torneo = torneo,

                    onClick = {
                        onTorneoClick(torneo.id)
                    },

                    onFavoritoClick = {

                    }
                )
            }
        }
    }
}