package com.tpgrupal.appsmoviles.ui.tienda

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.tpgrupal.appsmoviles.R
import com.tpgrupal.appsmoviles.ui.components.AppToolbar
import com.tpgrupal.appsmoviles.ui.components.BottomNavBar
import com.tpgrupal.appsmoviles.ui.navigation.LocalNavController

@Composable
fun TiendaScreen(
    viewModel: TiendaViewModel = viewModel(),
    onPerfilClick: () -> Unit
) {

    val navController =
        LocalNavController.current

    val uid = Firebase.auth.currentUser?.uid

    LaunchedEffect(uid) {
        if (uid != null) {
            viewModel.cargarDatos(uid)
        }
    }

    val premios by viewModel.premios.collectAsState()
    val usuario by viewModel.usuario.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()
    val loading by viewModel.loading.collectAsState()

    if (mensaje != null) {
        AlertDialog(
            onDismissRequest = { viewModel.limpiarMensaje() },
            confirmButton = {
                TextButton(onClick = { viewModel.limpiarMensaje() }) {
                    Text("OK")
                }
            },
            title = { Text("Tienda") },
            text = { Text(mensaje ?: "") }
        )
    }

    Scaffold(

        topBar = {

            AppToolbar(
                titulo = "Tienda",
                onPerfilClick = onPerfilClick
            )
        },
        bottomBar = {

            BottomNavBar(
                navController = navController,
                selectedIndex = 3
            )
        }

    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                contentPadding = PaddingValues(
                    top = 60.dp,
                    bottom = 80.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                items(premios) { premio ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {

                        Column {

                            Box {

                                Image(
                                    painter = painterResource(
                                        id = obtenerImagenPremio(premio.titulo)
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .height(140.dp)
                                        .fillMaxWidth(),
                                    contentScale = ContentScale.Crop
                                )

                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .background(
                                            androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.25f)
                                        )
                                )
                            }

                            Column(
                                modifier = Modifier.padding(12.dp)
                            ) {

                                Text(
                                    text = premio.titulo,
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = premio.descripcion,
                                    style = MaterialTheme.typography.bodySmall
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = "💰 ${premio.costoPuntos} pts",
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Button(
                                    onClick = { viewModel.comprarPremio(premio) },
                                    enabled = (usuario?.puntos ?: 0) >= premio.costoPuntos,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Comprar")
                                }
                            }
                        }
                    }
                }
            }

            // Puntos
            Card(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 12.dp)
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.outline,
                        RoundedCornerShape(16.dp)
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text(
                    text = "💰 ${usuario?.puntos ?: 0} puntos",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            if (loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

private fun obtenerImagenPremio(tituloPremio: String): Int {

    return when (tituloPremio) {

        "Teclado Mecánico RGB" -> com.tpgrupal.appsmoviles.R.drawable.teclado
        "Mouse Gamer Ultra Light" -> com.tpgrupal.appsmoviles.R.drawable.mouse
        "Headset 7.1 Surround" -> com.tpgrupal.appsmoviles.R.drawable.headset
        "Mousepad XL RGB" -> com.tpgrupal.appsmoviles.R.drawable.mousepad
        "Gift Card Steam" -> com.tpgrupal.appsmoviles.R.drawable.giftcard

        else -> R.drawable.ic_launcher_background
    }
}