package com.github.ai.autokpass.presentation.ui.core

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.ai.autokpass.presentation.ui.core.theme.AppColors

@Composable
fun PreviewWithBackground(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = AppColors.materialColors
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.background)
        ) {
            content.invoke()
        }
    }
}