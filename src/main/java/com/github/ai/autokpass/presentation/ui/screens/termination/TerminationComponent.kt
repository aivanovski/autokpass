package com.github.ai.autokpass.presentation.ui.screens.termination

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.github.ai.autokpass.di.GlobalInjector.get
import com.github.ai.autokpass.extensions.registerCoroutineViewModel
import com.github.ai.autokpass.presentation.ui.core.navigation.Component
import com.github.ai.autokpass.presentation.ui.root.RootComponent
import org.koin.core.parameter.parametersOf

class TerminationComponent(
    rootComponent: RootComponent,
    args: TerminationArgs
) : Component, ComponentContext by rootComponent {

    private val viewModel: TerminationViewModel = get(
        params = parametersOf(
            rootComponent.viewModel,
            rootComponent.router,
            args
        )
    )

    init {
        lifecycle.registerCoroutineViewModel(viewModel)
    }

    @Composable
    override fun render() {
        TerminationScreen(viewModel)
    }
}