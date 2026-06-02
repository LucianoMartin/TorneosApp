package com.tpgrupal.appsmoviles.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tpgrupal.appsmoviles.data.model.Torneo
import com.tpgrupal.appsmoviles.data.repository.TorneoRepository
import com.tpgrupal.appsmoviles.data.model.Usuario
import com.tpgrupal.appsmoviles.data.repository.UsuarioRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    onVolver: () -> Unit,
    onCerrarSesion: () -> Unit,
    onFavoritosClick: () -> Unit
) {

    val usuario = Firebase.auth.currentUser

    var torneos by remember {
        mutableStateOf<List<Torneo>>(emptyList())
    }

    LaunchedEffect(Unit) {

        torneos = TorneoRepository().obtenerTorneos()

        usuario?.let {

            UsuarioRepository().crearUsuario(
                Usuario(
                    uid = it.uid,

                    nombre = it.email
                        ?.substringBefore("@")
                        ?.replaceFirstChar { c -> c.uppercase() }
                        ?: "Jugador",

                    email = it.email ?: ""
                )
            )
        }
    }

    val uid = usuario?.uid ?: ""

    val cantidadFavoritos =
        torneos.count {
            it.favoritos.contains(uid)
        }

    val cantidadCreados =
        torneos.count {
            it.creadorId == uid
        }

    val cantidadParticipaciones =
        torneos.count {
            it.participantes.contains(uid)
        }

    val nombreUsuario =
        usuario?.email
            ?.substringBefore("@")
            ?.replaceFirstChar { it.uppercase() }
            ?: "Jugador"

    Scaffold(

        topBar = {

            TopAppBar(
                title = {
                    Text("Mi Perfil")
                }
            )
        }

    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(20.dp),

            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = nombreUsuario,
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = usuario?.email ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            ElevatedCard(
                modifier = Modifier.fillMaxWidth()
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),

                    horizontalArrangement =
                        Arrangement.SpaceEvenly
                ) {

                    EstadisticaItem(
                        valor = cantidadFavoritos.toString(),
                        titulo = "Favoritos"
                    )

                    EstadisticaItem(
                        valor = cantidadCreados.toString(),
                        titulo = "Torneos"
                    )

                    EstadisticaItem(
                        valor = cantidadParticipaciones.toString(),
                        titulo = "Partidas"
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            ElevatedCard(
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Text(
                        text = "Acciones rápidas",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onFavoritosClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = null
                        )

                        Spacer(
                            modifier = Modifier.width(8.dp)
                        )

                        Text("Mis favoritos")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            ElevatedCard(
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Text(
                        text = "Logros",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("🏆 Torneos creados: $cantidadCreados")
                    Text("❤️ Favoritos obtenidos: $cantidadFavoritos")
                    Text("🎮 Participaciones: $cantidadParticipaciones")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onCerrarSesion,
                modifier = Modifier.fillMaxWidth()
            ) {

                Icon(
                    Icons.Default.Logout,
                    contentDescription = null
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Text("Cerrar sesión")
            }
        }
    }
}

@Composable
private fun EstadisticaItem(
    valor: String,
    titulo: String
) {

    Column(
        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Text(
            text = valor,
            style =
                MaterialTheme.typography.headlineSmall
        )

        Text(
            text = titulo
        )
    }
}