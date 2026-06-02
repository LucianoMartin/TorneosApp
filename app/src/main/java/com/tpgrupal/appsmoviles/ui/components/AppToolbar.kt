package com.tpgrupal.appsmoviles.ui.components
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppToolbar(

    titulo: String,

    onPerfilClick: () -> Unit = {}

) {

    var mostrarMenu by remember {
        mutableStateOf(false)
    }

    TopAppBar(

        title = {

            Row {

                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "  $titulo",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        },

        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),

        actions = {

            IconButton(
                onClick = {
                    mostrarMenu = true
                }
            ) {

                Icon(
                    Icons.Default.AccountCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            DropdownMenu(
                expanded = mostrarMenu,
                onDismissRequest = {
                    mostrarMenu = false
                }
            ) {

                DropdownMenuItem(
                    text = {
                        Text("Mi perfil")
                    },
                    onClick = {

                        mostrarMenu = false

                        onPerfilClick()
                    }
                )

                DropdownMenuItem(
                    text = {
                        Text("Cerrar sesión")
                    },
                    onClick = {

                        mostrarMenu = false

                        Firebase.auth.signOut()
                    }
                )
            }
        }
    )
}