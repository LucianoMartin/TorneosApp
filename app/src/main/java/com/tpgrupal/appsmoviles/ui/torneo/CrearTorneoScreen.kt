package com.tpgrupal.appsmoviles.ui.torneo

import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tpgrupal.appsmoviles.data.model.Torneo
import com.tpgrupal.appsmoviles.data.repository.TorneoRepository
import com.tpgrupal.appsmoviles.ui.components.MapaTorneo
import com.tpgrupal.appsmoviles.ui.utils.obtenerCiudad
import com.tpgrupal.appsmoviles.data.repository.JuegoRepository
import com.tpgrupal.appsmoviles.data.model.Juego
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import coil.compose.AsyncImage
import com.tpgrupal.appsmoviles.data.cloudinary.CloudinaryUploader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearTorneoScreen(
    onVolver: () -> Unit
) {

    val repository = remember {
        TorneoRepository()
    }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    CloudinaryUploader.init(context)

    var nombre by remember { mutableStateOf("") }
    var ciudad by remember { mutableStateOf("La Plata") }
    var descripcion by remember { mutableStateOf("") }
    var requisitos by remember { mutableStateOf("") }
    var imagenUri by remember {
        mutableStateOf<Uri?>(null)
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        imagenUri = uri
    }
    var maxParticipantes by remember { mutableStateOf("16") }

    // Selección de juego
    val juegoRepository = remember { JuegoRepository() }
    var juegos by remember { mutableStateOf<List<Juego>>(emptyList()) }
    var juegoSeleccionado by remember { mutableStateOf<Juego?>(null) }
    var expanded by remember { mutableStateOf(false) }

    // Ubicación generica: La Plata
    var latitud by remember { mutableStateOf(-34.9205) }
    var longitud by remember { mutableStateOf(-57.9536) }

    var mapaSeleccionado by remember {
        mutableStateOf(false)
    }
    val focusManager = LocalFocusManager.current
    val quitarFocoMapa = Modifier.onFocusChanged {

        if (it.isFocused) {
            mapaSeleccionado = false
        }
    }

    val formularioValido =
        nombre.trim().isNotEmpty() &&
                juegoSeleccionado != null &&
                (maxParticipantes.toIntOrNull() ?: 0) > 1

    LaunchedEffect(Unit) {

        juegos =
            juegoRepository.obtenerJuegos()
    }

    Scaffold(

        topBar = {

            TopAppBar(
                title = {
                    Text("Crear Torneo")
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
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier
                    .fillMaxWidth()
                    .then(quitarFocoMapa)
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {

                    expanded = !expanded
                    mapaSeleccionado = false
                }
            ) {

                OutlinedTextField(
                    value = juegoSeleccionado?.nombre ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = {
                        Text("Juego")
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expanded
                        )
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .then(quitarFocoMapa)
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {
                        expanded = false
                    }
                ) {

                    juegos.forEach { juego ->

                        DropdownMenuItem(
                            text = {
                                Text(juego.nombre)
                            },
                            onClick = {

                                juegoSeleccionado = juego
                                expanded = false
                            }
                        )
                    }
                }
            }

            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.outlinedCardColors(
                    containerColor = androidx.compose.ui.graphics.Color.Transparent
                ),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(
                        if (mapaSeleccionado)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.outline
                    )
                )
            ) {

                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    Text(
                        text = "Ubicación del torneo",
                        style = MaterialTheme.typography.titleMedium
                    )

                    HorizontalDivider()

                    MapaTorneo(
                        context = context,
                        latitud = latitud,
                        longitud = longitud,
                        editable = true,
                        onMapaTocado = {

                            focusManager.clearFocus()
                            mapaSeleccionado = true
                        },
                        onUbicacionSeleccionada = { lat, lng ->

                            latitud = lat
                            longitud = lng

                            ciudad = obtenerCiudad(
                                context,
                                lat,
                                lng
                            )
                        },
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
                            text = "Ciudad: $ciudad",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    Text(
                        text = "Latitud: %.5f  |  Longitud: %.5f"
                            .format(latitud, longitud),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            OutlinedTextField(
                value = maxParticipantes,
                onValueChange = {

                    if (
                        it.all { caracter -> caracter.isDigit() } &&
                        (it.toIntOrNull() ?: 0) <= 1024
                    ) {
                        maxParticipantes = it
                    }
                },
                isError = (maxParticipantes.toIntOrNull() ?: 0) <= 1,

                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                label = { Text("Máximo de Participantes") },
                modifier = Modifier
                    .fillMaxWidth()
                    .then(quitarFocoMapa)
            )

            if ((maxParticipantes.toIntOrNull() ?: 0) <= 1) {
                Text(
                    text = "Debe haber al menos 2 participantes",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                modifier = Modifier
                    .fillMaxWidth()
                    .then(quitarFocoMapa)
            )

            OutlinedTextField(
                value = requisitos,
                onValueChange = { requisitos = it },
                label = { Text("Requisitos") },
                modifier = Modifier
                    .fillMaxWidth()
                    .then(quitarFocoMapa)
            )

            Button(
                onClick = {
                    launcher.launch("image/*")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Seleccionar imagen")
            }

            imagenUri?.let { uri ->

                AsyncImage(
                    model = uri,
                    contentDescription = "Imagen torneo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }

            Button(
                enabled = formularioValido,
                modifier = Modifier
                    .fillMaxWidth()
                    .then(quitarFocoMapa),
                onClick = {

                    scope.launch {

                        try {

                            val imagenUrl = if (imagenUri != null) {
                                CloudinaryUploader.uploadImage(imagenUri!!)
                            } else {
                                ""
                            }

                            repository.crearTorneo(

                                Torneo(
                                    nombre = nombre,
                                    juegoId = juegoSeleccionado?.id ?: "",
                                    ciudad = ciudad,
                                    descripcion = descripcion,
                                    requisitos = requisitos,
                                    imagenUrl = imagenUrl,
                                    maxParticipantes = maxParticipantes.toIntOrNull() ?: 16,
                                    latitud = latitud,
                                    longitud = longitud,
                                    fechaCreacion = System.currentTimeMillis(),
                                    fechaInicio = System.currentTimeMillis(),
                                    creadorId = Firebase.auth.currentUser?.uid ?: ""
                                )
                            )

                            onVolver()

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            ) {

                Text("Crear Torneo")
            }
        }
    }
}