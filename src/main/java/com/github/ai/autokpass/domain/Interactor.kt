package com.github.ai.autokpass.domain

import com.github.ai.autokpass.domain.usecases.AutotypeUseCase
import com.github.ai.autokpass.domain.usecases.AwaitWindowChangeUseCase
import com.github.ai.autokpass.domain.usecases.ReadPasswordUseCase
import com.github.ai.autokpass.domain.usecases.SelectEntryUseCase
import com.github.ai.autokpass.domain.usecases.SelectPatternUseCase
import com.github.ai.autokpass.model.ParsedArgs
import com.github.ai.autokpass.model.Result

class Interactor(
    private val readPasswordUseCase: ReadPasswordUseCase,
    private val selectEntryUseCase: SelectEntryUseCase,
    private val selectPatternUseCase: SelectPatternUseCase,
    private val autotypeUseCase: AutotypeUseCase,
    private val awaitWindowUseCase: AwaitWindowChangeUseCase,
    private val errorInteractor: ErrorInteractor
) {

    fun run(args: ParsedArgs) {
        val passwordResult = readPasswordUseCase.readPassword(args.filePath)
        exitIfFailed(passwordResult)

        val password = passwordResult.getDataOrThrow()
        val selectEntryResult = selectEntryUseCase.selectEntry(password, args)
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

        val autotypeResult = autotypeUseCase.doAutotype(selectedEntry, selectedPattern, args)
        exitIfFailed(autotypeResult)
    }

    private fun exitIfFailed(result: Result<*>) {
        if (result.isFailed()) {
            errorInteractor.processAndExit(result.getErrorOrThrow())
        }
    }
}