package com.tpgrupal.appsmoviles.ui.torneo

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.tpgrupal.appsmoviles.ui.components.MapaTorneo
import com.tpgrupal.appsmoviles.ui.utils.obtenerCiudad

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarTorneoScreen(
    torneoId: String,
    onVolver: () -> Unit
) {

    val viewModel: EditarTorneoViewModel =
        viewModel()

    LaunchedEffect(torneoId) {
        viewModel.cargarTorneo(torneoId)
    }

    val torneo = viewModel.torneo

    if (torneo == null) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }

        return
    }

    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->

        viewModel.actualizarImagen(uri)
    }

    var mapaSeleccionado by remember {
        mutableStateOf(false)
    }
    val focusManager = LocalFocusManager.current
    val quitarFocoMapa = Modifier.onFocusChanged {

        if (it.isFocused) {
            mapaSeleccionado = false
        }
    }

    Scaffold(

        topBar = {

            TopAppBar(

                title = {
                    Text("Editar torneo")
                },

                navigationIcon = {

                    IconButton(
                        onClick = onVolver
                    ) {

                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        }

    ) { padding ->

        var expanded by remember {
            mutableStateOf(false)
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {

            OutlinedTextField(
                value = viewModel.nombre,
                onValueChange = {
                    viewModel.nombre = it
                },
                label = {
                    Text("Nombre")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .then(quitarFocoMapa)
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    expanded = !expanded
                }
            ) {

                OutlinedTextField(
                    value = viewModel.juegoSeleccionado?.nombre ?: "",
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

                    viewModel.juegos.forEach { juego ->

                        DropdownMenuItem(
                            text = {
                                Text(juego.nombre)
                            },
                            onClick = {

                                viewModel.actualizarJuego(juego)

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
                        latitud = viewModel.latitud,
                        longitud = viewModel.longitud,
                        editable = true,
                        onMapaTocado = {

                            focusManager.clearFocus()
                            mapaSeleccionado = true
                        },
                        onUbicacionSeleccionada = { lat, lng ->

                            viewModel.actualizarUbicacion(
                                obtenerCiudad(context, lat, lng),
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
                            text = "Ciudad: ${viewModel.ciudad}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    Text(
                        text = "Latitud: %.5f  |  Longitud: %.5f"
                            .format(viewModel.latitud, viewModel.longitud),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            OutlinedTextField(
                value = viewModel.descripcion,
                onValueChange = {
                    viewModel.descripcion = it
                },
                label = {
                    Text("Descripción")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .then(quitarFocoMapa)
            )

            OutlinedTextField(
                value = viewModel.requisitos,
                onValueChange = {
                    viewModel.requisitos = it
                },
                label = {
                    Text("Requisitos")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .then(quitarFocoMapa)
            )

            OutlinedTextField(
                value = viewModel.maxParticipantes,
                onValueChange = viewModel::actualizarMaxParticipantes,
                isError = viewModel.errorMaxParticipantes != null,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                label = { Text("Máximo de participantes") },
                modifier = Modifier
                    .fillMaxWidth()
                    .then(quitarFocoMapa)
            )

            viewModel.errorMaxParticipantes?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Button(
                onClick = {
                    launcher.launch("image/*")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .then(quitarFocoMapa)
            ) {

                Text("Cambiar imagen")
            }

            if (viewModel.imagenUri != null) {

                AsyncImage(
                    model = viewModel.imagenUri,
                    contentDescription = "Imagen nueva",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )

            } else if (torneo.imagenUrl.isNotBlank()) {

                AsyncImage(
                    model = torneo.imagenUrl,
                    contentDescription = "Imagen actual",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }

            OutlinedCard(
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Text(
                        text = "Moderadores",
                        style = MaterialTheme.typography.titleMedium
                    )

                    HorizontalDivider()

                    torneo.participantes.forEach { uid ->

                        val esModerador =
                            uid in viewModel.moderadores

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                text = viewModel.participantesInfo[uid] ?: uid,
                                modifier = Modifier.weight(1f)
                            )

                            Button(

                                onClick = {

                                    if (esModerador) {

                                        viewModel.quitarModerador(uid)

                                    } else {

                                        viewModel.agregarModerador(uid)
                                    }
                                }
                            ) {

                                Text(

                                    if (esModerador)
                                        "Quitar"
                                    else
                                        "Hacer moderador"
                                )
                            }
                        }
                    }
                }
            }

            Button(
                onClick = {

                    viewModel.guardarCambios(
                        context = context,
                        onSuccess = onVolver
                    )
                },
                enabled = viewModel.formularioValido,
                modifier = Modifier
                    .fillMaxWidth()
                    .then(quitarFocoMapa)
            ) {

                Text("Guardar cambios")
            }
        }
    }
}