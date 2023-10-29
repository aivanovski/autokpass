package com.github.ai.autokpass.presentation.ui.core.navigation

import com.github.ai.autokpass.presentation.ui.Screen

class MockRouterImpl : Router {

    private val screens = mutableListOf<Screen>()

    val lastScreen: Screen?
        get() = screens.lastOrNull()

    override fun navigateTo(screen: Screen) {
        screens.add(screen)
    }

    override fun exit() {
    }
}