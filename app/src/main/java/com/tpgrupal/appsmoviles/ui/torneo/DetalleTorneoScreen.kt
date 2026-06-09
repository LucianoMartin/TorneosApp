package com.tpgrupal.appsmoviles.ui.torneo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tpgrupal.appsmoviles.data.model.Torneo
import com.tpgrupal.appsmoviles.data.model.enums.EstadoTorneo
import com.tpgrupal.appsmoviles.data.repository.JuegoRepository
import com.tpgrupal.appsmoviles.data.repository.TorneoRepository
import com.tpgrupal.appsmoviles.ui.theme.ErrorRed
import com.tpgrupal.appsmoviles.ui.theme.NeonBlue
import com.tpgrupal.appsmoviles.ui.theme.SuccessGreen
import com.tpgrupal.appsmoviles.ui.theme.TextPrimary
import com.tpgrupal.appsmoviles.ui.utils.textoAmigable
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.tpgrupal.appsmoviles.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import com.tpgrupal.appsmoviles.data.repository.UsuarioRepository
import com.tpgrupal.appsmoviles.ui.components.MapaTorneo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleTorneoScreen(
    torneoId: String,
    onVolver: () -> Unit
) {

    val torneoRepository = remember {
        TorneoRepository()
    }

    val juegoRepository = remember {
        JuegoRepository()
    }

    val usuarioRepository = remember {
        UsuarioRepository()
    }

    var participantesInfo by remember {
        mutableStateOf<Map<String, String>>(emptyMap())
    }

    var solicitudesInfo by remember {
        mutableStateOf<Map<String, String>>(emptyMap())
    }

    var torneo by remember {
        mutableStateOf<Torneo?>(null)
    }

    var nombreJuego by remember {
        mutableStateOf("")
    }

    val scope = rememberCoroutineScope()

    val usuarioId =
        Firebase.auth.currentUser?.uid ?: ""

    LaunchedEffect(Unit) {

        torneo =
            torneoRepository.obtenerTorneoPorId(
                torneoId
            )

        torneo?.let {

            val juego =
                juegoRepository.obtenerJuegoPorId(
                    it.juegoId
                )

            nombreJuego =
                juego?.nombre ?: "Juego"
            val mapaParticipantes =
                mutableMapOf<String, String>()

            it.participantes.forEach { uid ->

                val usuario =
                    usuarioRepository.obtenerUsuario(uid)

                mapaParticipantes[uid] =
                    usuario?.nombre
                        ?.takeIf { nombre ->
                            nombre.isNotBlank()
                        }
                        ?: uid
            }

            participantesInfo =
                mapaParticipantes
        }
    }

    Scaffold(

        topBar = {

            TopAppBar(
                title = {
                    Text("Torneo")
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

        if (torneo == null) {

            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {

                CircularProgressIndicator()
            }

            return@Scaffold
        }

        val t = torneo!!

        val yaParticipa =
            t.participantes.contains(usuarioId)

        val yaSolicito =
            t.solicitudes.contains(usuarioId)

        val esCreador =
            t.creadorId == usuarioId

        val esModerador =
            t.moderadores.contains(usuarioId)

        val esAdmin =
            esCreador || esModerador

        val torneoCompleto =
            t.participantes.size >= t.maxParticipantes

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            item {

                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.elevatedCardElevation(
                        defaultElevation = 8.dp
                    )
                ) {

                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(240.dp)
                        ) {

                            Image(
                                painter = painterResource(
                                    id = obtenerImagenJuego(t.juegoId)
                                ),
                                contentDescription = null,

                                modifier = Modifier.fillMaxSize(),

                                contentScale = ContentScale.Crop
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                Color.Black.copy(alpha = 0.85f)
                                            )
                                        )
                                    )
                            )

                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(20.dp)
                            ) {

                                Text(
                                    text = t.nombre,
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = Color.White
                                )

                                Spacer(
                                    modifier = Modifier.height(4.dp)
                                )

                                Text(
                                    text = nombreJuego,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {


                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {

                                AssistChip(
                                    onClick = {},

                                    colors = AssistChipDefaults.assistChipColors(

                                        containerColor = when (t.estado) {

                                            EstadoTorneo.INSCRIPCION ->
                                                SuccessGreen

                                            EstadoTorneo.EN_CURSO ->
                                                NeonBlue

                                            EstadoTorneo.FINALIZADO ->
                                                ErrorRed
                                        },

                                        labelColor = TextPrimary
                                    ),

                                    label = {
                                        Text(
                                            t.estado.textoAmigable()
                                        )
                                    }
                                )

                                AssistChip(
                                    onClick = {},
                                    label = {
                                        Text(t.ciudad)
                                    }
                                )
                            }

                            Spacer(
                                modifier = Modifier.height(16.dp)
                            )

                            Row {

                                Icon(
                                    Icons.Default.SportsEsports,
                                    contentDescription = null
                                )

                                Spacer(
                                    modifier = Modifier.width(8.dp)
                                )

                                Text(nombreJuego)
                            }

                            Spacer(
                                modifier = Modifier.height(8.dp)
                            )

                            Row {

                                Icon(
                                    Icons.Default.LocationOn,
                                    contentDescription = null
                                )

                                Spacer(
                                    modifier = Modifier.width(8.dp)
                                )

                                Text(t.ciudad)
                            }

                            Spacer(
                                modifier = Modifier.height(8.dp)
                            )

                            Row {

                                Icon(
                                    Icons.Default.Groups,
                                    contentDescription = null
                                )

                                Spacer(
                                    modifier = Modifier.width(8.dp)
                                )

                                Text(
                                    "${t.participantes.size}/${t.maxParticipantes} participantes"
                                )
                            }
                        }
                    }
                }            }

            item {

                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.elevatedCardElevation(
                        defaultElevation = 8.dp
                    )
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {

                        Text(
                            text = "Descripción",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )

                        HorizontalDivider()

                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )

                        Text(
                            t.descripcion.ifBlank { "Sin descripción" }
                        )
                    }
                }
            }

            item {

                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.elevatedCardElevation(
                        defaultElevation = 8.dp
                    )
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {

                        Text(
                            text = "Requisitos",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )

                        HorizontalDivider()

                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )

                        Text(
                            t.requisitos.ifBlank { "Sin requisitos" }
                        )
                    }
                }
            }

            item {

                ElevatedCard(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        Text(
                            text = "Ubicación",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )

                        HorizontalDivider()

                        MapaTorneo(
                            context = LocalContext.current,
                            latitud = t.latitud,
                            longitud = t.longitud
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )

                            Spacer(
                                modifier = Modifier.width(6.dp)
                            )

                            Text(
                                text = "Ciudad: ${t.ciudad}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        Text(
                            text = "Latitud: %.5f  |  Longitud: %.5f"
                                .format(
                                    t.latitud,
                                    t.longitud
                                ),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {

                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.elevatedCardElevation(
                        defaultElevation = 8.dp
                    )
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {

                        Text(
                            text = "Participantes",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )

                        HorizontalDivider()

                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )

                        if (t.participantes.isEmpty()) {

                            Text(
                                "Todavía no hay participantes"
                            )

                        } else {

                            val usuarioRepository = UsuarioRepository()

                            var nombresParticipantes by remember {
                                mutableStateOf<Map<String, String>>(emptyMap())
                            }

                            LaunchedEffect(t.participantes) {

                                val mapa = mutableMapOf<String, String>()

                                t.participantes.forEach { uid ->

                                    val usuario =
                                        usuarioRepository.obtenerUsuario(uid)

                                    mapa[uid] =
                                        usuario?.nombre
                                            ?.takeIf { it.isNotBlank() }
                                            ?: uid
                                }

                                nombresParticipantes = mapa
                            }

                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {

                                t.participantes.forEach { uid ->

                                    AssistChip(
                                        onClick = {},
                                        label = {
                                            Text(
                                                nombresParticipantes[uid] ?: uid
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {

                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.elevatedCardElevation(
                        defaultElevation = 8.dp
                    )
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {

                        Text(
                            text = "Bracket",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )

                        HorizontalDivider()

                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor =
                                    MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {

                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {

                                if (t.enfrentamientos.isEmpty()) {

                                    Text(
                                        "El torneo todavía no fue iniciado"
                                    )

                                } else {

                                    t.enfrentamientos.forEachIndexed { index, enfrentamiento ->

                                        val jugador1 =
                                            participantesInfo[enfrentamiento.jugador1]
                                                ?: enfrentamiento.jugador1

                                        val jugador2 =
                                            participantesInfo[enfrentamiento.jugador2]
                                                ?: enfrentamiento.jugador2

                                        Text(
                                            "🎮 $jugador1 vs $jugador2"
                                        )
                                        if (
                                            esAdmin &&
                                            enfrentamiento.ganador.isBlank()
                                        ) {

                                            Spacer(
                                                modifier = Modifier.height(8.dp)
                                            )

                                            Row(
                                                horizontalArrangement =
                                                    Arrangement.spacedBy(8.dp)
                                            ) {

                                                Button(
                                                    onClick = {

                                                        scope.launch {

                                                            torneoRepository
                                                                .seleccionarGanador(
                                                                    torneoId = t.id,
                                                                    indiceEnfrentamiento = index,
                                                                    ganadorUid =
                                                                        enfrentamiento.jugador1
                                                                )

                                                            torneo =
                                                                torneoRepository
                                                                    .obtenerTorneoPorId(
                                                                        t.id
                                                                    )
                                                        }
                                                    }
                                                ) {
                                                    Text("Ganó $jugador1")
                                                }

                                                Button(
                                                    onClick = {

                                                        scope.launch {

                                                            torneoRepository
                                                                .seleccionarGanador(
                                                                    torneoId = t.id,
                                                                    indiceEnfrentamiento = index,
                                                                    ganadorUid =
                                                                        enfrentamiento.jugador2
                                                                )

                                                            torneo =
                                                                torneoRepository
                                                                    .obtenerTorneoPorId(
                                                                        t.id
                                                                    )
                                                        }
                                                    }
                                                ) {
                                                    Text("Ganó $jugador2")
                                                }
                                            }
                                        }

                                        if (enfrentamiento.ganador.isNotBlank()) {

                                            Spacer(
                                                modifier = Modifier.height(4.dp)
                                            )

                                            Text(
                                                "🏆 Ganador: ${
                                                    participantesInfo[enfrentamiento.ganador]
                                                        ?: enfrentamiento.ganador
                                                }"
                                            )
                                        }

                                        Spacer(
                                            modifier = Modifier.height(12.dp)
                                        )

                                        HorizontalDivider()

                                        Spacer(
                                            modifier = Modifier.height(12.dp)
                                        )
                                    }
                                }

                                Text(
                                    "🏆 Ganadores avanzan a la siguiente ronda",
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
            item {

                if (esAdmin && t.solicitudes.isNotEmpty()) {

                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.elevatedCardElevation(
                            defaultElevation = 8.dp
                        )
                    ) {

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {

                            Text(
                                text = "Solicitudes pendientes",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(
                                modifier = Modifier.height(12.dp)
                            )

                            t.solicitudes.forEach { uid ->

                                var nombreUsuario by remember(uid) {
                                    mutableStateOf(uid)
                                }

                                LaunchedEffect(uid) {

                                    val usuario =
                                        usuarioRepository.obtenerUsuario(uid)

                                    nombreUsuario =
                                        usuario?.nombre
                                            ?.takeIf { it.isNotBlank() }
                                            ?: uid
                                }

                                Column(
                                    modifier = Modifier.fillMaxWidth()
                                ) {

                                    Text(
                                        text = nombreUsuario,
                                        style = MaterialTheme.typography.titleMedium
                                    )

                                    Spacer(
                                        modifier = Modifier.height(8.dp)
                                    )

                                    Row {

                                        Button(
                                            onClick = {

                                                scope.launch {

                                                    torneoRepository
                                                        .aceptarSolicitud(
                                                            torneoId = t.id,
                                                            usuarioId = uid
                                                        )

                                                    torneo =
                                                        torneoRepository
                                                            .obtenerTorneoPorId(
                                                                t.id
                                                            )
                                                }
                                            }
                                        ) {
                                            Text("Aceptar")
                                        }

                                        Spacer(
                                            modifier = Modifier.width(8.dp)
                                        )

                                        OutlinedButton(
                                            onClick = {

                                                scope.launch {

                                                    torneoRepository
                                                        .rechazarSolicitud(
                                                            torneoId = t.id,
                                                            usuarioId = uid
                                                        )

                                                    torneo =
                                                        torneoRepository
                                                            .obtenerTorneoPorId(
                                                                t.id
                                                            )
                                                }
                                            }
                                        ) {
                                            Text("Rechazar")
                                        }
                                    }

                                    Spacer(
                                        modifier = Modifier.height(12.dp)
                                    )

                                    HorizontalDivider()

                                    Spacer(
                                        modifier = Modifier.height(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {

                if (
                    esAdmin &&
                    t.estado == EstadoTorneo.INSCRIPCION
                ) {

                    Button(

                        onClick = {

                            scope.launch {

                                torneoRepository.iniciarTorneo(
                                    t.id
                                )

                                torneo =
                                    torneoRepository.obtenerTorneoPorId(
                                        t.id
                                    )
                            }
                        },

                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)

                    ) {

                        Text(
                            "Iniciar torneo"
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )
                }
            }

            item {

                FilledTonalButton(

                    enabled =
                        !yaParticipa &&
                                !yaSolicito &&
                                !torneoCompleto &&
                                t.estado == EstadoTorneo.INSCRIPCION,

                    onClick = {

                        scope.launch {

                            torneoRepository.solicitarParticipacion(
                                torneoId = t.id,
                                usuarioId = usuarioId
                            )

                            torneo =
                                torneoRepository.obtenerTorneoPorId(
                                    t.id
                                )
                        }
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),

                    shape = MaterialTheme.shapes.large
                ) {

                    Text(

                        text = when {

                            yaParticipa ->
                                "Ya estás participando"

                            yaSolicito ->
                                "Solicitud enviada"

                            torneoCompleto ->
                                "Cupos completos"

                            else ->
                                "Solicitar participación"
                        },

                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }

    }

}
private fun obtenerImagenJuego(juegoId: String): Int {

    return when (juegoId.lowercase()) {

        "cs2" -> R.drawable.cs2
        "fifa" -> R.drawable.fifa
        "fortnite" -> R.drawable.fortnite
        "lol" -> R.drawable.lol
        "mario_kart" -> R.drawable.mario_kart
        "minecraft" -> R.drawable.minecraft
        "rocketleague" -> R.drawable.rocketleague
        "smash" -> R.drawable.smash
        "valorant" -> R.drawable.valorant

        else -> R.drawable.ic_launcher_background
    }
}