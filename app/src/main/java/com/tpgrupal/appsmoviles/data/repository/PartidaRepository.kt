package com.tpgrupal.appsmoviles.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.tpgrupal.appsmoviles.data.model.Partida
import kotlinx.coroutines.tasks.await

class PartidaRepository {

    private val db =
        FirebaseFirestore.getInstance()

    suspend fun crearPartida(
        partida: Partida
    ) {

        val docRef =
            db.collection("partidas")
                .document()

        docRef.set(
            partida.copy(
                id = docRef.id
            )
        ).await()
    }

    suspend fun obtenerPartidasPorTorneo(
        torneoId: String
    ): List<Partida> {

        return db.collection("partidas")
            .whereEqualTo(
                "torneoId",
                torneoId
            )
            .get()
            .await()
            .documents
            .mapNotNull {
                it.toObject(Partida::class.java)
            }
    }

    suspend fun obtenerPartida(
        partidaId: String
    ): Partida? {

        return db.collection("partidas")
            .document(partidaId)
            .get()
            .await()
            .toObject(Partida::class.java)
    }

    suspend fun finalizarPartida(
        partidaId: String,
        ganadorId: String
    ) {

        db.collection("partidas")
            .document(partidaId)
            .update(
                mapOf(
                    "ganadorId" to ganadorId,
                    "finalizada" to true
                )
            )
            .await()
    }

    suspend fun eliminarPartidasDeTorneo(
        torneoId: String
    ) {

        val partidas =
            obtenerPartidasPorTorneo(torneoId)

        partidas.forEach {

            db.collection("partidas")
                .document(it.id)
                .delete()
                .await()
        }
    }

    suspend fun obtenerPartidaPorEnfrentamiento(
        torneoId: String,
        enfrentamientoId: String
    ): Partida? {
        return db.collection("partidas")
            .whereEqualTo("torneoId", torneoId)
            .whereEqualTo("enfrentamientoId", enfrentamientoId)
            .limit(1)
            .get()
            .await()
            .documents
            .firstOrNull()
            ?.toObject(Partida::class.java)
    }
}