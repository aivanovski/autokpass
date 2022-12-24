package com.github.ai.autokpass.domain.autotype

import com.github.ai.autokpass.model.AutotypeSequence
import com.github.ai.autokpass.model.AutotypeSequenceItem
import com.github.ai.autokpass.model.Result
import com.github.ai.autokpass.presentation.process.ProcessExecutor

class OsaScriptAutotypeExecutor(
    private val processExecutor: ProcessExecutor,
    private val threadThrottler: ThreadThrottler
) : AutotypeExecutor {

    override fun execute(sequence: AutotypeSequence): Result<Unit> {
        for (item in sequence.items) {
            val itemResult = when (item) {
                is AutotypeSequenceItem.Enter -> {
                    processExecutor.executeWithBash(
                        """echo "tell application \"System Events\" to key code 36" | osascript"""
                    )
                }

                is AutotypeSequenceItem.Tab -> {
                    processExecutor.executeWithBash(
                        """echo "tell application \"System Events\" to key code 48" | osascript"""
                    )
                }

                is AutotypeSequenceItem.Text -> {
                    processExecutor.executeWithBash(
                        """echo "tell application \"System Events\" to keystroke \"${item.text}\"" | osascript"""
                    )
                }

                is AutotypeSequenceItem.Delay -> {
                    threadThrottler.sleep(item.millis)
                    Result.Success(Unit)
                }
            }

            if (itemResult.isFailed()) {
                return itemResult.asErrorOrThrow()
            }
        }

        return Result.Success(Unit)
    }
}