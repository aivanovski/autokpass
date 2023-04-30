package com.github.ai.autokpass.presentation.ui.screens.settings

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.github.ai.autokpass.di.GlobalInjector.get
import com.github.ai.autokpass.extensions.registerViewModel
import com.github.ai.autokpass.presentation.ui.core.navigation.Component
import com.github.ai.autokpass.presentation.ui.root.RootComponent
import org.koin.core.parameter.parametersOf

class SettingsComponent(
    rootComponent: RootComponent
) : Component, ComponentContext by rootComponent {

    private val viewModel: SettingsViewModel = get(
        params = parametersOf(
            rootComponent.router
        )
    )

    init {
        lifecycle.registerViewModel(viewModel)
    }

    @Composable
    override fun render() {
        SettingsScreen(viewModel)
    }
}