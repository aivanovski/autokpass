package com.github.ai.autokpass.presentation.ui.screens.select_entry

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.github.ai.autokpass.di.GlobalInjector.get
import com.github.ai.autokpass.extensions.registerCoroutineViewModel
import com.github.ai.autokpass.presentation.ui.root.RootComponent
import com.github.ai.autokpass.presentation.ui.core.navigation.Component
import org.koin.core.parameter.parametersOf

class SelectEntryComponent(
    private val rootComponent: RootComponent,
    args: SelectEntryArgs
) : Component, ComponentContext by rootComponent {

    private val viewModel: SelectEntryViewModel = get(
        params = parametersOf(
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
        SelectEntryScreen(viewModel)
    }
}