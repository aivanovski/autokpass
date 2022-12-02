package com.github.ai.autokpass.presentation.ui.screens.autotype

import com.github.ai.autokpass.domain.Errors
import com.github.ai.autokpass.domain.autotype.AutotypeExecutorFactory
import com.github.ai.autokpass.domain.autotype.AutotypeSequenceFactory
import com.github.ai.autokpass.domain.coroutine.Dispatchers
import com.github.ai.autokpass.domain.exception.AutokpassException
import com.github.ai.autokpass.domain.usecases.DetermineAutotypeExecutorTypeUseCase
import com.github.ai.autokpass.domain.usecases.GetOSTypeUseCase
import com.github.ai.autokpass.domain.window.FocusedWindowProvider
import com.github.ai.autokpass.model.AutotypeExecutorType
import com.github.ai.autokpass.model.AutotypePattern
import com.github.ai.autokpass.model.AutotypeState
import com.github.ai.autokpass.model.KeepassEntry
import com.github.ai.autokpass.model.OSType
import com.github.ai.autokpass.model.ParsedArgs
import com.github.ai.autokpass.model.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class AutotypeInteractor(
    private val dispatchers: Dispatchers,
    private val focusedWindowProvider: FocusedWindowProvider,
    private val autotypeExecutorFactory: AutotypeExecutorFactory,
    private val sequenceFactory: AutotypeSequenceFactory,
    private val getOSTypeUseCase: GetOSTypeUseCase,
    private val determineExecutorTypeUseCase: DetermineAutotypeExecutorTypeUseCase
) {

    fun isAbleToAwaitWindowChanged(
        autotypeType: AutotypeExecutorType?
    ): Result<Boolean> {
        val getOsTypeResult = getOSTypeUseCase.getOSType()
        if (getOsTypeResult.isFailed()) {
            return getOsTypeResult.asErrorOrThrow()
        }

        val osType = getOsTypeResult.getDataOrNull()

        val isAbleToAwait = (osType == OSType.LINUX && autotypeType == null) ||
            autotypeType == AutotypeExecutorType.XDOTOOL

        return Result.Success(isAbleToAwait)
    }

    suspend fun awaitWindowFocusChanged(): Result<Unit> =
        withContext(dispatchers.IO) {
            val appWindow = focusedWindowProvider.getFocusedWindow()
                ?: return@withContext Result.Error(AutokpassException("Failed to get window name"))

            val startTime = System.currentTimeMillis()

            val result: Result<Unit>

            while (true) {
                val currentWindow = focusedWindowProvider.getFocusedWindow()
                if (currentWindow == null) {
                    result = Result.Error(AutokpassException("Failed to get window focus"))
                    break
                }

                if (currentWindow != appWindow) {
                    result = Result.Success(Unit)
                    break
                }

                if (startTime >= System.currentTimeMillis() + AWAIT_TIMEOUT) {
                    result = Result.Error(AutokpassException("Await timeout"))
                    break
                }

                delay(DELAY_BETWEEN_FOCUS_CHECK)
            }

            result
        }

    suspend fun buildAutotypeFlow(
        appArgs: ParsedArgs,
        entry: KeepassEntry,
        pattern: AutotypePattern,
        delayBetweenActionsInMillis: Long,
        startDelayInSeconds: Long?
    ): Flow<Result<AutotypeState>> {
        return flow {
            val sequence = sequenceFactory.createAutotypeSequence(entry, pattern, delayBetweenActionsInMillis)
            if (sequence == null) {
                emit(Result.Error(AutokpassException(Errors.FAILED_TO_COMPILE_AUTOTYPE_SEQUENCE)))
                return@flow
            }

            val getExecutorTypeResult = determineExecutorTypeUseCase.getAutotypeExecutorType(
                autotypeFromArgs = appArgs.autotypeType
            )
            if (getExecutorTypeResult.isFailed()) {
                emit(getExecutorTypeResult.asErrorOrThrow())
                return@flow
            }

            if (startDelayInSeconds != null) {
                for (seconds in startDelayInSeconds downTo 1) {
                    emit(Result.Success(AutotypeState.CountDown(seconds.toInt())))
                    delay(1000L)
                }
            }

            emit(Result.Success(AutotypeState.Autotyping))

            val executorType = getExecutorTypeResult.getDataOrThrow()
            autotypeExecutorFactory.getExecutor(executorType).execute(sequence)

            emit(Result.Success(AutotypeState.Finished))
        }
            .shareIn(CoroutineScope(dispatchers.IO), SharingStarted.Lazily)
    }

    companion object {
        private val DELAY_BETWEEN_FOCUS_CHECK = TimeUnit.MILLISECONDS.toMillis(200)
        private val AWAIT_TIMEOUT = TimeUnit.SECONDS.toMillis(30)
    }
}