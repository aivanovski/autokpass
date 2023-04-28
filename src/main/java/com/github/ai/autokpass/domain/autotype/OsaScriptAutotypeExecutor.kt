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
                    processExecutor.executeWithBash(ENTER_COMMAND)
                }

                is AutotypeSequenceItem.Tab -> {
                    processExecutor.executeWithBash(TAB_COMMAND)
                }

                is AutotypeSequenceItem.Text -> {
                    processExecutor.executeWithBash(String.format(TEXT_COMMAND, item.text))
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

    companion object {
        internal const val ENTER_COMMAND =
            """echo "tell application \"System Events\" to key code 36" | osascript"""

        internal const val TAB_COMMAND =
            """echo "tell application \"System Events\" to key code 48" | osascript"""

        internal const val TEXT_COMMAND = "echo \"tell application \\\"System Events\\\" to " +
            "keystroke \\\"%s\\\"\" | osascript"
    }
}