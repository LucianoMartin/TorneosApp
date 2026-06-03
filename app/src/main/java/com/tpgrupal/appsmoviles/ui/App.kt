package com.tpgrupal.appsmoviles.ui

import androidx.compose.runtime.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tpgrupal.appsmoviles.ui.navigation.AppNavigation
import com.tpgrupal.appsmoviles.ui.theme.TorneosTheme
import androidx.compose.ui.platform.LocalContext
import com.tpgrupal.appsmoviles.ui.notifications.NotificacionListener

@Composable
fun App() {

    val auth = Firebase.auth

    var user by remember {
        mutableStateOf(auth.currentUser)
    }

    LaunchedEffect(Unit) {

        auth.addAuthStateListener {

            user = it.currentUser
        }
    }

    val startDestination = if (
        user != null
    ) {
        "home"
    } else {
        "login"
    }

    val context = LocalContext.current

    LaunchedEffect(user) {

        if (user != null) {

            NotificacionListener(
                context
            ).iniciar()
        }
    }

    TorneosTheme {

        AppNavigation(
            startDestination = startDestination
        )
    }
}