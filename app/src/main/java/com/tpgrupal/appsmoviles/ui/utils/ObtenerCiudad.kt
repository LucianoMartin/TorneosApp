package com.tpgrupal.appsmoviles.ui.utils

import android.content.Context
import android.location.Geocoder
import java.util.Locale

fun obtenerCiudad(
    context: Context,
    latitud: Double,
    longitud: Double
): String {

    return try {

        val geocoder =
            Geocoder(
                context,
                Locale.getDefault()
            )

        val direcciones =
            geocoder.getFromLocation(
                latitud,
                longitud,
                1
            )

        direcciones?.firstOrNull()?.locality
            ?: "Ciudad desconocida"

    } catch (e: Exception) {

        "Ciudad desconocida"
    }
}