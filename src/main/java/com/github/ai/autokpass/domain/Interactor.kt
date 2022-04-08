package com.github.ai.autokpass.domain

import com.github.ai.autokpass.domain.usecases.AutotypeUseCase
import com.github.ai.autokpass.domain.usecases.AwaitWindowChangeUseCase
import com.github.ai.autokpass.domain.usecases.PrintGreetingsUseCase
import com.github.ai.autokpass.domain.usecases.ReadPasswordUseCase
import com.github.ai.autokpass.domain.usecases.SelectEntryUseCase
import com.github.ai.autokpass.domain.usecases.SelectPatternUseCase
import com.github.ai.autokpass.model.KeepassKey
import com.github.ai.autokpass.model.KeepassKey.FileKey
import com.github.ai.autokpass.model.KeepassKey.XmlFileKey
import com.github.ai.autokpass.model.ParsedArgs
import com.github.ai.autokpass.model.Result
import java.io.File

class Interactor(
    private val readPasswordUseCase: ReadPasswordUseCase,
    private val greetingsUseCase: PrintGreetingsUseCase,
    private val selectEntryUseCase: SelectEntryUseCase,
    private val selectPatternUseCase: SelectPatternUseCase,
    private val autotypeUseCase: AutotypeUseCase,
    private val awaitWindowUseCase: AwaitWindowChangeUseCase,
    private val errorInteractor: ErrorInteractor
) {

    fun run(args: ParsedArgs) {
        greetingsUseCase.printGreetings()

        val key = getKey(args)

        val selectEntryResult = selectEntryUseCase.selectEntry(key, args)
        exitIfFailed(selectEntryResult)

        val selectedEntry = selectEntryResult.getDataOrThrow()
            ?: errorInteractor.exit()

        val selectPatternResult = selectPatternUseCase.selectPattern()
        exitIfFailed(selectPatternResult)

        val selectedPattern = selectPatternResult.getDataOrThrow()
            ?: errorInteractor.exit()

        val awaitResult = awaitWindowUseCase.awaitUntilWindowChanged()
        if (awaitResult.isFailed()) {
            errorInteractor.processAndExit(awaitResult.getErrorOrThrow())
        }

        val autotypeResult = autotypeUseCase.doAutotype(selectedEntry, selectedPattern, args.delayInSeconds)
        exitIfFailed(autotypeResult)
    }

    private fun getKey(args: ParsedArgs): KeepassKey {
        return when {
            args.keyPath == null -> {
                val passwordResult = readPasswordUseCase.readPassword(args.filePath)
                exitIfFailed(passwordResult)

                KeepassKey.PasswordKey(passwordResult.getDataOrThrow())
            }
            args.isXmlKeyFile -> XmlFileKey(File(args.keyPath))
            else -> FileKey(File(args.keyPath))
        }
    }

    private fun exitIfFailed(result: Result<*>) {
        if (result.isFailed()) {
            errorInteractor.processAndExit(result.getErrorOrThrow())
        }
    }
}