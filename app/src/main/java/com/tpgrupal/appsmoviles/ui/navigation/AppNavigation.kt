package com.tpgrupal.appsmoviles.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import com.tpgrupal.appsmoviles.ui.home.HomeScreen
import com.tpgrupal.appsmoviles.ui.login.LoginScreen
import com.tpgrupal.appsmoviles.ui.torneo.CrearTorneoScreen
import com.tpgrupal.appsmoviles.ui.torneo.DetalleTorneoScreen

@Composable
fun AppNavigation(
    startDestination: String
) {

    val navController = rememberNavController()

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
    }
}