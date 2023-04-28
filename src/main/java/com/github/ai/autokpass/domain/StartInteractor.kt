package com.github.ai.autokpass.domain

import com.github.ai.autokpass.domain.arguments.ArgumentParser
import com.github.ai.autokpass.domain.arguments.CommandLineArgumentExtractor
import com.github.ai.autokpass.domain.usecases.PrintGreetingsUseCase
import com.github.ai.autokpass.domain.usecases.ReadConfigFileUseCase
import com.github.ai.autokpass.model.KeepassKey
import com.github.ai.autokpass.model.ParsedArgs
import com.github.ai.autokpass.model.Result
import com.github.ai.autokpass.presentation.ui.Screen
import com.github.ai.autokpass.presentation.ui.core.strings.StringResources
import com.github.ai.autokpass.presentation.ui.screens.selectEntry.SelectEntryArgs
import com.github.ai.autokpass.presentation.ui.screens.termination.TerminationArgs
import java.io.File

class StartInteractor(
    private val errorInteractor: ErrorInteractor,
    private val argumentParser: ArgumentParser,
    private val printGreetingsUseCase: PrintGreetingsUseCase,
    private val readConfigUseCase: ReadConfigFileUseCase,
    private val strings: StringResources
) {

    fun readArguments(commandLineArguments: Array<String>): Result<ParsedArgs> {
        printGreetingsUseCase.printGreetings()

        val argsResult = CommandLineArgumentExtractor(
            commandLineArguments,
            strings
        ).extractArguments()

        if (argsResult.isFailed()) {
            return argsResult.asErrorOrThrow()
        }

        val fileArgsResult = readConfigUseCase.readConfigArgs()
        if (fileArgsResult.isFailed()) {
            return fileArgsResult.asErrorOrThrow()
        }

        val args = argsResult.getDataOrThrow()
        val fileArgs = fileArgsResult.getDataOrThrow()
        val mergedArgs = when {
            fileArgs != null && args.isEmpty() -> fileArgs
            else -> args
        }

        return argumentParser.validateAndParse(mergedArgs)
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