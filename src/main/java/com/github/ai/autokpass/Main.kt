package com.github.ai.autokpass

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.lifecycle.LifecycleController
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.github.ai.autokpass.di.GlobalInjector.get
import com.github.ai.autokpass.di.KoinModule
import com.github.ai.autokpass.domain.StartInteractor
import com.github.ai.autokpass.model.ParsedConfig
import com.github.ai.autokpass.presentation.ui.core.strings.StringResources
import com.github.ai.autokpass.presentation.ui.root.RootComponent
import com.github.ai.autokpass.presentation.ui.root.RootScreen
import org.koin.core.context.startKoin

@OptIn(ExperimentalDecomposeApi::class)
fun main(args: Array<String>) {
    startKoin {
        modules(KoinModule.appModule)
    }

    val interactor: StartInteractor = get()
    val strings: StringResources = get()

    val configResult = interactor.setupConfig(args)

    val arguments = configResult.getDataOrNull() ?: ParsedConfig.EMPTY
    val lifecycle = LifecycleRegistry()
    val rootComponent = RootComponent(
        componentContext = DefaultComponentContext(lifecycle),
        startScreen = interactor.determineStartScreen(configResult),
        appArguments = arguments
    )

    application {
        val windowState by rootComponent.viewModel.windowState.collectAsState()

        LifecycleController(lifecycle, windowState)

        Window(
            onCloseRequest = {
                exitApplication()
            },
            title = strings.appName,
            state = windowState,
            alwaysOnTop = true,
            undecorated = false,
            resizable = true
        ) {
            RootScreen(rootComponent = rootComponent)
        }
    }
}