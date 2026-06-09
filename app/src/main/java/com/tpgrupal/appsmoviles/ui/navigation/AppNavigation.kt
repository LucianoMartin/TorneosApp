package com.tpgrupal.appsmoviles.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tpgrupal.appsmoviles.ui.home.HomeScreen
import com.tpgrupal.appsmoviles.ui.login.LoginScreen
import com.tpgrupal.appsmoviles.ui.participaciones.ParticipacionesScreen
import com.tpgrupal.appsmoviles.ui.profile.FavoritosScreen
import com.tpgrupal.appsmoviles.ui.profile.PerfilScreen
import com.tpgrupal.appsmoviles.ui.torneo.CrearTorneoScreen
import com.tpgrupal.appsmoviles.ui.torneo.DetalleTorneoScreen

@Composable
fun AppNavigation(
    startDestination: String
) {

    val navController = rememberNavController()

    CompositionLocalProvider(
        LocalNavController provides navController
    ) {

        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {

            composable("login") {

                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate("home") {
                            popUpTo("login") {
                                inclusive = true
                            }
                        }
                    },
                    onRegister = {},
                    errorMessage = null
                )
            }

            composable("home") {

                HomeScreen(

                    onCrearTorneo = {
                        navController.navigate("crear_torneo")
                    },

                    onTorneoClick = { torneoId ->
                        navController.navigate(
                            "detalle_torneo/$torneoId"
                        )
                    },

                    onPerfilClick = {
                        navController.navigate("perfil")
                    }
                )
            }

            composable("perfil") {

                PerfilScreen(

                    onVolver = {
                        navController.popBackStack()
                    },

                    onFavoritosClick = {
                        navController.navigate("favoritos")
                    },

                    onCerrarSesion = {

                        Firebase.auth.signOut()

                        navController.navigate("login") {
                            popUpTo(0)
                        }
                    }
                )
            }

            composable("favoritos") {

                FavoritosScreen(

                    onVolver = {
                        navController.popBackStack()
                    },

                    onTorneoClick = { torneoId ->
                        navController.navigate(
                            "detalle_torneo/$torneoId"
                        )
                    }
                )
            }

            composable("crear_torneo") {

                CrearTorneoScreen(
                    onVolver = {
                        navController.popBackStack()
                    }
                )
            }

            composable(
                route = "detalle_torneo/{torneoId}"
            ) {

                val torneoId =
                    it.arguments?.getString("torneoId")
                        ?: ""

                DetalleTorneoScreen(
                    torneoId = torneoId,
                    onVolver = {
                        navController.popBackStack()
                    }
                )
            }

            composable("participaciones") {

                ParticipacionesScreen(

                    onTorneoClick = { torneoId ->

                        navController.navigate(
                            "detalle_torneo/$torneoId"
                        )
                    },

                    onPerfilClick = {
                        navController.navigate("perfil")
                    }
                )
            }
        }
    }
}