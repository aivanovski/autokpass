package com.github.ai.autokpass.domain

import com.github.ai.autokpass.domain.autotype.AutotypeSequenceFactory.Companion.DEFAULT_DELAY_BETWEEN_ACTIONS
import com.github.ai.autokpass.domain.usecases.AutotypeUseCase
import com.github.ai.autokpass.domain.usecases.AwaitWindowChangeUseCase
import com.github.ai.autokpass.domain.usecases.DetermineAutotypeExecutorTypeUseCase
import com.github.ai.autokpass.domain.usecases.GetOSTypeUseCase
import com.github.ai.autokpass.domain.usecases.ProcessKeyUseCase
import com.github.ai.autokpass.domain.usecases.ReadPasswordUseCase
import com.github.ai.autokpass.domain.usecases.SelectEntryUseCase
import com.github.ai.autokpass.domain.usecases.SelectPatternUseCase
import com.github.ai.autokpass.model.AutotypeExecutorType
import com.github.ai.autokpass.model.AutotypePattern
import com.github.ai.autokpass.model.KeepassKey
import com.github.ai.autokpass.model.OSType
import com.github.ai.autokpass.model.ParsedArgs
import java.io.File

class Interactor(
    private val readPasswordUseCase: ReadPasswordUseCase,
    private val processKeyUseCase: ProcessKeyUseCase,
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

        val key = getKey(args) ?: return
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

    private fun getKey(args: ParsedArgs): KeepassKey? {
        return when {
            args.keyPath == null -> {
                val passwordResult = readPasswordUseCase.readPassword(args.inputReaderType, args.filePath)
                if (errorInteractor.processFailed(passwordResult)) {
                    return null
                }

                KeepassKey.PasswordKey(passwordResult.getDataOrThrow())
            }
            args.keyProcessingCommand != null -> {
                val processedKeyResult = processKeyUseCase.processKeyWithCommand(args.keyProcessingCommand, args.keyPath)
                if (errorInteractor.processFailed(processedKeyResult)) {
                    return null
                }

                KeepassKey.PasswordKey(processedKeyResult.getDataOrThrow())
            }
            else -> KeepassKey.FileKey(File(args.keyPath))
        }
    }
}