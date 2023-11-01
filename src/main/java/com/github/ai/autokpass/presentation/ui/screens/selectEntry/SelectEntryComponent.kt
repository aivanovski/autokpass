package com.github.ai.autokpass.presentation.ui.screens.selectEntry

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.github.ai.autokpass.di.GlobalInjector.get
import com.github.ai.autokpass.extensions.registerViewModel
import com.github.ai.autokpass.presentation.ui.core.navigation.Component
import com.github.ai.autokpass.presentation.ui.root.RootComponent
import org.koin.core.parameter.parametersOf

class SelectEntryComponent(
    private val rootComponent: RootComponent,
    args: SelectEntryArgs
) : Component, ComponentContext by rootComponent {

    private val viewModel: SelectEntryViewModel = get(
        params = parametersOf(
            rootComponent.router,
            args
        )
    )

    init {
        lifecycle.registerViewModel(viewModel)
    }

    @Composable
    override fun render() {
        SelectEntryScreen(viewModel)
    }
}