package com.tpgrupal.appsmoviles.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.tpgrupal.appsmoviles.data.model.Usuario
import kotlinx.coroutines.tasks.await

class UsuarioRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun crearUsuario(
        usuario: Usuario
    ) {

        db.collection("usuarios")
            .document(usuario.uid)
            .set(usuario)
            .await()
    }

    suspend fun obtenerUsuario(
        uid: String
    ): Usuario? {

        return try {

            db.collection("usuarios")
                .document(uid)
                .get()
                .await()
                .toObject(Usuario::class.java)

        } catch (e: Exception) {

            e.printStackTrace()
            null
        }
    }

    suspend fun actualizarAvatar(
        uid: String,
        avatarUrl: String
    ) {

        db.collection("usuarios")
            .document(uid)
            .update(
                "avatarUrl",
                avatarUrl
            )
            .await()
    }

    suspend fun actualizarPuntos(
        uid: String,
        nuevosPuntos: Int
    ) {

        db.collection("usuarios")
            .document(uid)
            .update(
                "puntos",
                nuevosPuntos
            )
            .await()
    }
}