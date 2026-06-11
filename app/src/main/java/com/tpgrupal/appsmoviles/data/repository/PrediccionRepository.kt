package com.tpgrupal.appsmoviles.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.tpgrupal.appsmoviles.data.model.Prediccion
import kotlinx.coroutines.tasks.await

class PrediccionRepository {

    private val db =
        FirebaseFirestore.getInstance()

    suspend fun guardarPrediccion(
        prediccion: Prediccion
    ) {

        val id =
            "${prediccion.partidaId}_${prediccion.usuarioId}"

        db.collection("predicciones")
            .document(id)
            .set(prediccion)
            .await()
    }

    suspend fun obtenerPrediccionesDePartida(
        partidaId: String
    ): List<Prediccion> {

        return db.collection("predicciones")
            .whereEqualTo(
                "partidaId",
                partidaId
            )
            .get()
            .await()
            .documents
            .mapNotNull {
                it.toObject(
                    Prediccion::class.java
                )
            }
    }

    suspend fun obtenerPrediccionUsuario(
        partidaId: String,
        usuarioId: String
    ): Prediccion? {

        return db.collection("predicciones")
            .document("${partidaId}_${usuarioId}")
            .get()
            .await()
            .toObject(
                Prediccion::class.java
            )
    }
}