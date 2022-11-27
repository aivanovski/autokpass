package com.github.ai.autokpass.presentation.ui.core.navigation

import com.arkivanov.decompose.router.stack.push
import com.github.ai.autokpass.presentation.ui.Screen
import com.github.ai.autokpass.presentation.ui.root.RootComponent

class Router(private val rootComponent: RootComponent) {

    fun navigateTo(screen: Screen) {
        rootComponent.navigation.push(screen)
    }
}