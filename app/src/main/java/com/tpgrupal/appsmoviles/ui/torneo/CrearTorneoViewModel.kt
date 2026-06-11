package com.tpgrupal.appsmoviles.ui.torneo

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tpgrupal.appsmoviles.data.cloudinary.CloudinaryUploader
import com.tpgrupal.appsmoviles.data.model.Juego
import com.tpgrupal.appsmoviles.data.model.Torneo
import com.tpgrupal.appsmoviles.data.repository.JuegoRepository
import com.tpgrupal.appsmoviles.data.repository.TorneoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CrearTorneoViewModel : ViewModel() {

    private val torneoRepository = TorneoRepository()
    private val juegoRepository = JuegoRepository()

    var nombre = MutableStateFlow("")
        private set
    var descripcion = MutableStateFlow("")
        private set
    var requisitos = MutableStateFlow("")
        private set
    var maxParticipantes = MutableStateFlow("16")
        private set
    var imagenUri = MutableStateFlow<Uri?>(null)
        private set

    var juegoSeleccionado = MutableStateFlow<Juego?>(null)
        private set
    var juegos = MutableStateFlow<List<Juego>>(emptyList())
        private set

    var ciudad = MutableStateFlow("La Plata")
        private set
    var latitud = MutableStateFlow(-34.9205)
        private set
    var longitud = MutableStateFlow(-57.9536)
        private set

    private val _creando = MutableStateFlow(false)
    val creando: StateFlow<Boolean> = _creando.asStateFlow()

    init {
        cargarJuegos()
    }

    private fun cargarJuegos() {
        viewModelScope.launch {
            juegos.value = juegoRepository.obtenerJuegos()
        }
    }

    fun actualizarNombre(valor: String) {
        nombre.value = valor
    }

    fun actualizarDescripcion(valor: String) {
        descripcion.value = valor
    }

    fun actualizarRequisitos(valor: String) {
        requisitos.value = valor
    }

    fun actualizarImagen(uri: Uri?) {
        imagenUri.value = uri
    }

    fun actualizarJuego(juego: Juego) {
        juegoSeleccionado.value = juego
    }

    fun actualizarUbicacion(
        ciudadNueva: String,
        lat: Double,
        lng: Double
    ) {
        ciudad.value = ciudadNueva
        latitud.value = lat
        longitud.value = lng
    }

    fun actualizarMaxParticipantes(valor: String) {

        if (
            valor.all { it.isDigit() } &&
            (valor.toIntOrNull() ?: 0) <= 1024
        ) {
            maxParticipantes.value = valor
        }
    }

    fun crearTorneo(
        context: Context,
        onSuccess: () -> Unit
    ) {

        viewModelScope.launch {

            try {

                _creando.value = true

                CloudinaryUploader.init(context)

                val imagenUrl =
                    if (imagenUri.value != null) {
                        CloudinaryUploader.uploadImage(
                            imagenUri.value!!
                        )
                    } else {
                        ""
                    }

                torneoRepository.crearTorneo(
                    Torneo(
                        nombre = nombre.value,
                        juegoId = juegoSeleccionado.value?.id ?: "",
                        ciudad = ciudad.value,
                        descripcion = descripcion.value,
                        requisitos = requisitos.value,
                        imagenUrl = imagenUrl,
                        maxParticipantes = maxParticipantes.value.toIntOrNull() ?: 16,
                        latitud = latitud.value,
                        longitud = longitud.value,
                        fechaCreacion = System.currentTimeMillis(),
                        creadorId = Firebase.auth.currentUser?.uid ?: ""
                    )
                )

                onSuccess()

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _creando.value = false
            }
        }
    }
}