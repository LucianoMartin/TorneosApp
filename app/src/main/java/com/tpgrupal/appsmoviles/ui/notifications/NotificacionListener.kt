package com.tpgrupal.appsmoviles.ui.notifications

import android.content.Context
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class NotificacionListener(

    private val context: Context

) {

    private val db =
        FirebaseFirestore.getInstance()

    fun iniciar() {

        val uid =
            Firebase.auth.currentUser?.uid
                ?: return

        db.collection("notificaciones")

            .whereEqualTo(
                "usuarioId",
                uid
            )

            .addSnapshotListener { snapshot, error ->

                if (
                    error != null ||
                    snapshot == null
                ) {

                    println(
                        "ERROR LISTENER: ${error?.message}"
                    )

                    return@addSnapshotListener
                }

                snapshot.documentChanges.forEach { change ->

                    if (
                        change.type.name ==
                        "ADDED"
                    ) {

                        val titulo =
                            change.document.getString(
                                "titulo"
                            ) ?: ""

                        val mensaje =
                            change.document.getString(
                                "mensaje"
                            ) ?: ""

                        NotificationHelper
                            .mostrarNotificacion(
                                context,
                                titulo,
                                mensaje
                            )

                        change.document
                            .reference
                            .delete()
                            .addOnSuccessListener {

                                println(
                                    "NOTIFICACION BORRADA: ${change.document.id}"
                                )
                            }
                            .addOnFailureListener {

                                println(
                                    "ERROR BORRANDO: ${it.message}"
                                )
                            }
                    }
                }
            }
    }
}