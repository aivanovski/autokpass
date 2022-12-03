package com.github.ai.autokpass.domain

import com.github.ai.autokpass.domain.arguments.ArgumentExtractor
import com.github.ai.autokpass.domain.arguments.ArgumentParser
import com.github.ai.autokpass.domain.usecases.PrintGreetingsUseCase
import com.github.ai.autokpass.model.KeepassKey
import com.github.ai.autokpass.model.ParsedArgs
import com.github.ai.autokpass.model.Result
import com.github.ai.autokpass.presentation.ui.Screen
import com.github.ai.autokpass.presentation.ui.screens.select_entry.SelectEntryArgs
import com.github.ai.autokpass.presentation.ui.screens.termination.TerminationArgs
import java.io.File

class MainInteractor(
    private val argumentExtractor: ArgumentExtractor,
    private val errorInteractor: ErrorInteractor,
    private val argumentParser: ArgumentParser,
    private val printGreetingsUseCase: PrintGreetingsUseCase,
) {

    fun initApp(args: Array<String>): Result<ParsedArgs> {
        printGreetingsUseCase.printGreetings()

        val rawArgs = argumentExtractor.extractArguments(args)

        return argumentParser.validateAndParse(rawArgs)
    }

    fun determineStartScreen(argsResult: Result<ParsedArgs>): Screen {
        val args = argsResult.getDataOrNull()

        return when {
            argsResult.isFailed() -> {
                val errorMessage = errorInteractor.processAndGetMessage(argsResult.asErrorOrThrow())
                Screen.Termination(TerminationArgs(errorMessage))
            }

            args?.keyPath == null -> {
                Screen.Unlock
            }

            else -> {
                val key = KeepassKey.FileKey(
                    file = File(args.keyPath),
                    processingCommand = args.keyProcessingCommand
                )
                Screen.SelectEntry(SelectEntryArgs(key))
            }
        }
    }
}