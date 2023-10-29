package com.github.ai.autokpass.presentation.ui.core.navigation

import com.github.ai.autokpass.presentation.ui.Screen

interface Router {
    fun navigateTo(screen: Screen)
    fun exit()
}