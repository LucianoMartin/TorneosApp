package com.tpgrupal.appsmoviles.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.view.MotionEvent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import android.view.View
import androidx.core.content.ContextCompat
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.MapEventsOverlay
import com.tpgrupal.appsmoviles.R

@Composable
fun MapaTorneo(
    context: Context,
    latitud: Double,
    longitud: Double,
    editable: Boolean = false,
    onMapaTocado: (() -> Unit)? = null,
    onUbicacionSeleccionada: ((Double, Double) -> Unit)? = null
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(16.dp)
            )
    ) {

        AndroidView(

            modifier = Modifier.matchParentSize(),

            factory = {

                Configuration.getInstance().load(
                    context,
                    context.getSharedPreferences(
                        "osm",
                        Context.MODE_PRIVATE
                    )
                )

                val mapView = MapView(context)

                @Suppress("ClickableViewAccessibility")
                mapView.setOnTouchListener { v, event ->

                    when (event.action) {

                        MotionEvent.ACTION_DOWN -> {

                            onMapaTocado?.invoke()

                            v.parent?.requestDisallowInterceptTouchEvent(true)
                        }

                        MotionEvent.ACTION_UP -> {
                            v.parent?.requestDisallowInterceptTouchEvent(false)
                            v.performClick()
                        }

                        MotionEvent.ACTION_CANCEL -> {
                            v.parent?.requestDisallowInterceptTouchEvent(false)
                        }
                    }

                    false
                }

                mapView.setTileSource(
                    TileSourceFactory.MAPNIK
                )

                mapView.setMultiTouchControls(true)
                mapView.setTilesScaledToDpi(true)

                val puntoInicial =
                    GeoPoint(
                        latitud,
                        longitud
                    )

                mapView.controller.setZoom(15.0)
                mapView.controller.setCenter(
                    puntoInicial
                )

                val marker =
                    Marker(mapView)

                marker.position =
                    puntoInicial

                marker.icon =
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_torneo_location
                    )

                marker.setAnchor(
                    Marker.ANCHOR_CENTER,
                    Marker.ANCHOR_BOTTOM
                )

                mapView.overlays.add(
                    marker
                )

                if (editable) {

                    val eventos =
                        MapEventsOverlay(
                            object : MapEventsReceiver {

                                override fun singleTapConfirmedHelper(
                                    p: GeoPoint?
                                ): Boolean {

                                    p ?: return false

                                    marker.position = p

                                    mapView.invalidate()

                                    onUbicacionSeleccionada?.invoke(
                                        p.latitude,
                                        p.longitude
                                    )

                                    return true
                                }

                                override fun longPressHelper(
                                    p: GeoPoint?
                                ): Boolean = false
                            }
                        )

                    mapView.overlays.add(
                        eventos
                    )
                }

                mapView
            },

            update = { mapView ->

                val punto =
                    GeoPoint(
                        latitud,
                        longitud
                    )

                mapView.controller.setCenter(
                    punto
                )
            }
        )
    }
}