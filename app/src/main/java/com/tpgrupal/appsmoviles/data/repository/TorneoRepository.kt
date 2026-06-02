package com.tpgrupal.appsmoviles.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.tpgrupal.appsmoviles.data.model.Torneo
import kotlinx.coroutines.tasks.await

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
}