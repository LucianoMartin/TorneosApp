package com.tpgrupal.appsmoviles.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.tpgrupal.appsmoviles.data.model.Torneo
import kotlinx.coroutines.tasks.await
import com.tpgrupal.appsmoviles.data.model.Enfrentamiento
import com.tpgrupal.appsmoviles.data.model.enums.EstadoTorneo
import com.tpgrupal.appsmoviles.data.model.SolicitudParticipacion
import com.tpgrupal.appsmoviles.data.model.enums.EstadoSolicitud

class TorneoRepository {

    private val db = FirebaseFirestore.getInstance()

    private val notificacionRepo = NotificacionRepository()

    suspend fun crearTorneo(torneo: Torneo) {

        val docRef = db.collection("torneos").document()

        val torneoConId = torneo.copy(
            id = docRef.id
        )

        docRef
            .set(torneoConId)
            .await()
    }

    suspend fun actualizarTorneo(torneo: Torneo) {

        db.collection("torneos")
            .document(torneo.id)
            .set(torneo)
            .await()
    }

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

    suspend fun actualizarEnfrentamientos(
        torneoId: String,
        enfrentamientos: List<Enfrentamiento>
    ) {
        db.collection("torneos")
            .document(torneoId)
            .update("enfrentamientos", enfrentamientos)
            .await()
    }

    suspend fun iniciarTorneo(
        torneoId: String,
        enfrentamientos: List<Enfrentamiento>
    ) {

        val torneo =
            obtenerTorneoPorId(torneoId)
                ?: return

        db.collection("torneos")
            .document(torneoId)
            .update(
                mapOf(
                    "estado" to EstadoTorneo.EN_CURSO.name,
                    "fechaInicio" to System.currentTimeMillis(),
                    "enfrentamientos" to enfrentamientos
                )
            )
            .await()

        val usuarios =
            db.collection("usuarios")
                .get()
                .await()

        usuarios.documents.forEach { documento ->

            val favoritos =
                documento.get("favoritos")
                        as? List<String>
                    ?: emptyList()

            if (favoritos.contains(torneoId)) {

                notificacionRepo.notificar(
                    usuarioId = documento.id,
                    titulo = "🚀 Torneo iniciado",
                    mensaje = "${torneo.nombre} acaba de comenzar"
                )
            }
        }

        val solicitudesSnapshot =
            db.collection("torneos")
                .document(torneoId)
                .collection("solicitudes")
                .get()
                .await()

        solicitudesSnapshot.documents.forEach {
            it.reference.delete().await()
        }
    }

    suspend fun finalizarTorneo(
        torneoId: String
    ) {

        db.collection("torneos")
            .document(torneoId)
            .update(
                mapOf(
                    "estado" to EstadoTorneo.FINALIZADO.name,
                    "fechaFin" to System.currentTimeMillis()
                )
            )
            .await()
    }

    suspend fun solicitarParticipacion(
        torneoId: String,
        solicitud: SolicitudParticipacion
    ) {

        val torneo =
            obtenerTorneoPorId(torneoId)
                ?: return

        db.collection("torneos")
            .document(torneoId)
            .collection("solicitudes")
            .document(solicitud.usuarioId)
            .set(solicitud)
            .await()

        notificacionRepo.notificar(
            usuarioId = torneo.creadorId,
            titulo = "📩 Nueva solicitud",
            mensaje = "Hay una nueva solicitud para ${torneo.nombre}"
        )
    }

    suspend fun agregarParticipante(
        torneoId: String,
        usuarioId: String
    ) {

        db.collection("torneos")
            .document(torneoId)
            .update(
                "participantes",
                FieldValue.arrayUnion(usuarioId)
            )
            .await()
    }

    suspend fun quitarParticipante(
        torneoId: String,
        usuarioId: String
    ) {

        db.collection("torneos")
            .document(torneoId)
            .update(
                "participantes",
                FieldValue.arrayRemove(usuarioId)
            )
            .await()
    }

    suspend fun obtenerSolicitudes(torneoId: String): List<SolicitudParticipacion> {
        return db.collection("torneos")
            .document(torneoId)
            .collection("solicitudes")
            .get()
            .await()
            .documents
            .mapNotNull { it.toObject(SolicitudParticipacion::class.java) }
    }

    suspend fun obtenerSolicitudesPendientes(
        torneoId: String
    ): List<SolicitudParticipacion> {

        return db.collection("torneos")
            .document(torneoId)
            .collection("solicitudes")
            .whereEqualTo(
                "estado",
                EstadoSolicitud.PENDIENTE.name
            )
            .get()
            .await()
            .documents
            .mapNotNull {
                it.toObject(SolicitudParticipacion::class.java)
            }
    }

    suspend fun aceptarSolicitud(
        torneoId: String,
        usuarioId: String
    ) {
        val torneo =
            obtenerTorneoPorId(torneoId)
                ?: return

        val solicitud =
            db.collection("torneos")
                .document(torneoId)
                .collection("solicitudes")
                .document(usuarioId)
                .get()
                .await()
                .toObject(SolicitudParticipacion::class.java)
                ?: return

        if (solicitud.estado != EstadoSolicitud.PENDIENTE)
            return

        if (torneo.participantes.size >= torneo.maxParticipantes)
            return

        db.collection("torneos")
            .document(torneoId)
            .collection("solicitudes")
            .document(usuarioId)
            .update(
                "estado",
                EstadoSolicitud.ACEPTADA.name
            )
            .await()

        db.collection("torneos")
            .document(torneoId)
            .update(
                "participantes",
                FieldValue.arrayUnion(usuarioId)
            )
            .await()

        notificacionRepo.notificar(
            usuarioId = usuarioId,
            titulo = "🎉 Solicitud aceptada",
            mensaje = "Fuiste aceptado en el torneo ${torneo.nombre}"
        )
    }

    suspend fun rechazarSolicitud(
        torneoId: String,
        usuarioId: String
    ) {
        val torneo =
            obtenerTorneoPorId(torneoId)
                ?: return

        val solicitud =
            db.collection("torneos")
                .document(torneoId)
                .collection("solicitudes")
                .document(usuarioId)
                .get()
                .await()
                .toObject(SolicitudParticipacion::class.java)
                ?: return

        if (solicitud.estado != EstadoSolicitud.PENDIENTE)
            return

        db.collection("torneos")
            .document(torneoId)
            .collection("solicitudes")
            .document(usuarioId)
            .update(
                "estado",
                EstadoSolicitud.RECHAZADA.name
            )
            .await()

        notificacionRepo.notificar(
            usuarioId = usuarioId,
            titulo = "❌ Solicitud rechazada",
            mensaje = "Tu solicitud para ${torneo.nombre} fue rechazada"
        )
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
}