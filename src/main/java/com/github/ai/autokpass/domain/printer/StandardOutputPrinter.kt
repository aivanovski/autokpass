package com.github.ai.autokpass.domain.printer

class StandardOutputPrinter : Printer {

    override fun println(line: String) {
        kotlin.io.println(line)
    }
}