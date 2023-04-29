package com.github.ai.autokpass.presentation.ui.core

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.ai.autokpass.presentation.ui.core.theme.AppTextStyles

@Composable
fun TopBar(
    title: String,
    startContent: (@Composable BoxScope.() -> Unit)? = null,
    endContent: (@Composable BoxScope.() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(0.25f)
                    .fillMaxHeight()
            ) {
                startContent?.invoke(this@Box)
            }

            Box(
                modifier = Modifier
                    .weight(0.5f)
                    .fillMaxHeight()
            ) {
                Text(
                    text = title,
                    style = AppTextStyles.primary,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Box(
                modifier = Modifier
                    .weight(0.25f)
                    .fillMaxHeight()
            ) {
                endContent?.invoke(this@Box)
            }
        }

        content.invoke()
    }
}