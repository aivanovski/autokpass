package com.github.ai.autokpass.domain

import com.github.ai.autokpass.data.config.ConfigRepository
import com.github.ai.autokpass.domain.usecases.PrintGreetingsUseCase
import com.github.ai.autokpass.model.KeepassKey
import com.github.ai.autokpass.model.ParsedConfig
import com.github.ai.autokpass.model.Result
import com.github.ai.autokpass.presentation.ui.Screen
import com.github.ai.autokpass.presentation.ui.screens.selectEntry.SelectEntryArgs
import java.io.File

class StartInteractor(
    private val configRepository: ConfigRepository,
    private val printGreetingsUseCase: PrintGreetingsUseCase
) {

    fun setupConfig(commandLineArguments: Array<String>): Result<ParsedConfig> {
        printGreetingsUseCase.printGreetings()

        return configRepository.initialize(commandLineArguments)
    }

    fun determineStartScreen(configResult: Result<ParsedConfig>): Screen {
        val config = configResult.getDataOrNull()

        return when {
            configResult.isFailed() || config?.keyPath == null -> {
                Screen.Unlock
            }
            else -> {
                val key = KeepassKey.FileKey(
                    file = File(config.keyPath),
                    processingCommand = config.keyProcessingCommand
                )
                Screen.SelectEntry(SelectEntryArgs(key))
            }
        }
    }
}