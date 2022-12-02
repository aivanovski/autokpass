package com.github.ai.autokpass.presentation.ui.core.navigation

import com.arkivanov.decompose.router.stack.push
import com.github.ai.autokpass.presentation.ui.Screen
import com.github.ai.autokpass.presentation.ui.root.RootComponent
import kotlin.system.exitProcess

class Router(private val rootComponent: RootComponent) {

    fun navigateTo(screen: Screen) {
        rootComponent.navigation.push(screen)
    }

    fun exit() {
        exitProcess(0)
    }
}