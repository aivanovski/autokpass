package com.github.ai.autokpass.presentation.ui.screens.unlock

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.github.ai.autokpass.di.GlobalInjector.get
import com.github.ai.autokpass.extensions.registerCoroutineViewModel
import com.github.ai.autokpass.presentation.ui.core.navigation.Component
import com.github.ai.autokpass.presentation.ui.root.RootComponent
import org.koin.core.parameter.parametersOf

class UnlockScreenComponent(
    rootComponent: RootComponent
) : Component, ComponentContext by rootComponent {

    private val viewModel: UnlockViewModel = get(
        params = parametersOf(
            rootComponent.router,
            rootComponent.appArguments
        )
    )

    init {
        lifecycle.registerCoroutineViewModel(viewModel)
    }

    @Composable
    override fun render() {
        UnlockScreen(viewModel)
    }
}