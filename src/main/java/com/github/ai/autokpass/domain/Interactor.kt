package com.github.ai.autokpass.domain

import com.github.ai.autokpass.domain.autotype.AutotypeSequenceFactory.Companion.DEFAULT_DELAY_BETWEEN_ACTIONS
import com.github.ai.autokpass.domain.usecases.AutotypeUseCase
import com.github.ai.autokpass.domain.usecases.AwaitWindowChangeUseCase
import com.github.ai.autokpass.domain.usecases.DetermineAutotypeExecutorTypeUseCase
import com.github.ai.autokpass.domain.usecases.GetOSTypeUseCase
import com.github.ai.autokpass.domain.usecases.PrintGreetingsUseCase
import com.github.ai.autokpass.domain.usecases.ProcessKeyUseCase
import com.github.ai.autokpass.domain.usecases.ReadPasswordUseCase
import com.github.ai.autokpass.domain.usecases.SelectEntryUseCase
import com.github.ai.autokpass.domain.usecases.SelectPatternUseCase
import com.github.ai.autokpass.model.AutotypeExecutorType
import com.github.ai.autokpass.model.AutotypePattern
import com.github.ai.autokpass.model.KeepassKey
import com.github.ai.autokpass.model.KeepassKey.FileKey
import com.github.ai.autokpass.model.KeepassKey.XmlFileKey
import com.github.ai.autokpass.model.OSType
import com.github.ai.autokpass.model.ParsedArgs
import com.github.ai.autokpass.model.Result
import java.io.File

class Interactor(
    private val readPasswordUseCase: ReadPasswordUseCase,
    private val greetingsUseCase: PrintGreetingsUseCase,
    private val selectEntryUseCase: SelectEntryUseCase,
    private val selectPatternUseCase: SelectPatternUseCase,
    private val awaitWindowUseCase: AwaitWindowChangeUseCase,
    private val getOsTypeUseCase: GetOSTypeUseCase,
    private val determineAutotypeUseCase: DetermineAutotypeExecutorTypeUseCase,
    private val autotypeUseCase: AutotypeUseCase,
    private val processKeyUseCase: ProcessKeyUseCase,
    private val errorInteractor: ErrorInteractor
) {

    fun run(args: ParsedArgs) {
        val osType = getOsTypeUseCase.getOSType().getDataOrNull()

        val autotypeExecutorResult = determineAutotypeUseCase.getAutotypeExecutorType(osType, args.autotypeType)
        exitIfFailed(autotypeExecutorResult)

        greetingsUseCase.printGreetings()

        val key = getKey(args)
        val autotypeExecutorType = autotypeExecutorResult.getDataOrThrow()

        val selectEntryResult = selectEntryUseCase.selectEntry(key, args.filePath)
        exitIfFailed(selectEntryResult)

        val selectedEntry = selectEntryResult.getDataOrThrow()
            ?: errorInteractor.exit()

        val selectPatternResult = selectPatternUseCase.selectPattern(AutotypePattern.ALL)
        exitIfFailed(selectPatternResult)

        val selectedPattern = selectPatternResult.getDataOrThrow()
            ?: errorInteractor.exit()

        if ((osType == OSType.LINUX && args.autotypeType == null) || args.autotypeType == AutotypeExecutorType.XDOTOOL) {
            val awaitResult = awaitWindowUseCase.awaitUntilWindowChanged()
            if (awaitResult.isFailed()) {
                errorInteractor.processAndExit(awaitResult.asErrorOrThrow())
            }
        }

        val autotypeResult = autotypeUseCase.doAutotype(
            executorType = autotypeExecutorType,
            entry = selectedEntry,
            pattern = selectedPattern,
            delayBetweenActions = args.autotypeDelayInMillis ?: DEFAULT_DELAY_BETWEEN_ACTIONS,
            startDelayInSeconds = args.delayInSeconds
        )
        exitIfFailed(autotypeResult)
    }

    private fun getKey(args: ParsedArgs): KeepassKey {
        return when {
            args.keyPath == null -> {
                val passwordResult = readPasswordUseCase.readPassword(args.filePath)
                exitIfFailed(passwordResult)

                KeepassKey.PasswordKey(passwordResult.getDataOrThrow())
            }
            args.keyProcessingCommand != null -> {
                val processedKeyResult = processKeyUseCase.processKeyWithCommand(args.keyProcessingCommand, args.keyPath)
                exitIfFailed(processedKeyResult)

                KeepassKey.PasswordKey(processedKeyResult.getDataOrThrow())
            }
            args.isXmlKeyFile -> XmlFileKey(File(args.keyPath))
            else -> FileKey(File(args.keyPath))
        }
    }

    private fun exitIfFailed(result: Result<*>) {
        if (result.isFailed()) {
            errorInteractor.processAndExit(result.asErrorOrThrow())
        }
    }
}