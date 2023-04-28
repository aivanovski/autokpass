package com.github.ai.autokpass.domain.autotype

import com.github.ai.autokpass.model.AutotypeSequence
import com.github.ai.autokpass.model.AutotypeSequenceItem
import com.github.ai.autokpass.model.Result
import com.github.ai.autokpass.presentation.process.ProcessExecutor

class XdotoolAutotypeExecutor(
    private val processExecutor: ProcessExecutor,
    private val threadThrottler: ThreadThrottler
) : AutotypeExecutor {

    override fun execute(sequence: AutotypeSequence): Result<Unit> {
        for (item in sequence.items) {
            val itemResult = when (item) {
                is AutotypeSequenceItem.Enter -> {
                    processExecutor.execute("xdotool key enter")
                }

                is AutotypeSequenceItem.Tab -> {
                    processExecutor.execute("xdotool key Tab")
                }

                is AutotypeSequenceItem.Text -> {
                    processExecutor.execute("xdotool type ${item.text}")
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