package com.github.ai.autokpass.domain

import com.github.ai.autokpass.domain.usecases.AutotypeUseCase
import com.github.ai.autokpass.domain.usecases.PrintAllEntriesUseCase
import com.github.ai.autokpass.model.LaunchMode
import com.github.ai.autokpass.model.ParsedArgs

class Interactor(
    private val printAllUseCase: PrintAllEntriesUseCase,
    private val autotypeUseCase: AutotypeUseCase,
    private val errorInteractor: ErrorInteractor,
) {

    fun run(args: ParsedArgs) {
        val result = when (args.launchMode) {
            LaunchMode.PRINT_ALL -> {
                printAllUseCase.printAllEntries(args)
            }
            LaunchMode.AUTOTYPE -> {
                autotypeUseCase.autotypeValues(args)
            }
        }

        if (result.isFailed()) {
            errorInteractor.processAndExit(result.getErrorOrThrow())
        }
    }
}