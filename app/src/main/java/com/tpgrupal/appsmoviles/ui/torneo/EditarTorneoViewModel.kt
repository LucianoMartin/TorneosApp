package com.tpgrupal.appsmoviles.ui.torneo

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tpgrupal.appsmoviles.data.cloudinary.CloudinaryUploader
import com.tpgrupal.appsmoviles.data.model.Juego
import com.tpgrupal.appsmoviles.data.repository.TorneoRepository
import com.tpgrupal.appsmoviles.data.model.Torneo
import com.tpgrupal.appsmoviles.data.repository.JuegoRepository
import com.tpgrupal.appsmoviles.data.repository.UsuarioRepository
import kotlinx.coroutines.launch

class EditarTorneoViewModel : ViewModel() {

    private val repo = TorneoRepository()
    private val juegoRepo = JuegoRepository()

    private val usuarioRepo = UsuarioRepository()

    var participantesInfo by mutableStateOf<Map<String, String>>(emptyMap())
        private set

    var participantesActuales by mutableStateOf(0)
        private set

    var torneo by mutableStateOf<Torneo?>(null)
        private set

    var nombre by mutableStateOf("")
    var descripcion by mutableStateOf("")
    var requisitos by mutableStateOf("")
    var maxParticipantes by mutableStateOf("16")

    var juegos by mutableStateOf<List<Juego>>(emptyList())
        private set
    var juegoSeleccionado by mutableStateOf<Juego?>(null)

    var ciudad by mutableStateOf("")
    var latitud by mutableStateOf(0.0)
    var longitud by mutableStateOf(0.0)

    var imagenUri by mutableStateOf<Uri?>(null)

    var moderadores by mutableStateOf<List<String>>(emptyList())

    var errorMaxParticipantes by mutableStateOf<String?>(null)
        private set

    val formularioValido: Boolean
        get() {
            val max = maxParticipantes.toIntOrNull() ?: return false
            return max >= participantesActuales &&
                    nombre.isNotBlank()
        }

    init {

        viewModelScope.launch {

            juegos =
                juegoRepo.obtenerJuegos()
        }
    }

    fun cargarTorneo(torneoId: String) {

        viewModelScope.launch {

            val t =
                repo.obtenerTorneoPorId(torneoId)
                    ?: return@launch

            torneo = t

            nombre = t.nombre
            descripcion = t.descripcion
            requisitos = t.requisitos

            maxParticipantes =
                t.maxParticipantes.toString()

            ciudad = t.ciudad
            latitud = t.latitud
            longitud = t.longitud

            moderadores = t.moderadores

            val mapa = mutableMapOf<String, String>()

            t.participantes.forEach { uid ->

                val usuario =
                    usuarioRepo.obtenerUsuario(uid)

                mapa[uid] =
                    usuario?.nombre ?: uid
            }

            participantesInfo = mapa

            participantesActuales = t.participantes.size

            juegoSeleccionado =
                juegoRepo.obtenerJuegoPorId(
                    t.juegoId
                )
        }
    }

    fun actualizarMaxParticipantes(valor: String) {

        if (valor.all { it.isDigit() } || valor.isEmpty()) {
            maxParticipantes = valor
            validarMaxParticipantes(valor)
        }
    }

    private fun validarMaxParticipantes(valor: String) {

        val nuevo = valor.toIntOrNull() ?: return

        errorMaxParticipantes =
            if (nuevo < participantesActuales) {
                "Debe haber al menos $participantesActuales participantes"
            } else {
                null
            }
    }

    fun actualizarJuego(
        juego: Juego
    ) {
        juegoSeleccionado = juego
    }

    fun actualizarUbicacion(
        ciudadNueva: String,
        lat: Double,
        lng: Double
    ) {
        ciudad = ciudadNueva
        latitud = lat
        longitud = lng
    }

    fun actualizarImagen(
        uri: Uri?
    ) {
        imagenUri = uri
    }

    fun agregarModerador(uid: String) {

        if (uid !in moderadores) {

            moderadores =
                moderadores + uid
        }
    }

    fun quitarModerador(uid: String) {

        moderadores =
            moderadores.filterNot {
                it == uid
            }
    }

    fun guardarCambios(
        context: Context,
        onSuccess: () -> Unit
    ) {

        val torneoActual =
            torneo ?: return

        viewModelScope.launch {

            val nuevoMax = maxParticipantes.toIntOrNull() ?: torneoActual.maxParticipantes

            if (nuevoMax < participantesActuales) {
                errorMaxParticipantes =
                    "Debe haber al menos $participantesActuales participantes"
                return@launch
            }

            CloudinaryUploader.init(context)

            val imagenUrl =

                if (imagenUri != null) {

                    CloudinaryUploader.uploadImage(
                        imagenUri!!
                    )

                } else {

                    torneoActual.imagenUrl
                }

            repo.actualizarTorneo(

                torneoActual.copy(

                    nombre = nombre,
                    descripcion = descripcion,
                    requisitos = requisitos,
                    maxParticipantes =
                        maxParticipantes.toIntOrNull()
                            ?: torneoActual.maxParticipantes,
                    ciudad = ciudad,
                    latitud = latitud,
                    longitud = longitud,
                    imagenUrl = imagenUrl,

                    juegoId =
                        juegoSeleccionado?.id
                            ?: torneoActual.juegoId,

                    moderadores = moderadores,
                )
            )

            onSuccess()
        }
    }
}