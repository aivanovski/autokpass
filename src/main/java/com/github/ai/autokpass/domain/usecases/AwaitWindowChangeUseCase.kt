package com.github.ai.autokpass.domain.usecases

import com.github.ai.autokpass.domain.exception.AutokpassException
import com.github.ai.autokpass.domain.window.FocusedWindowProvider
import com.github.ai.autokpass.model.Result
import com.github.ai.autokpass.presentation.printer.Printer
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

class AwaitWindowChangeUseCase(
    private val focusedWindowProvider: FocusedWindowProvider,
    private val printer: Printer
) {

    fun awaitUntilWindowChanged(): Result<Unit> {
        printer.println("Please select window to start autotype")

        val terminalWindow = focusedWindowProvider.getFocusedWindow()
            ?: return Result.Error(AutokpassException("Failed to get window name"))

        val resultRef = AtomicReference<Result<Unit>>()

        Thread {
            val startTime = System.currentTimeMillis()

            while (true) {
                val currentWindow = focusedWindowProvider.getFocusedWindow()
                if (currentWindow == null) {
                    resultRef.set(Result.Error(AutokpassException("Failed to get window name")))
                    break
                }

                if (currentWindow != terminalWindow) {
                    resultRef.set(Result.Success(Unit))
                    break
                }

                if (startTime >= System.currentTimeMillis() + AWAIT_TIMEOUT) {
                    resultRef.set(Result.Error(AutokpassException("Awaiting timeout")))
                    break
                }

                try {
                    Thread.sleep(DELAY_BETWEEN_FOCUS_CHECK)
                } catch (e: InterruptedException) {
                    resultRef.set(Result.Error(e))
                }
            }
        }.apply {
            start()
            join()
        }

        return resultRef.get() ?: Result.Error(AutokpassException("Unknown issue has occurred"))
    }

    companion object {
        private val DELAY_BETWEEN_FOCUS_CHECK = TimeUnit.MILLISECONDS.toMillis(200)
        private val AWAIT_TIMEOUT = TimeUnit.SECONDS.toMillis(30)
    }
}