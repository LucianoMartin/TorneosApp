package com.tpgrupal.appsmoviles.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.tpgrupal.appsmoviles.data.model.Notificacion
import kotlinx.coroutines.tasks.await

class NotificacionRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun crearNotificacion(
        notificacion: Notificacion
    ) {

        val docRef =
            db.collection("notificaciones")
                .document()

        docRef.set(

            notificacion.copy(
                id = docRef.id
            )

        ).await()
    }

    suspend fun notificar(
        usuarioId: String,
        titulo: String,
        mensaje: String
    ) {
        crearNotificacion(
            Notificacion(
                usuarioId = usuarioId,
                titulo = titulo,
                mensaje = mensaje,
                fecha = System.currentTimeMillis(),
                leida = false
            )
        )
    }
}