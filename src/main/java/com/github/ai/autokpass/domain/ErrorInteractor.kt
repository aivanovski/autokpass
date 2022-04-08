package com.github.ai.autokpass.domain

import com.github.ai.autokpass.domain.exception.AutokpassException
import com.github.ai.autokpass.presentation.printer.Printer
import com.github.ai.autokpass.model.Result
import kotlin.system.exitProcess

class ErrorInteractor(
    private val printer: Printer
) {

    fun processAndExit(error: Result.Error): Nothing {
        if (error.exception !is AutokpassException) {
            error.exception.printStackTrace()
        }

        printer.println(error.exception.message ?: error.exception.toString())
        exitProcess(1)
    }

    fun exit(): Nothing {
        exitProcess(0)
    }
}