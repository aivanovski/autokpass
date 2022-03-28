package com.github.ai.autokpass.domain

import com.github.ai.autokpass.domain.usecases.AutotypeUseCase
import com.github.ai.autokpass.domain.usecases.ReadPasswordUseCase
import com.github.ai.autokpass.domain.usecases.SelectEntryUseCase
import com.github.ai.autokpass.domain.usecases.SelectPatternUseCase
import com.github.ai.autokpass.model.ParsedArgs

class Interactor(
    private val readPasswordUseCase: ReadPasswordUseCase,
    private val selectEntryUseCase: SelectEntryUseCase,
    private val selectPatternUseCase: SelectPatternUseCase,
    private val autotypeUseCase: AutotypeUseCase,
    private val errorInteractor: ErrorInteractor
) {

    fun run(args: ParsedArgs) {
        val passwordResult = readPasswordUseCase.readPassword()
        if (passwordResult.isFailed()) {
            errorInteractor.processAndExit(passwordResult.getErrorOrThrow())
        }

        val password = passwordResult.getDataOrThrow()

        val selectEntryResult = selectEntryUseCase.selectEntry(password, args)
        if (selectEntryResult.isFailed()) {
            errorInteractor.processAndExit(selectEntryResult.getErrorOrThrow())
        }

        val selectedEntry = selectEntryResult.getDataOrThrow()
            ?: errorInteractor.exit()

        val selectPatternResult = selectPatternUseCase.selectPattern()
        if (selectPatternResult.isFailed()) {
            errorInteractor.processAndExit(selectPatternResult.getErrorOrThrow())
        }

        val selectedPattern = selectPatternResult.getDataOrThrow()
            ?: errorInteractor.exit()

        val autotypeResult = autotypeUseCase.doAutotype(selectedEntry, selectedPattern, args)
        if (autotypeResult.isFailed()) {
            errorInteractor.processAndExit(autotypeResult.getErrorOrThrow())
        }
    }
}