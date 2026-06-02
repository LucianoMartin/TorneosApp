package com.tpgrupal.appsmoviles.ui.torneo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tpgrupal.appsmoviles.data.model.Torneo
import com.tpgrupal.appsmoviles.data.repository.TorneoRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearTorneoScreen(
    onVolver: () -> Unit
) {

    val repository = remember {
        TorneoRepository()
    }

    val scope = rememberCoroutineScope()

    var nombre by remember { mutableStateOf("") }
    var ciudad by remember { mutableStateOf("") }
    var juegoId by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var requisitos by remember { mutableStateOf("") }
    var maxParticipantes by remember { mutableStateOf("16") }

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
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = juegoId,
                onValueChange = { juegoId = it },
                label = { Text("ID Juego") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = ciudad,
                onValueChange = { ciudad = it },
                label = { Text("Ciudad") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = maxParticipantes,
                onValueChange = { maxParticipantes = it },
                label = { Text("Máx Participantes") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = requisitos,
                onValueChange = { requisitos = it },
                label = { Text("Requisitos") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {

                    scope.launch {

                        repository.crearTorneo(

                            Torneo(
                                nombre = nombre,
                                juegoId = juegoId,
                                ciudad = ciudad,
                                descripcion = descripcion,
                                requisitos = requisitos,
                                maxParticipantes = maxParticipantes.toIntOrNull() ?: 16,
                                fechaCreacion = System.currentTimeMillis(),
                                fechaInicio = System.currentTimeMillis(),
                                creadorId = Firebase.auth.currentUser?.uid ?: ""
                            )
                        )

                        onVolver()
                    }
                }
            ) {

                Text("Crear Torneo")
            }
        }
    }
}