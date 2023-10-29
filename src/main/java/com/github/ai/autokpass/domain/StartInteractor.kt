package com.github.ai.autokpass.domain

import com.github.ai.autokpass.data.config.ConfigRepository
import com.github.ai.autokpass.domain.usecases.PrintGreetingsUseCase
import com.github.ai.autokpass.model.ParsedConfig
import com.github.ai.autokpass.model.Result

class StartInteractor(
    private val configRepository: ConfigRepository,
    private val printGreetingsUseCase: PrintGreetingsUseCase
) {

    fun setupConfig(commandLineArguments: Array<String>): Result<ParsedConfig> {
        printGreetingsUseCase.printGreetings()

        return configRepository.initialize(commandLineArguments)
    }
}