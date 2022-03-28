package com.github.ai.autokpass.presentation.printer

class StandardOutputPrinter : Printer {

    override fun println(line: String) {
        kotlin.io.println(line)
    }
}