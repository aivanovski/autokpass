package com.github.ai.autokpass.presentation.ui.core.navigation

import com.arkivanov.decompose.router.stack.push
import com.github.ai.autokpass.presentation.ui.Screen
import com.github.ai.autokpass.presentation.ui.root.RootComponent
import kotlin.system.exitProcess

class RouterImpl(private val rootComponent: RootComponent) : Router {

    override fun navigateTo(screen: Screen) {
        rootComponent.navigation.push(screen)
    }

    override fun exit() {
        exitProcess(0)
    }
}