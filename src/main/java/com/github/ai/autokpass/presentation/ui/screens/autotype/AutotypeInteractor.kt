package com.github.ai.autokpass.presentation.ui.screens.autotype

import com.github.ai.autokpass.domain.autotype.AutotypeExecutorFactory
import com.github.ai.autokpass.domain.autotype.AutotypeSequenceFactory
import com.github.ai.autokpass.domain.coroutine.Dispatchers
import com.github.ai.autokpass.domain.exception.AutokpassException
import com.github.ai.autokpass.domain.usecases.DetermineAutotypeExecutorTypeUseCase
import com.github.ai.autokpass.domain.usecases.DetermineDesktopUseCase
import com.github.ai.autokpass.domain.usecases.GetOSTypeUseCase
import com.github.ai.autokpass.domain.window.FocusedWindowProvider
import com.github.ai.autokpass.model.AutotypeExecutorType
import com.github.ai.autokpass.model.AutotypePattern
import com.github.ai.autokpass.model.AutotypeState
import com.github.ai.autokpass.model.DesktopType
import com.github.ai.autokpass.model.KeepassEntry
import com.github.ai.autokpass.model.OSType
import com.github.ai.autokpass.model.ParsedConfig
import com.github.ai.autokpass.model.Result
import com.github.ai.autokpass.presentation.ui.core.strings.StringResources
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.withContext

class AutotypeInteractor(
    private val dispatchers: Dispatchers,
    private val focusedWindowProvider: FocusedWindowProvider,
    private val autotypeExecutorFactory: AutotypeExecutorFactory,
    private val sequenceFactory: AutotypeSequenceFactory,
    private val getOSTypeUseCase: GetOSTypeUseCase,
    private val determineExecutorTypeUseCase: DetermineAutotypeExecutorTypeUseCase,
    private val determineDesktopTypeUseCase: DetermineDesktopUseCase,
    private val strings: StringResources
) {

    fun isAbleToAwaitWindowChanged(
        autotypeType: AutotypeExecutorType?
    ): Result<Boolean> {
        val getOsTypeResult = getOSTypeUseCase.getOSType()
        if (getOsTypeResult.isFailed()) {
            return getOsTypeResult.asErrorOrThrow()
        }

        val getDesktopTypeResult = determineDesktopTypeUseCase.getDesktopType()
        if (getDesktopTypeResult.isFailed()) {
            return getDesktopTypeResult.asErrorOrThrow()
        }

        val osType = getOsTypeResult.getDataOrNull()
        val desktopType = getDesktopTypeResult.getDataOrNull()

        val isXorgEnvironment = (osType == OSType.LINUX && desktopType == DesktopType.XORG)
        val isAbleToAwait = (isXorgEnvironment && autotypeType == null) ||
            autotypeType == AutotypeExecutorType.XDOTOOL

        return Result.Success(isAbleToAwait)
    }

    suspend fun awaitWindowFocusChanged(): Result<Unit> =
        withContext(dispatchers.IO) {
            val appWindow = focusedWindowProvider.getFocusedWindow()
                ?: return@withContext Result.Error(
                    AutokpassException(strings.errorFailedToGetWindowName)
                )

            val startTime = System.currentTimeMillis()

            val result: Result<Unit>

            while (true) {
                val currentWindow = focusedWindowProvider.getFocusedWindow()
                if (currentWindow == null) {
                    result = Result.Error(
                        AutokpassException(strings.errorFailedToGetWindowFocus)
                    )
                    break
                }

                if (currentWindow != appWindow) {
                    result = Result.Success(Unit)
                    break
                }

                if (startTime >= System.currentTimeMillis() + AWAIT_TIMEOUT) {
                    result = Result.Error(AutokpassException(strings.errorWindowFocusAwaitTimeout))
                    break
                }

                delay(DELAY_BETWEEN_FOCUS_CHECK)
            }

            result
        }

    suspend fun buildAutotypeFlow(
        appArgs: ParsedConfig,
        entry: KeepassEntry,
        pattern: AutotypePattern,
        startDelayInMillis: Long,
        delayBetweenActionsInMillis: Long
    ): Flow<Result<AutotypeState>> {
        return flow {
            val sequence = sequenceFactory.createAutotypeSequence(
                entry,
                pattern,
                delayBetweenActionsInMillis
            )
            if (sequence == null) {
                emit(Result.Error(AutokpassException(strings.errorFailedToCompileAutotypeSequence)))
                return@flow
            }

            val getExecutorTypeResult = determineExecutorTypeUseCase.getAutotypeExecutorType(
                autotypeFromArgs = appArgs.autotypeType
            )
            if (getExecutorTypeResult.isFailed()) {
                emit(getExecutorTypeResult.asErrorOrThrow())
                return@flow
            }

            var timeLeft = startDelayInMillis
            while (timeLeft > 0) {
                val delay = calculateNextDelayForCountdown(timeLeft)

                emit(
                    Result.Success(
                        AutotypeState.CountDown(
                            secondsLeft = formatTimeForCountdown(timeLeft)
                        )
                    )
                )

                delay(delay)
                timeLeft -= delay
            }

            emit(Result.Success(AutotypeState.Autotyping))

            val executorType = getExecutorTypeResult.getDataOrThrow()
            val autotypeResult = autotypeExecutorFactory.getExecutor(executorType).execute(sequence)
            if (autotypeResult.isFailed()) {
                emit(autotypeResult.asErrorOrThrow())
                return@flow
            }

            emit(Result.Success(AutotypeState.Finished))
        }
            .shareIn(CoroutineScope(dispatchers.IO), SharingStarted.Lazily)
    }

    private fun calculateNextDelayForCountdown(millisLeft: Long): Long {
        return if (millisLeft % ONE_SECOND != 0L) {
            millisLeft % ONE_SECOND
        } else {
            ONE_SECOND
        }
    }

    private fun formatTimeForCountdown(millisLeft: Long): String {
        return if (millisLeft % ONE_SECOND != 0L) {
            (millisLeft.toDouble() / ONE_SECOND.toDouble()).toString()
        } else {
            (millisLeft / 1000L).toString()
        }
    }

    companion object {
        private val DELAY_BETWEEN_FOCUS_CHECK = TimeUnit.MILLISECONDS.toMillis(200)
        private val AWAIT_TIMEOUT = TimeUnit.SECONDS.toMillis(30)
        private val ONE_SECOND = TimeUnit.SECONDS.toMillis(1)
    }
}