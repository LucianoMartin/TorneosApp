package com.tpgrupal.appsmoviles.ui.utils

fun String.juegoAmigable(): String {

    return when (this.lowercase()) {

        "cs2" -> "CS2"
        "lol" -> "LoL"
        "fifa" -> "FIFA"

        else -> this
            .replace("_", " ")
            .split(" ")
            .joinToString(" ") {
                it.replaceFirstChar { c ->
                    c.uppercase()
                }
            }
    }
}