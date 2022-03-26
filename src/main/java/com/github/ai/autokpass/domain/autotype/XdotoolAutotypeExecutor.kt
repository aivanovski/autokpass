package com.github.ai.autokpass.domain.autotype

import com.github.ai.autokpass.domain.printer.Printer
import com.github.ai.autokpass.domain.process.ProcessExecutor
import com.github.ai.autokpass.model.AutotypeSequence
import com.github.ai.autokpass.model.AutotypeSequenceItem

class XdotoolAutotypeExecutor(
    private val printer: Printer,
    private val processExecutor: ProcessExecutor
) : AutotypeExecutor {

    override fun execute(sequence: AutotypeSequence) {
        printer.println("Autotype will be started after 3 seconds delay")
        Thread.sleep(3000L)

        sequence.items.forEach { item ->
            when (item) {
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
                    Thread.sleep(item.millis)
                }
            }
        }
    }
}