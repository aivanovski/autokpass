package com.github.ai.autokpass.presentation.ui.root

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.github.ai.autokpass.presentation.ui.core.navigation.Component
import com.github.ai.autokpass.presentation.ui.core.theme.AppColors

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun RootScreen(rootComponent: RootComponent) {
    MaterialTheme(
        colors = AppColors.materialColors
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.background)
        ) {
            Children(
                stack = rootComponent.childStack
            ) { (_, component) ->
                (component as Component).render()
            }
        }
    }
}