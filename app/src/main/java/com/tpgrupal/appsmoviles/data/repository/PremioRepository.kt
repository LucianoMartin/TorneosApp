package com.tpgrupal.appsmoviles.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.tpgrupal.appsmoviles.data.model.Premio
import kotlinx.coroutines.tasks.await

class PremioRepository {

    private val db = FirebaseFirestore.getInstance()
    private val premiosRef = db.collection("premios")

    suspend fun getPremios(): List<Premio> {
        return try {
            val snapshot = premiosRef.get().await()
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Premio::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}