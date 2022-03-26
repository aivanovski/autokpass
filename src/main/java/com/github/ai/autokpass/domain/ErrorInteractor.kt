package com.github.ai.autokpass.domain

import com.github.ai.autokpass.domain.printer.Printer
import com.github.ai.autokpass.model.Result
import kotlin.system.exitProcess

class ErrorInteractor(
    private val printer: Printer
) {

    fun processAndExit(error: Result.Error): Nothing {
        processAndExit(error.exception)
    }

    fun processAndExit(exception: Exception): Nothing {
        printer.println(exception.toString())
        exitProcess(1)
    }

    fun exit(message: String): Nothing {
        printer.println(message)
        exitProcess(1)
    }
}