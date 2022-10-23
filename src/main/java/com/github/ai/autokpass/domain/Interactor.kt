package com.github.ai.autokpass.domain

import com.github.ai.autokpass.domain.autotype.AutotypeSequenceFactory.Companion.DEFAULT_DELAY_BETWEEN_ACTIONS
import com.github.ai.autokpass.domain.usecases.AutotypeUseCase
import com.github.ai.autokpass.domain.usecases.AwaitWindowChangeUseCase
import com.github.ai.autokpass.domain.usecases.DetermineAutotypeExecutorTypeUseCase
import com.github.ai.autokpass.domain.usecases.GetKeyUseCase
import com.github.ai.autokpass.domain.usecases.GetOSTypeUseCase
import com.github.ai.autokpass.domain.usecases.SelectEntryUseCase
import com.github.ai.autokpass.domain.usecases.SelectPatternUseCase
import com.github.ai.autokpass.model.AutotypeExecutorType
import com.github.ai.autokpass.model.AutotypePattern
import com.github.ai.autokpass.model.OSType
import com.github.ai.autokpass.model.ParsedArgs

class Interactor(
    private val getKeyUseCase: GetKeyUseCase,
    private val selectEntryUseCase: SelectEntryUseCase,
    private val selectPatternUseCase: SelectPatternUseCase,
    private val awaitWindowUseCase: AwaitWindowChangeUseCase,
    private val getOsTypeUseCase: GetOSTypeUseCase,
    private val determineAutotypeUseCase: DetermineAutotypeExecutorTypeUseCase,
    private val autotypeUseCase: AutotypeUseCase,
    private val errorInteractor: ErrorInteractor
) {

    fun run(args: ParsedArgs) {
        val getOsTypeResult = getOsTypeUseCase.getOSType()
        if (errorInteractor.processFailed(getOsTypeResult)) {
            return
        }

        val osType = getOsTypeResult.getDataOrNull()
        val autotypeExecutorResult = determineAutotypeUseCase.getAutotypeExecutorType(osType, args.autotypeType)
        if (errorInteractor.processFailed(autotypeExecutorResult)) {
            return
        }

        val getKeyResult = getKeyUseCase.getKey(
            inputReaderType = args.inputReaderType,
            dbFilePath = args.filePath,
            keyPath = args.keyPath,
            keyProcessingCommand = args.keyProcessingCommand
        )
        if (errorInteractor.processFailed(getKeyResult)) {
            return
        }

        val key = getKeyResult.getDataOrThrow()
        val autotypeExecutorType = autotypeExecutorResult.getDataOrThrow()

        val selectEntryResult = selectEntryUseCase.selectEntry(key, args.filePath)
        if (errorInteractor.processFailed(selectEntryResult)) {
            return
        }

        val selectedEntry = selectEntryResult.getDataOrThrow() ?: return

        val selectPatternResult = selectPatternUseCase.selectPattern(AutotypePattern.ALL)
        if (errorInteractor.processFailed(selectPatternResult)) {
            return
        }

        val selectedPattern = selectPatternResult.getDataOrThrow() ?: return

        if ((osType == OSType.LINUX && args.autotypeType == null) || args.autotypeType == AutotypeExecutorType.XDOTOOL) {
            val awaitResult = awaitWindowUseCase.awaitUntilWindowChanged()
            if (errorInteractor.processFailed(awaitResult)) {
                return
            }
        }

        val autotypeResult = autotypeUseCase.doAutotype(
            executorType = autotypeExecutorType,
            entry = selectedEntry,
            pattern = selectedPattern,
            delayBetweenActions = args.autotypeDelayInMillis ?: DEFAULT_DELAY_BETWEEN_ACTIONS,
            startDelayInSeconds = args.delayInSeconds
        )
        errorInteractor.processFailed(autotypeResult)
    }
}