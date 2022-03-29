package com.github.ai.autokpass.domain

import com.github.ai.autokpass.domain.usecases.AutotypeUseCase
import com.github.ai.autokpass.domain.usecases.ReadPasswordUseCase
import com.github.ai.autokpass.domain.usecases.RunItselfUseCase
import com.github.ai.autokpass.domain.usecases.SelectEntryUseCase
import com.github.ai.autokpass.domain.usecases.SelectPatternUseCase
import com.github.ai.autokpass.model.ParsedArgs
import com.github.ai.autokpass.model.Result

class Interactor(
    private val readPasswordUseCase: ReadPasswordUseCase,
    private val selectEntryUseCase: SelectEntryUseCase,
    private val selectPatternUseCase: SelectPatternUseCase,
    private val autotypeUseCase: AutotypeUseCase,
    private val errorInteractor: ErrorInteractor,
    private val runItselfUseCase: RunItselfUseCase
) {

    fun run(args: ParsedArgs) {
        if (args.autotypeSequence != null) {
            val autotypeResult = autotypeUseCase.doAutotype(args.autotypeSequence, args)
            exitIfFailed(autotypeResult)
            return
        }

        val passwordResult = readPasswordUseCase.readPassword()
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

        val createSequenceResult = autotypeUseCase.createSequence(selectedEntry, selectedPattern)
        exitIfFailed(createSequenceResult)

        val sequence = createSequenceResult.getDataOrThrow()
        if (args.isSingleProcess) {
            val autotypeResult = autotypeUseCase.doAutotype(sequence, args)
            exitIfFailed(autotypeResult)
        } else {
            val runItselfResult = runItselfUseCase.runItself(sequence, args.delayInSeconds)
            exitIfFailed(runItselfResult)
        }
    }

    private fun exitIfFailed(error: Result<*>) {
        if (error.isFailed()) {
            errorInteractor.processAndExit(error.getErrorOrThrow())
        }
    }
}