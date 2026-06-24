    package com.tpgrupal.appsmoviles.ui.torneo

    import androidx.compose.foundation.layout.*
    import androidx.compose.foundation.lazy.LazyColumn
    import androidx.compose.material.icons.Icons
    import androidx.compose.material.icons.filled.Groups
    import androidx.compose.material.icons.filled.LocationOn
    import androidx.compose.material.icons.filled.SportsEsports
    import androidx.compose.material.icons.filled.Person
    import androidx.compose.material.icons.filled.Edit
    import androidx.compose.material.icons.filled.CalendarMonth
    import androidx.compose.material.icons.filled.Favorite
    import androidx.compose.material.icons.filled.Flag
    import androidx.compose.material.icons.filled.PlayArrow
    import androidx.compose.material.icons.outlined.FavoriteBorder
    import androidx.compose.material.icons.automirrored.filled.ArrowBack
    import androidx.compose.material3.*
    import androidx.compose.runtime.*
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.unit.dp
    import com.tpgrupal.appsmoviles.data.model.enums.EstadoTorneo
    import com.tpgrupal.appsmoviles.ui.theme.TextPrimary
    import com.tpgrupal.appsmoviles.ui.utils.textoAmigable
    import androidx.compose.foundation.Image
    import androidx.compose.ui.layout.ContentScale
    import androidx.compose.ui.res.painterResource
    import com.tpgrupal.appsmoviles.R
    import androidx.compose.foundation.background
    import androidx.compose.foundation.layout.Arrangement
    import androidx.compose.ui.text.style.TextAlign
    import androidx.compose.ui.graphics.Brush
    import androidx.compose.ui.graphics.Color
    import androidx.compose.foundation.layout.FlowRow
    import androidx.compose.foundation.shape.CircleShape
    import androidx.compose.ui.platform.LocalContext
    import com.google.firebase.auth.ktx.auth
    import com.google.firebase.ktx.Firebase
    import java.text.SimpleDateFormat
    import java.util.Date
    import java.util.Locale
    import com.tpgrupal.appsmoviles.data.repository.TorneoRepository
    import com.tpgrupal.appsmoviles.data.repository.UsuarioRepository
    import com.tpgrupal.appsmoviles.ui.components.MapaTorneo
    import com.tpgrupal.appsmoviles.ui.utils.colorEstado
    import com.tpgrupal.appsmoviles.ui.utils.iconoEstado
    import coil.compose.AsyncImage
    import com.tpgrupal.appsmoviles.data.model.enums.EstadoSolicitud
    import com.tpgrupal.appsmoviles.data.repository.PartidaRepository
    import com.tpgrupal.appsmoviles.data.repository.PrediccionRepository
    import com.tpgrupal.appsmoviles.ui.components.BracketTorneoCard
    import com.tpgrupal.appsmoviles.ui.components.InfoCard
    import com.tpgrupal.appsmoviles.ui.components.ListaItem
    import com.tpgrupal.appsmoviles.ui.components.ParticipantesCard
    import com.tpgrupal.appsmoviles.ui.components.SolicitudesPendientesCard
    import com.tpgrupal.appsmoviles.ui.utils.torneoengine.EliminacionDirectaEngine
    import com.tpgrupal.appsmoviles.ui.utils.torneoengine.TorneoService

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun DetalleTorneoScreen(
        torneoId: String,
        onVolver: () -> Unit,
        onEditarTorneo: (String) -> Unit
    ) {

        val usuarioId =
            Firebase.auth.currentUser?.uid ?: ""

        val viewModel = remember {
            DetalleTorneoViewModel(
                repo = TorneoRepository(),
                service = TorneoService(
                    repo = TorneoRepository(),
                    partidaRepo = PartidaRepository(),
                    prediccionRepo = PrediccionRepository(),
                    usuarioRepo = UsuarioRepository(),
                    engine = EliminacionDirectaEngine()
                ),
                usuarioRepo = UsuarioRepository()
            )
        }

        LaunchedEffect(torneoId) {
            viewModel.cargarTorneo(torneoId)
            viewModel.cargarSolicitudes(torneoId)
            viewModel.cargarPartidas(torneoId)
        }

        val torneo = viewModel.torneo

        val esFavorito =
            torneo?.favoritos?.contains(usuarioId) == true

        if (torneo == null) {
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return
        }

        val yaParticipa =
            torneo.participantes.contains(usuarioId)

        val yaSolicito =
            viewModel.solicitudes.any {
                it.usuarioId == usuarioId &&
                        it.estado == EstadoSolicitud.PENDIENTE
            }

        val esCreador =
            torneo.creadorId == usuarioId

        val esModerador =
            torneo.moderadores.contains(usuarioId)

        val esAdmin =
            esCreador || esModerador

        val puedeIniciar =
            esAdmin &&
                    torneo.estado == EstadoTorneo.INSCRIPCION &&
                    viewModel.puedeIniciarTorneo()

        val torneoCompleto =
            torneo.participantes.size >= torneo.maxParticipantes

        val solicitudes = viewModel.solicitudes

        val solicitudesPendientes =
            solicitudes.filter {
                it.estado == EstadoSolicitud.PENDIENTE
            }

        val ganadorTorneo =
            if (torneo.estado == EstadoTorneo.FINALIZADO) {
                torneo.enfrentamientos
                    .firstOrNull { it.ganador.isNotBlank() }
                    ?.ganador
            } else null

        LaunchedEffect(torneo.creadorId) {
            viewModel.cargarUsuario(torneo.creadorId)
        }

        val nombreCreador =
            viewModel.usuarios[torneo.creadorId]?.nombre
                ?: "Desconocido"

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
            },

            floatingActionButton = {

                if (esAdmin) {

                    FloatingActionButton(

                        onClick = {
                            onEditarTorneo(torneo.id)
                        }

                    ) {

                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Editar torneo"
                        )
                    }
                }
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

            LaunchedEffect(torneo.participantes) {
                torneo.participantes.forEach {
                    viewModel.cargarUsuario(it)
                }
            }

            LaunchedEffect(torneo.id) {
                viewModel.partidas.forEach { partida ->
                    viewModel.cargarPrediccion(
                        partida.id,
                        usuarioId
                    )
                }
            }

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

                                if (torneo.imagenUrl.isNotBlank()) {

                                    AsyncImage(
                                        model = torneo.imagenUrl,
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )

                                } else {

                                    Image(
                                        painter = painterResource(
                                            id = obtenerImagenJuego(torneo.juegoId)
                                        ),
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }

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

                                Surface(
                                    onClick = {
                                        viewModel.toggleFavorito(usuarioId)
                                    },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(12.dp),
                                    shape = CircleShape,
                                    color = Color.Black.copy(alpha = 0.45f),
                                    tonalElevation = 6.dp,
                                    shadowElevation = 6.dp
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
                                                Color.White,

                                        modifier = Modifier
                                            .padding(12.dp)
                                            .size(30.dp)
                                    )
                                }

                                Column(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(20.dp)
                                ) {

                                    Text(
                                        text = torneo.nombre,
                                        style = MaterialTheme.typography.headlineMedium,
                                        color = Color.White
                                    )

                                    Spacer(
                                        modifier = Modifier.height(4.dp)
                                    )

                                    Text(
                                        text = viewModel.nombreJuego,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                }
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp)
                            ) {

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

                                Spacer(
                                    modifier = Modifier.height(16.dp)
                                )

                                ListaItem(
                                    icono = Icons.Default.SportsEsports,
                                    texto = "Juego: ${viewModel.nombreJuego}"
                                )

                                Spacer(
                                    modifier = Modifier.height(8.dp)
                                )

                                ListaItem(
                                    icono = Icons.Default.LocationOn,
                                    texto = "Ciudad: ${torneo.ciudad}"
                                )

                                Spacer(
                                    modifier = Modifier.height(8.dp)
                                )

                                ListaItem(
                                    icono = Icons.Default.Groups,
                                    texto = "Participantes: ${torneo.participantes.size}/${torneo.maxParticipantes}"
                                )

                                Spacer(
                                    modifier = Modifier.height(8.dp)
                                )

                                ListaItem(
                                    icono = Icons.Default.Person,
                                    texto = "Creador: $nombreCreador"
                                )

                                Spacer(
                                    modifier = Modifier.height(8.dp)
                                )

                                ListaItem(
                                    icono = Icons.Default.CalendarMonth,
                                    texto = "Creado: ${formatearFecha(torneo.fechaCreacion)}"
                                )

                                if (torneo.fechaInicio > 0) {

                                    Spacer(
                                        modifier = Modifier.height(8.dp)
                                    )

                                    ListaItem(
                                        icono = Icons.Default.PlayArrow,
                                        texto = "Inicio: ${formatearFecha(torneo.fechaInicio)}"
                                    )
                                }

                                if (torneo.fechaFin > 0) {

                                    Spacer(
                                        modifier = Modifier.height(8.dp)
                                    )

                                    ListaItem(
                                        icono = Icons.Default.Flag,
                                        texto = "Finalizado: ${formatearFecha(torneo.fechaFin)}"
                                    )
                                }

                                Spacer(
                                    modifier = Modifier.height(8.dp)
                                )

                                ListaItem(
                                    icono = Icons.Default.Favorite,
                                    texto = if (torneo.favoritos.size == 1)
                                        "1 favorito"
                                    else
                                        "${torneo.favoritos.size} favoritos"
                                )
                            }
                        }
                    }
                }

                item {
                    InfoCard(
                        titulo = "Descripción",
                        contenido = torneo.descripcion
                    )
                }

                item {
                    InfoCard(
                        titulo = "Requisitos",
                        contenido = torneo.requisitos
                    )
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
                                latitud = torneo.latitud,
                                longitud = torneo.longitud
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
                                    text = "Ciudad: ${torneo.ciudad}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }

                            Text(
                                text = "Latitud: %.5f  |  Longitud: %.5f"
                                    .format(
                                        torneo.latitud,
                                        torneo.longitud
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

                            if (torneo.participantes.isEmpty()) {

                                Text(
                                    "Todavía no hay participantes"
                                )

                            } else {

                                val usuarioRepository = UsuarioRepository()

                                var nombresParticipantes by remember {
                                    mutableStateOf<Map<String, String>>(emptyMap())
                                }

                                LaunchedEffect(torneo.participantes) {

                                    val mapa = mutableMapOf<String, String>()

                                    torneo.participantes.forEach { uid ->

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

                                    torneo.participantes.forEach { uid ->

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

                    BracketTorneoCard(
                        torneo = torneo,
                        partidas = viewModel.partidas,
                        usuarios = viewModel.usuarios,
                        misPredicciones = viewModel.misPredicciones,
                        esAdmin = esAdmin, // Para poder predecir tu propio torneo poner "!"
                        yaParticipa = yaParticipa,

                        onPredecir = { partidaId, ganadorId ->
                            viewModel.predecirGanador(usuarioId, partidaId, ganadorId)
                        },

                        onSeleccionarGanador = { enfrentamiento, ganadorId ->
                            viewModel.seleccionarGanador(
                                torneo.id,
                                enfrentamiento,
                                ganadorId
                            )
                        }
                    )
                }

                item {

                    if (torneo.estado == EstadoTorneo.FINALIZADO && ganadorTorneo != null) {

                        ElevatedCard(
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {

                                Text(
                                    text = "🏆 Ganador de ${torneo.nombre}",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = viewModel.usuarios[ganadorTorneo]?.nombre
                                        ?: ganadorTorneo,
                                    style = MaterialTheme.typography.headlineMedium
                                )
                            }
                        }
                    }
                }

                item {

                    if (esAdmin && torneo.estado == EstadoTorneo.INSCRIPCION) {

                        OutlinedButton(
                            onClick = { viewModel.agregarBot() },
                            enabled = torneo.participantes.size < torneo.maxParticipantes,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                when {
                                    torneo.participantes.size >= torneo.maxParticipantes ->
                                        "Cupo completo"
                                    else ->
                                        "Añadir bot"
                                }
                            )
                        }
                    }
                }

                item {

                    if (esAdmin && solicitudesPendientes.isNotEmpty()) {

                        solicitudesPendientes.forEach { sol ->
                            LaunchedEffect(sol.usuarioId) {
                                viewModel.cargarUsuario(sol.usuarioId)
                            }
                        }

                        SolicitudesPendientesCard(

                            solicitudes = solicitudes,

                            usuarios = viewModel.usuarios,

                            onAceptar = {
                                viewModel.aceptarSolicitud(torneo.id, it)
                            },

                            onRechazar = {
                                viewModel.rechazarSolicitud(
                                    torneo.id,
                                    it.usuarioId
                                )
                            }
                        )
                    }
                }

                item {

                    if (
                        esAdmin &&
                        torneo.estado == EstadoTorneo.INSCRIPCION
                    ) {
                        if (!puedeIniciar) {

                            Text(
                                text = "Se necesitan más participantes (actuales: ${torneo.participantes.size})",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        Button(
                            onClick = {
                                viewModel.iniciarTorneo(torneo.id)
                            },
                            enabled = puedeIniciar,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp)
                        ) {
                            Text(
                                text = "Iniciar torneo",
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }

                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )
                    }
                }

                item {

                    if (
                        !yaParticipa &&
                        !yaSolicito &&
                        torneo.estado == EstadoTorneo.INSCRIPCION
                    ) {

                        ParticipantesCard(
                            torneoCompleto = torneoCompleto,
                            estadoTorneo = torneo.estado,

                            onSolicitarParticipacion = { comentario ->

                                viewModel.solicitarParticipacion(
                                    torneoId = torneo.id,
                                    usuarioId = usuarioId,
                                    comentario = comentario
                                )
                            }
                        )
                    }
                }

                item {

                    when {

                        yaParticipa && torneo.estado == EstadoTorneo.INSCRIPCION -> {

                            FilledTonalButton(
                                onClick = {},
                                enabled = false,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                            ) {

                                Text(
                                    text = "✓ Ya participás en este torneo",
                                    style = MaterialTheme.typography.titleMedium,
                                )
                            }
                        }

                        yaSolicito && torneo.estado == EstadoTorneo.INSCRIPCION -> {

                            FilledTonalButton(
                                onClick = {},
                                enabled = false,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                            ) {

                                Text(
                                    text = "Solicitud pendiente de aprobación",
                                    style = MaterialTheme.typography.titleMedium,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    private fun obtenerImagenJuego(juegoId: String): Int {

        return when (juegoId.lowercase()) {

            "brawlhalla" -> R.drawable.brawlhalla
            "cs2" -> R.drawable.cs2
            "fifa" -> R.drawable.fifa
            "fortnite" -> R.drawable.fortnite
            "lol" -> R.drawable.lol
            "mario_kart" -> R.drawable.mario_kart
            "minecraft" -> R.drawable.minecraft
            "rocket_league" -> R.drawable.rocket_league
            "smash" -> R.drawable.smash
            "valorant" -> R.drawable.valorant

            else -> R.drawable.ic_launcher_background
        }
    }

    fun formatearFecha(timestamp: Long): String {
        return SimpleDateFormat(
            "dd/MM/yyyy HH:mm",
            Locale.getDefault()
        ).format(Date(timestamp))
    }