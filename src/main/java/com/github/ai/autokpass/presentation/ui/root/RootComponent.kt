package com.github.ai.autokpass.presentation.ui.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.github.ai.autokpass.model.ParsedArgs
import com.github.ai.autokpass.presentation.ui.core.navigation.Router
import com.github.ai.autokpass.presentation.ui.Screen
import com.github.ai.autokpass.presentation.ui.screens.select_entry.SelectEntryComponent
import com.github.ai.autokpass.presentation.ui.screens.unlock.UnlockScreenComponent

class RootComponent(
    componentContext: ComponentContext,
    startScreen: Screen,
    val appArguments: ParsedArgs
) : ComponentContext by componentContext {

    val navigation = StackNavigation<Screen>()
    val router = Router(this)
    val viewModel = RootViewModel()
    val childStack = childStack(
        source = navigation,
        initialConfiguration = startScreen,
        childFactory = { screen, _ -> createChildComponent(screen) }
    )

    private fun createChildComponent(screen: Screen): ComponentContext {
        return when (screen) {
            is Screen.Unlock -> UnlockScreenComponent(this)
            is Screen.SelectEntry -> SelectEntryComponent(this, screen.args)
        }
    }
}