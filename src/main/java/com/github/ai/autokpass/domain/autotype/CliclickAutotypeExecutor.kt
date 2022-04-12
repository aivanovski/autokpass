package com.github.ai.autokpass.domain.autotype

import com.github.ai.autokpass.model.AutotypeSequence
import com.github.ai.autokpass.model.AutotypeSequenceItem
import com.github.ai.autokpass.presentation.process.ProcessExecutor

class CliclickAutotypeExecutor(
    private val processExecutor: ProcessExecutor,
    private val threadThrottler: ThreadThrottler
) : AutotypeExecutor {
    override fun execute(sequence: AutotypeSequence) {
        sequence.items.forEach { item ->
            when (item) {
                is AutotypeSequenceItem.Enter -> {
                    processExecutor.execute("cliclick kp:enter")
                }
                is AutotypeSequenceItem.Tab -> {
                    processExecutor.execute("cliclick kp:tab")
                }
                is AutotypeSequenceItem.Text -> {
                    processExecutor.execute("cliclick t:${item.text}")
                }
                is AutotypeSequenceItem.Delay -> {
                    threadThrottler.sleep(item.millis)
                }
            }
        }
    }
}