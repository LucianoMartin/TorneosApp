package com.tpgrupal.appsmoviles.ui.navigation

import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavHostController

val LocalNavController =
    compositionLocalOf<NavHostController> {
        error("NavController no disponible")
    }