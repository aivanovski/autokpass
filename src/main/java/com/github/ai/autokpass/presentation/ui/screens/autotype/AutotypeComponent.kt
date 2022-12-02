package com.github.ai.autokpass.presentation.ui.screens.autotype

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.github.ai.autokpass.di.GlobalInjector.get
import com.github.ai.autokpass.extensions.registerCoroutineViewModel
import com.github.ai.autokpass.presentation.ui.core.navigation.Component
import com.github.ai.autokpass.presentation.ui.root.RootComponent
import org.koin.core.parameter.parametersOf

class AutotypeComponent(
    rootComponent: RootComponent,
    args: AutotypeArgs
) : Component, ComponentContext by rootComponent {

    private val viewModel: AutotypeViewModel = get(
        params = parametersOf(
            rootComponent.viewModel,
            rootComponent.router,
            args,
            rootComponent.appArguments
        )
    )

    init {
        lifecycle.registerCoroutineViewModel(viewModel)
    }

    @Composable
    override fun render() {
        AutotypeScreen(viewModel)
    }
}