package com.tpgrupal.appsmoviles.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.tpgrupal.appsmoviles.data.model.Torneo
import kotlinx.coroutines.tasks.await
import com.tpgrupal.appsmoviles.data.model.Enfrentamiento
import com.tpgrupal.appsmoviles.data.model.enums.EstadoTorneo

class TorneoRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun obtenerTorneos(): List<Torneo> {

        return try {

            db.collection("torneos")
                .get()
                .await()
                .documents
                .mapNotNull { document ->

                    document.toObject(Torneo::class.java)
                        ?.copy(
                            id = document.id
                        )
                }

        } catch (e: Exception) {

            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun crearTorneo(torneo: Torneo) {

        val docRef = db.collection("torneos").document()

        val torneoConId = torneo.copy(
            id = docRef.id
        )

        docRef
            .set(torneoConId)
            .await()
    }

    suspend fun obtenerTorneoPorId(
        torneoId: String
    ): Torneo? {

        return try {

            val document = db.collection("torneos")
                .document(torneoId)
                .get()
                .await()

            document.toObject(Torneo::class.java)
                ?.copy(id = document.id)

        } catch (e: Exception) {

            e.printStackTrace()
            null
        }
    }

    fun observarTorneos(
        onChange: (List<Torneo>) -> Unit
    ) {

        db.collection("torneos")
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    error.printStackTrace()
                    return@addSnapshotListener
                }

                val torneos =
                    snapshot?.documents?.mapNotNull { document ->

                        document.toObject(Torneo::class.java)
                            ?.copy(id = document.id)

                    } ?: emptyList()

                onChange(torneos)
            }
    }

    suspend fun agregarFavorito(
        torneoId: String,
        usuarioId: String
    ) {

        db.collection("torneos")
            .document(torneoId)
            .update(
                "favoritos",
                FieldValue.arrayUnion(usuarioId)
            )
            .await()
    }

    suspend fun quitarFavorito(
        torneoId: String,
        usuarioId: String
    ) {

        db.collection("torneos")
            .document(torneoId)
            .update(
                "favoritos",
                FieldValue.arrayRemove(usuarioId)
            )
            .await()
    }

    suspend fun participarEnTorneo(
        torneoId: String,
        usuarioId: String
    ) {

        db.collection("torneos")
            .document(torneoId)
            .update(
                "participantes",
                com.google.firebase.firestore.FieldValue.arrayUnion(usuarioId)
            )
            .await()
    }

    suspend fun iniciarTorneo(
        torneoId: String
    ) {

        val torneo =
            obtenerTorneoPorId(torneoId)
                ?: return

        val participantes =
            torneo.participantes.shuffled()

        val enfrentamientos =
            mutableListOf<Enfrentamiento>()

        var i = 0

        while (i < participantes.size - 1) {

            enfrentamientos.add(

                Enfrentamiento(

                    jugador1 =
                        participantes[i],

                    jugador2 =
                        participantes[i + 1],

                    ronda = 1
                )
            )

            i += 2
        }

        db.collection("torneos")
            .document(torneoId)
            .update(
                mapOf(
                    "estado" to EstadoTorneo.EN_CURSO.name,
                    "enfrentamientos" to enfrentamientos
                )
            )
            .await()
    }

    suspend fun solicitarParticipacion(
        torneoId: String,
        usuarioId: String
    ) {

        db.collection("torneos")
            .document(torneoId)
            .update(
                "solicitudes",
                FieldValue.arrayUnion(usuarioId)
            )
            .await()
    }

    suspend fun aceptarSolicitud(
        torneoId: String,
        usuarioId: String
    ) {

        db.collection("torneos")
            .document(torneoId)
            .update(
                mapOf(
                    "solicitudes" to FieldValue.arrayRemove(usuarioId),
                    "participantes" to FieldValue.arrayUnion(usuarioId)
                )
            )
            .await()
    }

    suspend fun rechazarSolicitud(
        torneoId: String,
        usuarioId: String
    ) {

        db.collection("torneos")
            .document(torneoId)
            .update(
                "solicitudes",
                FieldValue.arrayRemove(usuarioId)
            )
            .await()
    }

    suspend fun seleccionarGanador(
        torneoId: String,
        indiceEnfrentamiento: Int,
        ganadorUid: String
    ) {

        val torneo =
            obtenerTorneoPorId(torneoId)
                ?: return

        val enfrentamientos =
            torneo.enfrentamientos.toMutableList()

        enfrentamientos[indiceEnfrentamiento] =
            enfrentamientos[indiceEnfrentamiento].copy(
                ganador = ganadorUid
            )

        db.collection("torneos")
            .document(torneoId)
            .update(
                "enfrentamientos",
                enfrentamientos
            )
            .await()
    }
}