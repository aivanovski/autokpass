package com.github.ai.autokpass.domain.selector

import com.github.ai.autokpass.domain.printer.Printer
import com.github.ai.autokpass.extensions.toIntSafely

class StandardOutputOptionSelector(
    private val printer: Printer
) : OptionSelector {

    override fun show(options: List<String>): Int? {
        options.forEachIndexed { index, entry ->
            printer.println("$index - $entry")
        }

        val minIndex = 0
        val maxIndex = options.size - 1
        printer.println("Please select an entry by entering a number [$minIndex-$maxIndex]:")

        val result = readLine()?.toIntSafely()

        return if (result != null && result in (minIndex..maxIndex)) {
            result
        } else {
            null
        }
    }
}