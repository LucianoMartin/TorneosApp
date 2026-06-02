package com.tpgrupal.appsmoviles.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.tpgrupal.appsmoviles.data.model.Juego
import kotlinx.coroutines.tasks.await

class JuegoRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun obtenerJuegos(): List<Juego> {

        return try {

            db.collection("juegos")
                .get()
                .await()
                .documents
                .mapNotNull { document ->

                    document.toObject(Juego::class.java)
                        ?.copy(
                            id = document.id
                        )
                }

        } catch (e: Exception) {

            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun obtenerJuegoPorId(
        juegoId: String
    ): Juego? {

        return try {

            db.collection("juegos")
                .document(juegoId)
                .get()
                .await()
                .let { document ->

                    document.toObject(Juego::class.java)
                        ?.copy(
                            id = document.id
                        )
                }

        } catch (e: Exception) {

            e.printStackTrace()
            null
        }
    }
}