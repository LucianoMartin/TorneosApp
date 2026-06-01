package com.tpgrupal.appsmoviles.data.repository

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
                .mapNotNull {
                    it.toObject(Torneo::class.java)
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
}