package com.tpgrupal.appsmoviles.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tpgrupal.appsmoviles.data.model.Usuario

@Composable
fun JugadorSide(
    user: Usuario?,
    isWinner: Boolean = false,
    avatarDerecha: Boolean = false
) {

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (!avatarDerecha) {
            AvatarUsuario(
                avatarUrl = user?.avatarUrl
            )

            Spacer(Modifier.width(6.dp))
        }

        Text(
            text = user?.nombre ?: "Jugador",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color =
                if (isWinner)
                    MaterialTheme.colorScheme.onSurface
                else
                    MaterialTheme.colorScheme.primary
        )

        if (avatarDerecha) {
            Spacer(Modifier.width(6.dp))

            AvatarUsuario(
                avatarUrl = user?.avatarUrl
            )
        }
    }
}