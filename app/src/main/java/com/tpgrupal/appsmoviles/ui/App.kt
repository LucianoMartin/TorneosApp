package com.tpgrupal.appsmoviles.ui

import androidx.compose.runtime.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tpgrupal.appsmoviles.ui.navigation.AppNavigation

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

    AppNavigation(
        startDestination = startDestination
    )
}