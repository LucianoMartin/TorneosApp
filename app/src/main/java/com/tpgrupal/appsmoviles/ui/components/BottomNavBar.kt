package com.tpgrupal.appsmoviles.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun BottomNavBar(
    navController: NavController,
    selectedIndex: Int
) {

    NavigationBar {

        NavigationBarItem(
            selected = selectedIndex == 0,
            onClick = {
                navController.navigate("home")
            },
            icon = {
                Icon(
                    Icons.Default.Home,
                    contentDescription = "Inicio"
                )
            },
            label = {
                Text("Inicio")
            }
        )

        NavigationBarItem(
            selected = selectedIndex == 1,
            onClick = {
                navController.navigate("participaciones")
            },
            icon = {
                Icon(
                    Icons.Default.Groups,
                    contentDescription = "Participaciones"
                )
            },
            label = {
                Text("Mis partidas")
            }
        )

        NavigationBarItem(
            selected = selectedIndex == 2,
            onClick = {
                navController.navigate("mis_torneos")
            },
            icon = {
                Icon(
                    Icons.Default.EmojiEvents,
                    contentDescription = "Mis torneos"
                )
            },
            label = {
                Text("Mis torneos")
            }
        )

        NavigationBarItem(
            selected = selectedIndex == 3,
            onClick = {
                navController.navigate("tienda")
            },
            icon = {
                Icon(
                    Icons.Default.Store,
                    contentDescription = "Tienda"
                )
            },
            label = {
                Text("Tienda")
            }
        )
    }
}