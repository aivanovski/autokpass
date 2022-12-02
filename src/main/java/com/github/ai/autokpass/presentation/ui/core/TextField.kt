package com.github.ai.autokpass.presentation.ui.core

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TextFieldIcons(
    isPasswordToggleEnabled: Boolean,
    isError: Boolean,
    isPasswordVisible: Boolean,
    onErrorIconClicked: () -> Unit,
    onPasswordIconClicked: () -> Unit
) {
    when {
        isError -> {
            Icon(
                imageVector = Icons.Rounded.ErrorOutline,
                contentDescription = null,
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(bounded = false),
                    ) {
                        onErrorIconClicked.invoke()
                    }
                    .padding(8.dp)
            )
        }
        isPasswordToggleEnabled -> {
            val icon = if (isPasswordVisible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(bounded = false),
                    ) {
                        onPasswordIconClicked.invoke()
                    }
                    .padding(8.dp)
            )
        }
    }
}
