package com.tpgrupal.appsmoviles.ui

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun HomeScreen() {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text("Pantalla principal")

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                Firebase.auth.signOut()
            }) {
                Text("Cerrar sesión")
            }
        }
    }
}
