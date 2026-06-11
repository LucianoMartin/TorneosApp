package com.tpgrupal.appsmoviles.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material3.*
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tpgrupal.appsmoviles.data.model.Torneo
import com.tpgrupal.appsmoviles.data.repository.TorneoRepository
import com.tpgrupal.appsmoviles.data.model.Usuario
import com.tpgrupal.appsmoviles.data.repository.UsuarioRepository
import com.tpgrupal.appsmoviles.ui.components.ListaItem
import com.tpgrupal.appsmoviles.data.cloudinary.CloudinaryUploader
import kotlinx.coroutines.launch
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    onVolver: () -> Unit,
    onCerrarSesion: () -> Unit,
    onFavoritosClick: () -> Unit,
    onMisTorneosClick: () -> Unit,
    onMisPartidasClick: () -> Unit
) {

    val usuario = Firebase.auth.currentUser
    var usuarioFirestore by remember { mutableStateOf<Usuario?>(null) }

    val context = LocalContext.current
    CloudinaryUploader.init(context)

    val scope = rememberCoroutineScope()

    var nuevoNombre by remember {
        mutableStateOf("")
    }

    var mostrarDialogoNombre by remember {
        mutableStateOf(false)
    }

    var avatarUrl by remember {
        mutableStateOf("")
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->

        if (uri != null && usuario != null) {

            scope.launch {

                try {

                    val url =
                        CloudinaryUploader.uploadImage(uri)

                    android.util.Log.d(
                        "AVATAR",
                        "URL Cloudinary: $url"
                    )

                    UsuarioRepository()
                        .actualizarAvatar(
                            usuario.uid,
                            url
                        )

                    avatarUrl = url

                } catch (e: Exception) {

                    android.util.Log.e(
                        "AVATAR",
                        "Error subiendo avatar",
                        e
                    )
                }
            }
        }
    }

    var torneos by remember {
        mutableStateOf<List<Torneo>>(emptyList())
    }

    LaunchedEffect(Unit) {

        torneos = TorneoRepository().obtenerTorneos()
        usuario?.let {

            usuarioFirestore =
                UsuarioRepository()
                    .obtenerUsuario(it.uid)

            avatarUrl =
                usuarioFirestore?.avatarUrl ?: ""
        }

        usuario?.let {

            val repo = UsuarioRepository()

            val usuarioExistente =
                repo.obtenerUsuario(it.uid)

            if (usuarioExistente == null) {

                repo.crearUsuario(
                    Usuario(
                        uid = it.uid,
                        nombre = it.email
                            ?.substringBefore("@")
                            ?.replaceFirstChar { c -> c.uppercase() }
                            ?: "Jugador",
                        email = it.email ?: "",
                        puntos = 0
                    )
                )
            }
        }

        if (!mostrarDialogoNombre) {

            nuevoNombre =
                usuarioFirestore?.nombre ?: ""
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
        usuarioFirestore?.nombre
            ?: usuario?.email
                ?.substringBefore("@")
                ?.replaceFirstChar { it.uppercase() }
            ?: "Jugador"

    Scaffold(

        topBar = {

            TopAppBar(
                title = {
                    Text("Mi Perfil")
                },

                navigationIcon = {

                    IconButton(
                        onClick = onVolver
                    ) {

                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }

    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),

            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if (avatarUrl.isNotBlank()) {

                AsyncImage(
                    model = avatarUrl,
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

            } else {

                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier.size(120.dp)
                )
            }

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

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = {
                    launcher.launch("image/*")
                }
            ) {
                Text("Cambiar foto")
            }

            OutlinedButton(
                onClick = {
                    nuevoNombre = nombreUsuario
                    mostrarDialogoNombre = true
                }
            ) {
                Text("Cambiar nombre")
            }

            if (mostrarDialogoNombre) {

                AlertDialog(

                    onDismissRequest = {
                        mostrarDialogoNombre = false
                    },

                    title = {
                        Text("Cambiar nombre")
                    },

                    text = {

                        OutlinedTextField(
                            value = nuevoNombre,
                            onValueChange = {
                                nuevoNombre = it
                            },
                            label = {
                                Text("Nombre")
                            }
                        )
                    },

                    confirmButton = {

                        TextButton(

                            onClick = {

                                usuario?.let { user ->

                                    scope.launch {

                                        UsuarioRepository()
                                            .actualizarNombre(
                                                user.uid,
                                                nuevoNombre
                                            )

                                        usuarioFirestore =
                                            usuarioFirestore?.copy(
                                                nombre = nuevoNombre
                                            )
                                    }
                                }

                                mostrarDialogoNombre = false
                            }
                        ) {

                            Text("Guardar")
                        }
                    },

                    dismissButton = {

                        TextButton(
                            onClick = {
                                mostrarDialogoNombre = false
                            }
                        ) {
                            Text("Cancelar")
                        }
                    }
                )
            }

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

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = onMisTorneosClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Icon(
                            Icons.Default.EmojiEvents,
                            contentDescription = null
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text("Mis torneos")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = onMisPartidasClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Icon(
                            Icons.Default.SportsEsports,
                            contentDescription = null
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text("Mis partidas")
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

                    ListaItem(
                        Icons.Default.EmojiEvents,
                        "Torneos creados: $cantidadCreados"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    ListaItem(
                        Icons.Default.Favorite,
                        "Favoritos: $cantidadFavoritos"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    ListaItem(
                        Icons.Default.SportsEsports,
                        "Participaciones: $cantidadParticipaciones"
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text(
                    text = "💰 ${usuarioFirestore?.puntos ?: 0} puntos",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onCerrarSesion,
                modifier = Modifier.fillMaxWidth()
            ) {

                Icon(
                    Icons.AutoMirrored.Filled.Logout,
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